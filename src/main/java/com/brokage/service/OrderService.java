package com.brokage.service;

import com.brokage.common.*;
import com.brokage.dto.OrderRequestDTO;
import com.brokage.dto.OrderRequestDTOMapper;
import com.brokage.exception.OrderNotFoundException;
import com.brokage.model.*;
import com.brokage.repository.*;
import lombok.*;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final OrderRequestDTOMapper orderRequestDTOMapper;
    private static final Logger logger = LogManager.getLogger(OrderService.class);

    public OrderRequestDTO getOrderById(Long orderId){
        logger.debug("Match order {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return orderRequestDTOMapper.apply(order);
    }
    public OrderRequestDTO createOrder(OrderRequestDTO request) {
        logger.debug(request);
        logger.debug("Fetch asset for the customer: {} & asset: {}", request.getCustomerId(),request.getAssetName());

        if (request.getOrderSide().equals(OrderSide.BUY)) {
            if(!request.getAssetName().equals("TRY")) {
                Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(request.getCustomerId(), "TRY")
                        .orElseThrow(() -> new IllegalArgumentException("TRY asset not found"));

                double requiredAmount = request.getSize() * request.getPrice();
                if (tryAsset.getUsableSize() < requiredAmount) {
                    throw new IllegalArgumentException("Insufficient funds");
                }
                tryAsset.setUsableSize(tryAsset.getUsableSize() - (int) requiredAmount);
                assetRepository.save(tryAsset);
            }
        }else{
            Asset asset = assetRepository.findByCustomerIdAndAssetName(request.getCustomerId(), request.getAssetName())
                    .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
            if (asset.getUsableSize() < request.getSize()) {
                throw new IllegalArgumentException("Not enough shares available");
            }
            asset.setUsableSize(asset.getUsableSize() - request.getSize());
            assetRepository.save(asset);
        }
        Order order = new Order(null, request.getCustomerId(), request.getAssetName(), request.getOrderSide(),
                request.getSize(), request.getPrice(), OrderStatus.PENDING, LocalDateTime.now());

        return orderRequestDTOMapper.apply(orderRepository.save(order)) ;
    }

    public List<OrderRequestDTO> listOrders(Long customerId, LocalDateTime start, LocalDateTime end) {
        logger.debug("Fetch orders for the customer: {} between {} & {}", customerId,start,end);
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, start, end)
                .stream().map(orderRequestDTOMapper).collect(Collectors.toList());
    }
    public void deleteOrder(Long orderId) {
        logger.debug("Delete order {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be canceled");
        }
        if(!order.getAssetName().equals("TRY")){
            Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY")
                    .orElseThrow(() -> new IllegalArgumentException("TRY asset not found"));
            if(order.getOrderSide().equals(OrderSide.BUY)){
                double requiredAmount = order.getSize() * order.getPrice();
                tryAsset.setUsableSize(tryAsset.getUsableSize()+requiredAmount);
            }
        }
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    public OrderRequestDTO matchPendingOrder(Long orderId) {
        logger.debug("Match order {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be canceled");
        }
        if(order.getAssetName().equals("TRY")){
            if(order.getOrderSide().equals(OrderSide.BUY)){
                buyTRYAsset(order);
            }else{
                sellTRYAsset(order);
            }
        }else{
            if(order.getOrderSide().equals(OrderSide.BUY)){
                buyAsset(order);
            }else{
                sellAsset(order);
            }
        }

        order.setStatus(OrderStatus.MATCHED);
        return orderRequestDTOMapper.apply( orderRepository.save(order));
    }

    private void buyTRYAsset(Order order){
        Asset asset = assetRepository.findByCustomerIdAndAssetName(
                        order.getCustomerId(), order.getAssetName())
                .orElse(null);
        if(asset==null){
            asset = new Asset(null, order.getCustomerId(), order.getAssetName(), order.getSize(), order.getSize());
        }else{
            asset.setSize(asset.getSize()+order.getSize());
            asset.setUsableSize(asset.getUsableSize()+order.getSize());
        }
        assetRepository.save(asset);
    }
    private void sellTRYAsset(Order order){
        Asset asset = assetRepository.findByCustomerIdAndAssetName(
                        order.getCustomerId(), order.getAssetName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer does not have asset: "+ order.getAssetName()
                ));

        asset.setSize(asset.getSize()-order.getSize());
        assetRepository.save(asset);
    }
    private void buyAsset(Order order){
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY")
                .orElseThrow(() -> new IllegalArgumentException("TRY asset not found"));

        Asset asset = assetRepository.findByCustomerIdAndAssetName(
                        order.getCustomerId(), order.getAssetName())
                .orElse(null);
        if(asset==null){
            asset=new Asset(null,order.getCustomerId(), order.getAssetName(), order.getSize(), order.getSize());
        }else{
            asset.setSize(asset.getSize()+order.getSize());
            asset.setUsableSize(asset.getUsableSize()+order.getSize());
        }
        double requiredAmount = order.getSize() * order.getPrice();
        tryAsset.setSize(tryAsset.getSize()-requiredAmount);
        assetRepository.save(tryAsset);
        assetRepository.save(asset);
    }
    private void sellAsset(Order order){
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY")
                .orElseThrow(() -> new IllegalArgumentException("TRY asset not found"));
        Asset asset = assetRepository.findByCustomerIdAndAssetName(
                        order.getCustomerId(), order.getAssetName())
                .orElseThrow(() -> new IllegalArgumentException(order.getAssetName()+" asset not found"));

        asset.setSize(asset.getSize()-order.getSize());
        double requiredAmount = order.getSize() * order.getPrice();
        tryAsset.setSize(tryAsset.getSize()+requiredAmount);
        tryAsset.setUsableSize(tryAsset.getUsableSize()+requiredAmount);
        assetRepository.save(tryAsset);
        assetRepository.save(asset);
    }
}