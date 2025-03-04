package com.brokage.service;

import com.brokage.dto.AssetDTO;
import com.brokage.dto.AssetDTOMapper;
import com.brokage.dto.OrderRequestDTOMapper;
import com.brokage.model.Asset;
import com.brokage.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetDTOMapper assetDTOMapper;
    private static final Logger logger = LogManager.getLogger(AssetService.class);

    public List<AssetDTO> listAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId).stream().map(assetDTOMapper).collect(Collectors.toList());
    }

    public void updateUsableSize(Long customerId, String assetName, double amount) {
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new RuntimeException("Asset not found for customer"));

        asset.setUsableSize(asset.getUsableSize() + amount);
        assetRepository.save(asset);
    }

    public boolean hasEnoughAsset(Long customerId, String assetName, double requiredAmount) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .map(asset -> asset.getUsableSize() >= requiredAmount)
                .orElse(false);
    }
}