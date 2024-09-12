package com.francisconicolau.pruebainditex.application.service;

import com.francisconicolau.pruebainditex.application.dto.CreatePriceRequestDTO;
import com.francisconicolau.pruebainditex.application.dto.PriceDTO;
import com.francisconicolau.pruebainditex.application.exception.CustomException;

import java.util.List;

public interface PriceService {

    PriceDTO findById(int id);

    PriceDTO createNewPrice(CreatePriceRequestDTO pricesDTO);

    void deletePrice(int id);

    PriceDTO updatePrice(int id, CreatePriceRequestDTO priceDTO);

    List<PriceDTO> findAll(String date, String productId, String brandId, boolean orderByPriority) throws CustomException;
}
