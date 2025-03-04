package com.brokage.controller;

import com.brokage.dto.OrderRequestDTO;
import com.brokage.model.Order;
import com.brokage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderRequestDTO> createOrder(@RequestBody OrderRequestDTO request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PostMapping("/match")
    public ResponseEntity<Void> matchOrder(@RequestBody Long orderId) {
        orderService.matchPendingOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderRequestDTO>> listOrders(
            @RequestParam Long customerId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(orderService.listOrders(customerId, start, end));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}