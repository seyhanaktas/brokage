package com.brokage.model;
import com.brokage.common.OrderSide;
import com.brokage.common.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String assetName;

    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;

    private double size;
    private double price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createDate;

    public Order(Long customerId, String assetName, OrderSide orderSide, double size, double price, OrderStatus status) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
        this.status = status;
        this.createDate= LocalDateTime.now();
    }
}