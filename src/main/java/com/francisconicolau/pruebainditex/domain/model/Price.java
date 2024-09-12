package com.francisconicolau.pruebainditex.domain.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "prices")
public class Price {

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "BRAND_ID", nullable = false)
    int brandId;

    @Column(name = "START_DATE", nullable = false)
    LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    LocalDateTime endDate;

    @Column(name = "PRICE_LIST", nullable = false)
    int priceList;

    @Column(name = "PRODUCT_ID", nullable = false)
    int productId;

    @Column(name = "PRIORITY", nullable = false)
    int priority;

    @Column(name = "PRICE", nullable = false)
    BigDecimal precio;

    @Column(name = "CURR", nullable = false)
    String curr;
}
