package com.brokage.dto;

import com.brokage.model.Asset;
import com.brokage.model.Order;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AssetDTOMapper implements Function<Asset, AssetDTO> {

    @Override
    public AssetDTO apply(Asset asset) {
        return new AssetDTO(
                asset.getCustomerId(),
                asset.getAssetName(),
                asset.getSize(),
                asset.getUsableSize()
        );
    }
}