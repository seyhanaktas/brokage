package com.brokage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    @Schema(nullable = false, description = "How much customer wants to pay for per share", example = "150")
    private Long customerId;
    @Schema(nullable = false, description="Asset Name, use TRY for money", example="CIMSA")
    private String assetName;
    @Schema(nullable = false, description = "Size", example = "150")
    private double size;
    @Schema(nullable = false, description = "Usable size", example = "142")
    private double usableSize;
}