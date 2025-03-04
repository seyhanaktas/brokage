package com.brokage.repository;

import com.brokage.model.Order;
import com.brokage.common.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime start, LocalDateTime end);
    List<Order> findByStatus(OrderStatus status);
}