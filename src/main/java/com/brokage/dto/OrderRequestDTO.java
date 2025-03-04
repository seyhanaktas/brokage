package com.brokage.dto;

import com.brokage.common.OrderSide;
import com.brokage.common.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(nullable = false, description="Customer Id as a numeric value", example="1234")
    private Long customerId;
    @Schema(nullable = false, description="Asset Name, use TRY for money", example="CIMSA")
    private String assetName;
    @Schema(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;
    @Schema(nullable = false, description = "How many shares customer wants to buy?", example = "3")
    private double size;
    @Schema(nullable = false, description = "How much customer wants to pay for per share. For TRY order, price value will be ignored, it will always be 1.", example = "150")
    private double price;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createDate;

    public OrderRequestDTO(Long customerId, String assetName, OrderSide orderSide, double size, double price) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
    }
}