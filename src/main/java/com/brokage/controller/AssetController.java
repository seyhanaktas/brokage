package com.brokage.controller;

import com.brokage.dto.AssetDTO;
import com.brokage.dto.OrderRequestDTO;
import com.brokage.model.Asset;
import com.brokage.service.AssetService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/{customerId}")
    public ResponseEntity<List<AssetDTO>> listAssets(@PathVariable Long customerId) {
        return ResponseEntity.ok(assetService.listAssets(customerId));
    }
}