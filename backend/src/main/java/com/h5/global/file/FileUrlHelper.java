package com.h5.global.file;

import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.entity.TblType;
import com.h5.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUrlHelper {

    private final FileService fileService;

    public String getProfileUrlOrDefault(TblType tblType, Integer tblId) {
        return !fileService.getFileUrl(tblType, tblId).isEmpty() ? fileService.getFileUrl(tblType, tblId).get(0).getUrl() : "Default Image";
    }
}
