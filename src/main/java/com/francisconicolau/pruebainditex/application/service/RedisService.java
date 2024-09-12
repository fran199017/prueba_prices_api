package com.francisconicolau.pruebainditex.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.francisconicolau.pruebainditex.application.dto.PriceDTO;
import com.francisconicolau.pruebainditex.application.exception.CustomException;
import com.francisconicolau.pruebainditex.infrastructure.config.ServiceProperties;
import com.francisconicolau.pruebainditex.infrastructure.config.ServicePropertyConst;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Getter
    @Autowired
    private ServiceProperties properties;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;



    public RedisService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveRedisCache(String clave, PriceDTO priceDTO) {
        try{
            String priceDto = objectMapper.writeValueAsString(priceDTO);
            redisTemplate.opsForValue().set(clave, priceDto);
        }catch (JsonProcessingException e){
            throw new CustomException(ServicePropertyConst.JSON_ERROR, properties.getStatusMessage(ServicePropertyConst.JSON_ERROR));
        }
    }

    public PriceDTO getRedisCache(String clave) {
        try{
            String priceJSON = redisTemplate.opsForValue().get(clave);
            if (priceJSON != null && !priceJSON.isEmpty()){
                return objectMapper.readValue(priceJSON, PriceDTO.class);
            }
            return null;
        }catch (JsonProcessingException e){
            throw new CustomException(ServicePropertyConst.JSON_ERROR, properties.getStatusMessage(ServicePropertyConst.JSON_ERROR));
        }

    }
}