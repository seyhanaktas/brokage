package com.brokage.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String assetName;
    private double size;
    private double usableSize;  // Usable shares available for transactions
}