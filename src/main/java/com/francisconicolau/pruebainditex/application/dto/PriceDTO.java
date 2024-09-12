package com.francisconicolau.pruebainditex.application.dto;


import com.francisconicolau.pruebainditex.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4618939498178014003L;

    Integer productId;

    Integer brandId;

    LocalDateTime startDate;

    LocalDateTime endDate;

    Integer priceList;

    Integer priority;

    BigDecimal price;

    Status status;
}
