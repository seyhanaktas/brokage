package com.brokage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(Long assetId) {
        super("Asset with ID " + assetId + " not found.");
    }
    public AssetNotFoundException(String message) {
        super(message);
    }
}
