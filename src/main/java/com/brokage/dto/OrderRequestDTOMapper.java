package com.brokage.dto;

import com.brokage.model.Order;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class OrderRequestDTOMapper implements Function<Order, OrderRequestDTO> {

    @Override
    public OrderRequestDTO apply(Order order) {
        return new OrderRequestDTO(
                order.getId(),
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide(),
                order.getSize(),
                order.getPrice(),
                order.getStatus(),
                order.getCreateDate()
        );
    }
}
