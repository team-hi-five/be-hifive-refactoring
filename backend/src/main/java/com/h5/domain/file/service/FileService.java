package com.h5.domain.file.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.file.dto.request.FileUploadRequest;
import com.h5.domain.file.dto.response.FileResponse;
import com.h5.domain.file.dto.response.FileUploadResponse;
import com.h5.domain.file.dto.response.GetFileUrlResponse;
import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.repository.FileRepository;
import com.h5.global.exception.DomainErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload.root-dir}")
    private String fileUploadRootDir;

    @Value("${file.access.url.prefix}")
    private String fileAccessUrlPrefix;

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * 여러 개의 파일을 업로드하고 메타데이터와 함께 파일 시스템 및 데이터베이스에 저장합니다.
     *
     * @param multipartFileList 업로드할 파일 목록
     * @param metaData          각 파일에 대한 테이블 타입 및 ID 메타데이터
     * @return 업로드된 파일 정보가 포함된 응답 객체
     * @throws BusinessException 파일 목록과 메타데이터 개수가 일치하지 않을 때, 파일 저장에 실패했을 때 발생
     */
    public FileUploadResponse upload(List<MultipartFile> multipartFileList, FileUploadRequest metaData) {
        List<FileEntity.TblType> tblTypes = metaData.getTblType();
        List<Integer> tblIds = metaData.getTblId();
        int count = multipartFileList.size();

        if (count != tblTypes.size() || count != tblIds.size()) {
            throw new BusinessException(DomainErrorCode.INVALID_FILE_INPUT);
        }

        List<FileEntity> entities = IntStream.range(0, count)
                .mapToObj(i -> buildFileEntity(multipartFileList.get(i), tblTypes.get(i), tblIds.get(i)))
                .collect(Collectors.toList());

        List<FileEntity> saved = fileRepository.saveAll(entities);

        List<FileResponse> responses = saved.stream()
                .map(entity -> FileResponse.builder()
                        .filePath(entity.getFilePath())
                        .originFileName(entity.getOriginFileName())
                        .uploadAt(entity.getUploadAt())
                        .tblType(entity.getTblType())
                        .tblId(entity.getTblId())
                        .build())
                .toList();

        return FileUploadResponse.builder()
                .files(responses)
                .build();
    }

    /**
     * 지정된 테이블 타입과 ID에 해당하는 파일 URL 목록을 조회합니다.
     *
     * @param tblType 파일이 속한 테이블 타입
     * @param tblId   파일이 속한 테이블 ID
     * @return 파일 URL 정보 목록
     */
    @Transactional(readOnly = true)
    public List<GetFileUrlResponse> getFileUrl(FileEntity.TblType tblType, Integer tblId) {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("activeFilter");

        List<FileEntity> fileEntities = fileRepository.findAllByTblTypeAndTblId(tblType, tblId);

        String normalizedPrefix = fileAccessUrlPrefix.endsWith("/")
                ? fileAccessUrlPrefix.substring(0, fileAccessUrlPrefix.length() - 1)
                : fileAccessUrlPrefix;

        List<GetFileUrlResponse> responses = new ArrayList<>();
        for (FileEntity fileEntity : fileEntities) {
            String filePath = fileEntity.getFilePath().replace("\\", "/");
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }

            String url = normalizedPrefix + "/" + filePath;

            responses.add(GetFileUrlResponse.builder()
                    .fileId(fileEntity.getId())
                    .url(url)
                    .fileName(fileEntity.getOriginFileName())
                    .build());
        }
        return responses;
    }

    /**
     * 파일 ID로 파일 리소스를 다운로드합니다.
     *
     * @param fileId 다운로드할 파일 엔티티의 ID
     * @return 스프링 Resource 형태의 파일
     * @throws BusinessException 파일이 없거나 로드에 실패했을 때 발생
     */
    @Transactional(readOnly = true)
    public Resource downloadFile(Integer fileId) {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("activeFilter");

        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.FILE_NOT_FOUND));

        return loadFileAsResource(fileEntity.getFilePath());
    }

    /**
     * 파일 ID로 원본 파일명을 조회합니다.
     *
     * @param fileId 조회할 파일 엔티티의 ID
     * @return 원본 파일명
     * @throws BusinessException 파일이 없을 때 발생
     */
    @Transactional(readOnly = true)
    public String getOriginFileName(Integer fileId) {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("activeFilter");

        return fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.FILE_NOT_FOUND))
                .getOriginFileName();
    }

    /**
     * 파일 엔티티를 논리 삭제(삭제 일시 설정)합니다.
     *
     * @param fileId 삭제할 파일 엔티티의 ID
     * @throws BusinessException 파일이 없을 때 발생
     */
    public void deleteFile(Integer fileId) {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("activeFilter");

        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.FILE_NOT_FOUND));

        fileEntity.setDeletedAt(LocalDateTime.now());

        fileRepository.save(fileEntity);
    }

    /**
     * 파일 시스템에서 리소스를 로드하여 반환합니다.
     *
     * @param filePath 데이터베이스에 저장된 상대 경로
     * @return 스프링 Resource 형태의 파일
     * @throws BusinessException 경로가 잘못되었거나 파일에 접근할 수 없을 때 발생
     */
    private Resource loadFileAsResource(String filePath) {
        try {
            Path fileStorageLocation = Paths.get(fileUploadRootDir).toAbsolutePath().normalize();
            Path targetPath = fileStorageLocation.resolve(filePath).normalize();

            if (!targetPath.startsWith(fileStorageLocation)) {
                throw new BusinessException(DomainErrorCode.FILE_NOT_FOUND);
            }

            Resource resource = new UrlResource(targetPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException(DomainErrorCode.FILE_NOT_FOUND);
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new BusinessException(DomainErrorCode.FILE_LOAD_FAILED);
        }
    }

    /**
     * MultipartFile과 메타데이터를 기반으로 FileEntity 객체를 생성합니다.
     *
     * @param file 파일 업로드 데이터
     * @param type 파일이 속한 테이블 타입
     * @param id   파일이 속한 테이블 ID
     * @return 영속화 준비가 완료된 FileEntity 객체
     * @throws BusinessException 파일 저장에 실패했을 때 발생
     */
    private FileEntity buildFileEntity(MultipartFile file, FileEntity.TblType type, Integer id) {
        String originalName = file.getOriginalFilename();
        String uniqueName = UUID.randomUUID().toString().replaceAll("-", "") + "_" + originalName;

        Path targetDir = Paths.get(fileUploadRootDir, type.name(), String.valueOf(id));
        try {
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(uniqueName);
            file.transferTo(target);
        } catch (IOException e) {
            throw new BusinessException(DomainErrorCode.FILE_UPLOAD_FAILED);
        }

        return FileEntity.builder()
                .filePath(Paths.get(type.name(), String.valueOf(id), uniqueName).toString())
                .originFileName(originalName)
                .tblType(type)
                .tblId(id)
                .uploadAt(LocalDateTime.now())
                .build();
    }
}