package com.francisconicolau.pruebainditex.infrastructure.web;

import com.francisconicolau.pruebainditex.application.dto.CreatePriceRequestDTO;
import com.francisconicolau.pruebainditex.application.dto.PriceDTO;
import com.francisconicolau.pruebainditex.application.exception.CustomException;
import com.francisconicolau.pruebainditex.application.service.PriceService;
import com.francisconicolau.pruebainditex.application.service.RedisService;
import com.francisconicolau.pruebainditex.domain.model.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class PriceApi {

    private static final Logger log = LoggerFactory.getLogger(PriceApi.class);

    @Autowired
    PriceService priceService;

    @Autowired
    RedisService redisService;

    @GetMapping(value = "prices/findAll")
    @Operation(summary = "Devuelve un listado de precios segun los filtros aplicados eq:, neq:, gt:, lt: bw: (Solo para la fecha)",
            description = " Para cada campo se le pueden aplicar varios filtros : " +
                    "eq:value para filtro EQUALS." +
                    "neq:value para filtro NOT EQUALS. " +
                    "gt:value para filtro GREATHER THAN OR EQUAL. " +
                    "lt:value para filtro LESS THAN OR EQUAL." +
                    "bw:value para filtro BETWEEN (solo para rango de fechas startDate y endDate).")
    public ResponseEntity<List<PriceDTO>> findAll(
            @Parameter(description = "Fecha en formato filtro:yyyyMMddHHmmss si el filtro es bw:yyyyMMddHHmmss buscará entre startDate y endDate, para los demas filtros (solo buscará la startDate)", example = "bw:20200614000000") @RequestParam(required = false) String date,
            @Parameter(description = "Id producto filtro:35455", example = "neq:35456") @RequestParam(required = false) String productId,
            @Parameter(description = "Id brand filtro:1", example = "gt:1") @RequestParam(required = false) String brandId,
            @Parameter(description = "Obtiene el de mayor prioridad si el valor es true, sino omite la prioridad", example = "true") @RequestParam Boolean orderByPriority) {
        try {
            return new ResponseEntity<>(priceService.findAll(date, productId, brandId, orderByPriority), HttpStatus.OK);
        } catch (CustomException e) {
            log.error(e.getMessage(), e);
            var priceDTO = setPriceDTOErrorResponse(e);
            List<PriceDTO> priceDTOList = new ArrayList<>();
            priceDTOList.add(priceDTO);
            return new ResponseEntity<>(priceDTOList, HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping(value = "prices/create", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Crea un nuevo precio")
    public ResponseEntity<PriceDTO> createPrice(@Valid @RequestBody CreatePriceRequestDTO createPriceDTO
    ) {
        try {
            var price = priceService.createNewPrice(createPriceDTO);
            if (price != null) {
                price.setStatus(Status.getSuccessDBStatus());
            }
            return new ResponseEntity<>(price, HttpStatus.OK);
        } catch (CustomException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(setPriceDTOErrorResponse(e), HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping(value = "prices/{id}", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Actualiza un precio existente")
    public ResponseEntity<PriceDTO> updatePrice(@Valid @RequestBody CreatePriceRequestDTO createPriceDTO,
                                                @PathVariable int id) {
        try {
            var price = priceService.updatePrice(id, createPriceDTO);
            if (price != null) {
                price.setStatus(Status.getSuccessDBStatus());
            }
            return new ResponseEntity<>(price, HttpStatus.OK);
        } catch (CustomException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(setPriceDTOErrorResponse(e), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping(value = "prices/{id}")
    @Operation(summary = "Devuelve un precio por su ID")
    public ResponseEntity<PriceDTO> findById(@PathVariable int id) {
        try {

            PriceDTO priceDTO;
            var redisCachePriceDTO = redisService.getRedisCache(String.valueOf(id));
            if (redisCachePriceDTO != null) {
                redisCachePriceDTO.setStatus(Status.getSuccessRedisStatus());
                return new ResponseEntity<>(redisCachePriceDTO, HttpStatus.OK);
            }

            priceDTO = priceService.findById(id);
            if (priceDTO != null) {
                priceDTO.setStatus(Status.getSuccessDBStatus());
                redisService.saveRedisCache(String.valueOf(id), priceDTO);
            }
            return new ResponseEntity<>(priceDTO, HttpStatus.OK);
        } catch (CustomException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(setPriceDTOErrorResponse(e), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "prices/{id}")
    @Operation(summary = "Borra un precio por su ID")
    public ResponseEntity<Void> deletePrice(@PathVariable int id) {
        try {
            priceService.deletePrice(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private static PriceDTO setPriceDTOErrorResponse(CustomException e) {
        var price = new PriceDTO();
        price.setStatus(new Status(e.getStatusExceptionCode(), e.getMessage()));
        return price;
    }


}
