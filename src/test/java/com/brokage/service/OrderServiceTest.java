package com.brokage.service;

import com.brokage.common.OrderSide;
import com.brokage.common.OrderStatus;
import com.brokage.dto.OrderRequestDTO;
import com.brokage.dto.OrderRequestDTOMapper;
import com.brokage.exception.OrderNotFoundException;
import com.brokage.model.*;
import com.brokage.model.Order;
import com.brokage.repository.*;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class OrderServiceTest {
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Mock
    private OrderRequestDTOMapper orderRequestDTOMapper;

    @Autowired
    private OrderService orderService;

    private Order sampleOrder;
    private OrderRequestDTO sampleOrderBuyDTO;
    private OrderRequestDTO sampleOrderSellDTO;
    private OrderRequestDTO sampleTRYOrderBuyDTO;
    private OrderRequestDTO sampleTRYOrderSellDTO;
    LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleOrder = new Order( 1L, "CIMSA", OrderSide.BUY, 100, 3, OrderStatus.PENDING);
        sampleOrderBuyDTO = new OrderRequestDTO( 1L, "CIMSA", OrderSide.BUY, 100, 3);
        sampleOrderSellDTO = new OrderRequestDTO( 1L, "CIMSA", OrderSide.SELL, 100, 3);
        sampleTRYOrderBuyDTO = new OrderRequestDTO( 1L, "TRY", OrderSide.BUY, 1000, 1.0);
        sampleTRYOrderSellDTO = new OrderRequestDTO( 1L, "TRY", OrderSide.SELL, 1000, 1.0);
    }

    @Test
    void testCreateOrder_Success() {
        OrderRequestDTO result = orderService.createOrder(sampleTRYOrderBuyDTO);
        assertNotNull(result);
        assertNotNull(result.getCreateDate());
        assertEquals(result.getStatus(), OrderStatus.PENDING);
    }

    @Test
    void testListOrders_Success() {
        orderService.createOrder(sampleTRYOrderBuyDTO);
        List<OrderRequestDTO> result = orderService.listOrders(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(10));
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getStatus(), OrderStatus.PENDING);
    }

    @Test
    void testGetOrderByCustomerIdAndDateRange_NotFound() {
        assertTrue(orderService.listOrders(1L, startDate, endDate).isEmpty());
    }

    @Test
    void testDeleteOrder_Success() {
        sampleTRYOrderBuyDTO=orderService.createOrder(sampleTRYOrderBuyDTO);
        assertDoesNotThrow(() -> orderService.deleteOrder(sampleTRYOrderBuyDTO.getId()));
    }

    @Test
    void testDeleteOrder_NotFound() {
        assertThrows(OrderNotFoundException.class,() -> orderService.deleteOrder(1L));
    }

    @Test
    void testMatchPendingOrder_OrderNotFound() {
        assertThrows(OrderNotFoundException.class, () -> orderService.matchPendingOrder(1L));
    }

    @Test
    void testMatchPendingOrder_OrderNotPending() {
        sampleOrder.setStatus(OrderStatus.MATCHED);
        sampleOrder=orderRepository.save(sampleOrder);
        assertThrows(IllegalStateException.class, () -> orderService.matchPendingOrder(sampleOrder.getId()));
    }

    @Test
    void testMatchPendingOrder_BuyTRYAsset() {
        sampleTRYOrderBuyDTO=orderService.createOrder(sampleTRYOrderBuyDTO);
        sampleTRYOrderBuyDTO= orderService.matchPendingOrder(sampleTRYOrderBuyDTO.getId());
        assertEquals(OrderStatus.MATCHED, sampleTRYOrderBuyDTO.getStatus());
        Optional<Asset> asset= assetRepository.findByCustomerIdAndAssetName(1L, "TRY");
        assertNotNull(asset);
        assertEquals(asset.get().getAssetName(), "TRY");
        assertEquals(asset.get().getSize(), asset.get().getUsableSize());
        assertEquals(asset.get().getSize(), sampleTRYOrderBuyDTO.getSize());
    }

    @Test
    void testMatchPendingOrder_SellTRYAsset() {
        sampleTRYOrderBuyDTO=orderService.createOrder(sampleTRYOrderBuyDTO);
        sampleTRYOrderBuyDTO=orderService.matchPendingOrder(sampleTRYOrderBuyDTO.getId());
        sampleTRYOrderSellDTO= orderService.createOrder(sampleTRYOrderSellDTO);

        Optional<Asset> asset= assetRepository.findByCustomerIdAndAssetName(1L, "TRY");
        assertNotNull(asset);
        assertEquals(asset.get().getAssetName(), "TRY");
        assertEquals(asset.get().getSize(), 1000);
        assertEquals(asset.get().getUsableSize(), 0);

        sampleTRYOrderSellDTO=orderService.matchPendingOrder(sampleTRYOrderSellDTO.getId());
        assertEquals(OrderStatus.MATCHED, sampleTRYOrderSellDTO.getStatus());

        asset= assetRepository.findByCustomerIdAndAssetName(1L, "TRY");
        assertNotNull(asset);
        assertEquals(asset.get().getAssetName(), "TRY");
        assertEquals(asset.get().getSize(), asset.get().getUsableSize());
        assertEquals(asset.get().getUsableSize(), 0);

    }

    @Test
    void testMatchPendingOrder_BuyOtherAsset() {
        sampleTRYOrderBuyDTO=orderService.createOrder(sampleTRYOrderBuyDTO);
        sampleTRYOrderBuyDTO=orderService.matchPendingOrder(sampleTRYOrderBuyDTO.getId());
        sampleOrderBuyDTO=orderService.createOrder(sampleOrderBuyDTO);

        Optional<Asset> asset= assetRepository.findByCustomerIdAndAssetName(1L, "TRY");
        assertEquals(asset.get().getUsableSize(), asset.get().getSize()-sampleOrderBuyDTO.getSize()*sampleOrderBuyDTO.getPrice());
        assertEquals(asset.get().getSize(), sampleTRYOrderBuyDTO.getSize());

        Optional<Asset> assetCIMSA= assetRepository.findByCustomerIdAndAssetName(1L, "CIMSA");
        assertEquals(assetCIMSA, Optional.empty());

        sampleOrderBuyDTO=orderService.matchPendingOrder(sampleOrderBuyDTO.getId());
        assertEquals(OrderStatus.MATCHED, sampleOrderBuyDTO.getStatus());

        asset= assetRepository.findByCustomerIdAndAssetName(1L, "TRY");
        assertEquals(asset.get().getUsableSize(), asset.get().getSize());

        assetCIMSA= assetRepository.findByCustomerIdAndAssetName(1L, "CIMSA");
        assertNotNull(assetCIMSA);
        assertEquals(assetCIMSA.get().getUsableSize(), assetCIMSA.get().getSize());
        assertEquals(assetCIMSA.get().getSize(), sampleOrderBuyDTO.getSize());

    }

    @Test
    void testMatchPendingOrder_SellOtherAsset() {
        sampleTRYOrderBuyDTO=orderService.createOrder(sampleTRYOrderBuyDTO);
        sampleTRYOrderBuyDTO=orderService.matchPendingOrder(sampleTRYOrderBuyDTO.getId());
        sampleOrderBuyDTO=orderService.createOrder(sampleOrderBuyDTO);
        sampleOrderBuyDTO=orderService.matchPendingOrder(sampleOrderBuyDTO.getId());
        sampleOrderSellDTO=orderService.createOrder(sampleOrderSellDTO);
        Optional<Asset> asset= assetRepository.findByCustomerIdAndAssetName(1L, "TRY");
        assertEquals(asset.get().getSize(), asset.get().getUsableSize());

        Optional<Asset> assetCIMSA= assetRepository.findByCustomerIdAndAssetName(1L, "CIMSA");
        assertEquals(assetCIMSA.get().getUsableSize(), 0);
        assertNotEquals(assetCIMSA.get().getSize(), 0);
        sampleOrderSellDTO=orderService.matchPendingOrder(sampleOrderSellDTO.getId());

        assetCIMSA= assetRepository.findByCustomerIdAndAssetName(1L, "CIMSA");
        assertEquals(assetCIMSA.get().getUsableSize(), 0);
        assertEquals(assetCIMSA.get().getSize(), 0);

        asset= assetRepository.findByCustomerIdAndAssetName(1L, "TRY");
        assertEquals(asset.get().getSize(), asset.get().getUsableSize());
        assertEquals(asset.get().getSize(), 1000);
    }
}