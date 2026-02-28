package com.autoflex.autoflex.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private RawMaterial rawMaterial;

    @Column(nullable = false)
    private Integer requiredQuantity;
}
