package com.autoflex.autoflex.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private Integer stockQuantity;

    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL)
    private List<ProductRawMaterial> products;
}
