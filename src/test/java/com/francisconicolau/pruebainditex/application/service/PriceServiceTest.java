package com.francisconicolau.pruebainditex.application.service;

import com.francisconicolau.pruebainditex.application.dto.CreatePriceRequestDTO;
import com.francisconicolau.pruebainditex.application.exception.CustomException;
import com.francisconicolau.pruebainditex.application.service.impl.PriceServiceImpl;
import com.francisconicolau.pruebainditex.infrastructure.config.ServiceProperties;
import com.francisconicolau.pruebainditex.infrastructure.config.ServicePropertyConst;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class PriceServiceTest {
    @Autowired
    private PriceServiceImpl service;

    @Getter
    @Autowired
    private ServiceProperties properties;

    // Define el formato esperado
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    String productId = "eq:35455";
    String brand = "eq:1";

    @Test
    @DisplayName("Test 1: petición a las 10:00 del día 14 del producto 35455 para la brand 1 (ZARA)")
    void test1() throws Exception {
        var date = "bw:20200614100000";

        var actual = service.findAll(date, productId, brand, true);

        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(35455, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(35.5), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-14T00:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59", formatter), actual.get(0).getEndDate());
    }

    @Test
    @DisplayName("Test 2: petición a las 16:00 del día 14 del producto 35455   para la brand 1 (ZARA)")
    void test2() throws Exception {
        var date = "bw:20200614160000";
        var actual = service.findAll(date, productId, brand, true);

        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(35455, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(25.45), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-14T15:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2020-06-14T18:30:00", formatter), actual.get(0).getEndDate());
        assertEquals(1, actual.get(0).getPriority());
    }

    @Test
    @DisplayName("Test 3: petición a las 21:00 del día 14 del producto 35455   para la brand 1 (ZARA)")
    void test3() throws Exception {
        var date = "bw:20200614210000";
        var actual = service.findAll(date, productId, brand, true);

        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(35455, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(35.5), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-14T00:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59", formatter), actual.get(0).getEndDate());
    }

    @Test
    @DisplayName("Test 4: petición a las 10:00 del día 15 del producto 35455   para la brand 1 (ZARA)")
    void test4() throws Exception {
        var date = "bw:20200615100000";
        var actual = service.findAll(date, productId, brand, true);

        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(35455, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(30.5), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-15T00:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2020-06-15T11:00:00", formatter), actual.get(0).getEndDate());
        assertEquals(1, actual.get(0).getPriority());
    }

    @Test
    @DisplayName("Test 5: petición a las 21:00 del día 16 del producto 35455   para la brand 1 (ZARA)")
    void test5() throws Exception {
        var date = "bw:20200616210000";
        var actual = service.findAll(date, productId, brand, true);

        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(35455, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(38.95), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-15T16:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59", formatter), actual.get(0).getEndDate());
        assertEquals(1, actual.get(0).getPriority());
    }

    // OPTIONAL TESTS
    @Test
    @DisplayName("Test 2: petición a las 16:00 del día 14 del producto 35455   para la brand 1 (ZARA) sin prioridad")
    void optional_test2_1_without_priority() throws Exception {
        var date = "bw:20200614160000";
        var actual = service.findAll(date, productId, brand, false);

        assertEquals(2, actual.size());
        //Price 1
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(35455, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(35.5), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-14T00:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59", formatter), actual.get(0).getEndDate());
        assertEquals(0, actual.get(0).getPriority());
        //Price 2
        assertEquals(1, actual.get(1).getBrandId());
        assertEquals(35455, actual.get(1).getProductId());
        assertEquals(BigDecimal.valueOf(25.45), actual.get(1).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-14T15:00:00", formatter), actual.get(1).getStartDate());
        assertEquals(LocalDateTime.parse("2020-06-14T18:30:00", formatter), actual.get(1).getEndDate());
        assertEquals(1, actual.get(1).getPriority());
    }

    @Test
    @DisplayName("Test 4: petición a las 10:00 del día 15 del producto 35455   para la brand 1 (ZARA) sin prioridad")
    void optional_test4_1_without_priority() throws Exception {
        var date = "bw:20200615100000";
        var actual = service.findAll(date, productId, brand, false);

        assertEquals(2, actual.size());
        //Price 1
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(35455, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(35.5), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-14T00:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59", formatter), actual.get(0).getEndDate());
        assertEquals(0, actual.get(0).getPriority());

        //Price 2
        assertEquals(1, actual.get(1).getBrandId());
        assertEquals(35455, actual.get(1).getProductId());
        assertEquals(BigDecimal.valueOf(30.5), actual.get(1).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-15T00:00:00", formatter), actual.get(1).getStartDate());
        assertEquals(LocalDateTime.parse("2020-06-15T11:00:00", formatter), actual.get(1).getEndDate());
        assertEquals(1, actual.get(1).getPriority());
    }

    @Test
    @DisplayName("Test 5: petición a las 21:00 del día 16 del producto 35455   para la brand 1 (ZARA) sin prioridad")
    void optional_test5_1_without_priority() throws Exception {
        var date = "bw:20200616210000";
        var actual = service.findAll(date, productId, brand, false);

        assertEquals(2, actual.size());
        //Price 1
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(35455, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(35.5), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-14T00:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59", formatter), actual.get(0).getEndDate());
        assertEquals(0, actual.get(0).getPriority());

        //Price 2
        assertEquals(1, actual.get(1).getBrandId());
        assertEquals(35455, actual.get(1).getProductId());
        assertEquals(BigDecimal.valueOf(38.95), actual.get(1).getPrice());
        assertEquals(LocalDateTime.parse("2020-06-15T16:00:00", formatter), actual.get(1).getStartDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59", formatter), actual.get(1).getEndDate());
        assertEquals(1, actual.get(1).getPriority());
    }

    @Test
    @DisplayName("Test 6.1: con filtros not equals less than y greather than")
    void optional_test6_1() throws Exception {
        var date = "neq:20210614000000";
        var productId = "gt:35900";
        var brand = "lt:2";
        var actual = service.findAll(date, productId, brand, false);

        assertEquals(1, actual.size());
        //Price 1
        assertEquals(1, actual.get(0).getBrandId());
        assertEquals(36001, actual.get(0).getProductId());
        assertEquals(BigDecimal.valueOf(25.45), actual.get(0).getPrice());
        assertEquals(LocalDateTime.parse("2021-06-14T15:00:00", formatter), actual.get(0).getStartDate());
        assertEquals(LocalDateTime.parse("2021-06-14T18:30:00", formatter), actual.get(0).getEndDate());
        assertEquals(1, actual.get(0).getPriority());
    }

    @Test
    @DisplayName("Test 7: con filtro erroneo")
    void optional_test7_bad_filter() {
        var date = "ac:20210614000000";

        CustomException exception = assertThrows(CustomException.class, () -> service.findAll(date, null, null, false));
        assertEquals(properties.getStatusMessage(ServicePropertyConst.FILTRO_NO_ENCONTRADO), exception.getMessage());
    }

    @Test
    @DisplayName("Test 8: No filtro")
    void optional_test8_no_filter() {
        var date = "20210614000000";
        CustomException exception = assertThrows(CustomException.class, () -> service.findAll(date, null, null, false));
        assertEquals(properties.getStatusMessage(ServicePropertyConst.NO_FILTRO), exception.getMessage());
    }

    @Test
    @DisplayName("Test 8.1: error de formato fecha, dia 32")
    void optional_test8_1_error_date_format() {
        var date = "eq:20210632000000";
        CustomException exception = assertThrows(CustomException.class, () -> service.findAll(date, null, null, false));
        assertEquals(properties.getStatusMessage(ServicePropertyConst.DATE_FORMAT_ERROR), exception.getMessage());
    }

    @Test
    @DisplayName("Test 9: by id OK")
    void get_by_id_ok() {
        var actual = service.findById(1);
        assertEquals(1, actual.getBrandId());
        assertEquals(35455, actual.getProductId());
        assertEquals(BigDecimal.valueOf(35.5), actual.getPrice());
        assertEquals(LocalDateTime.parse("2020-06-14T00:00:00", formatter), actual.getStartDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59", formatter), actual.getEndDate());
    }

    @Test
    @DisplayName("Test 10: by id not found")
    void get_by_id_not_found() {
        CustomException exception = assertThrows(CustomException.class, () -> service.findById(100));
        assertEquals(properties.getStatusMessage(ServicePropertyConst.PRECIO_NO_EXISTENTE), exception.getMessage());
    }

    @Test
    @DisplayName("Test 11: create ok")
    void create_new_price_ok() {
        var dto = getCreatePriceRequestDTO(2);
        var actual = service.createNewPrice(dto);
        assertEquals(2, actual.getBrandId());
        assertEquals(36002, actual.getProductId());
        assertEquals(BigDecimal.valueOf(10.0), actual.getPrice());
        assertEquals(LocalDateTime.parse("2023-01-01T00:00:00", formatter), actual.getStartDate());
        assertEquals(LocalDateTime.parse("2023-01-01T23:59:59", formatter), actual.getEndDate());
    }


    @Test
    @DisplayName("Test 12: create error brand inexistente")
    void create_new_price_error() {
        var dto = getCreatePriceRequestDTO(4);
        var exception = assertThrows(CustomException.class, () -> service.createNewPrice(dto));
        assertEquals(properties.getStatusMessage(ServicePropertyConst.BRAND_NO_EXISTENTE), exception.getMessage());
    }

    @Test
    @DisplayName("Test 13: update ok")
    void update_price_ok() {
        var dto = getCreatePriceRequestDTO(2);
        var actual = service.updatePrice(7, dto);
        assertEquals(2, actual.getBrandId());
        assertEquals(36002, actual.getProductId());
        assertEquals(BigDecimal.valueOf(10.0), actual.getPrice());
        assertEquals(LocalDateTime.parse("2023-01-01T00:00:00", formatter), actual.getStartDate());
        assertEquals(LocalDateTime.parse("2023-01-01T23:59:59", formatter), actual.getEndDate());
    }

    @Test
    @DisplayName("Test 14: update error brand inexistente")
    void update_price_error() {
        var dto = getCreatePriceRequestDTO(4);
        var exception = assertThrows(CustomException.class, () -> service.updatePrice(7, dto));
        assertEquals(properties.getStatusMessage(ServicePropertyConst.BRAND_NO_EXISTENTE), exception.getMessage());
    }

    private static CreatePriceRequestDTO getCreatePriceRequestDTO(int brandId) {
        var dto = CreatePriceRequestDTO
                .builder()
                .price(BigDecimal.valueOf(10.0))
                .brandId(brandId)
                .startDate("20230101000000")
                .endDate("20230101235959")
                .priceList(3)
                .productId(36002)
                .priority(1)
                .curr("EUR")
                .build();
        return dto;
    }


}
