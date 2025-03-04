package com.brokage.repository;

import com.brokage.model.Asset;
import com.brokage.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);
    List<Asset> findByCustomerId(Long customerId);
}