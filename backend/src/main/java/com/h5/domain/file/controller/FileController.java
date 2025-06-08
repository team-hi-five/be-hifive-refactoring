package com.h5.domain.file.controller;

import com.h5.domain.file.dto.request.FileUploadRequest;
import com.h5.domain.file.dto.response.FileUploadResponse;
import com.h5.domain.file.dto.response.GetFileUrlResponse;
import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.service.FileService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File API", description = "파일 업로드, 조회, 다운로드, 삭제 기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "여러 파일 업로드", description = "파일과 메타데이터를 Multipart/Form-Data 형식으로 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "파일 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultResponse<FileUploadResponse> uploadFiles(
            @RequestPart("file")
            @Parameter(description = "업로드할 파일 목록", required = true)
            List<MultipartFile> multipartFileList,
            @RequestPart("metaData")
            @Parameter(description = "각 파일에 대한 테이블 타입 및 ID 메타데이터", required = true)
            FileUploadRequest metaData) {

        return ResultResponse.created(fileService.upload(multipartFileList, metaData));
    }

    @Operation(summary = "파일 URL 목록 조회", description = "tblType과 tblId를 기준으로 파일 URL 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public ResultResponse<List<GetFileUrlResponse>> getFileUrls(
            @RequestParam
            @Parameter(description = "파일이 속한 테이블 타입", required = true)
            FileEntity.TblType tblType,
            @RequestParam
            @Parameter(description = "파일이 속한 테이블 ID", required = true)
            Integer tblId) {
        return ResultResponse.success(fileService.getFileUrl(tblType, tblId));
    }

    @Operation(summary = "파일 다운로드", description = "지정된 fileId의 파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
    })
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable
            @Parameter(description = "다운로드할 파일의 ID", required = true)
            Integer fileId) {
        Resource resource = fileService.downloadFile(fileId);
        String originalName = fileService.getOriginFileName(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalName + "\"")
                .body(resource);
    }

    @Operation(summary = "파일 삭제", description = "지정된 fileId의 파일을 논리 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
    })
    @DeleteMapping("/{fileId}")
    public ResultResponse<Void> deleteFile(
            @PathVariable
            @Parameter(description = "삭제할 파일의 ID", required = true)
            Integer fileId) {
        fileService.deleteFile(fileId);
        return ResultResponse.success();
    }
}
