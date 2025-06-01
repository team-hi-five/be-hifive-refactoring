package com.h5.domain.asset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class LoadCardResponseDto {

    @Schema(description = "카드 자산 목록")
    private List<CardAssetResponseDto> cardAssetList;
}
