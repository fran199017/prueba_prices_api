package com.francisconicolau.pruebainditex.domain.mappers;


import com.francisconicolau.pruebainditex.application.dto.PriceDTO;
import com.francisconicolau.pruebainditex.domain.model.Price;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    PriceMapper INSTANCE = Mappers.getMapper(PriceMapper.class);

    PriceDTO fromEntity(Price entity);

    List<PriceDTO> fromEntityList(List<Price> entities);
}
