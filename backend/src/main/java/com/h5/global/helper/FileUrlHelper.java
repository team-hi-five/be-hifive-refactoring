package com.h5.global.helper;

import com.h5.domain.file.dto.response.GetFileUrlResponseDto;
import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileUrlHelper {

    private final FileService fileService;

    public String getProfileUrlOrDefault(FileEntity.TblType tblType, Integer tblId) {
        return !fileService.getFileUrl(tblType, tblId).isEmpty() ? fileService.getFileUrl(tblType, tblId).get(0).getUrl() : "Default Image";
    }
}
