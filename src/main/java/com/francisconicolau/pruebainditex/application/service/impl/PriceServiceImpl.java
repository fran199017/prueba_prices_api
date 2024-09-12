package com.francisconicolau.pruebainditex.application.service.impl;

import com.francisconicolau.pruebainditex.application.dto.CreatePriceRequestDTO;
import com.francisconicolau.pruebainditex.application.dto.PriceDTO;
import com.francisconicolau.pruebainditex.application.exception.CustomException;
import com.francisconicolau.pruebainditex.application.service.PriceService;
import com.francisconicolau.pruebainditex.domain.mappers.PriceMapper;
import com.francisconicolau.pruebainditex.domain.model.Price;
import com.francisconicolau.pruebainditex.domain.repository.BrandRepository;
import com.francisconicolau.pruebainditex.domain.repository.PriceRepository;
import com.francisconicolau.pruebainditex.infrastructure.config.ServiceProperties;
import com.francisconicolau.pruebainditex.infrastructure.config.ServicePropertyConst;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("pricesService")
public class PriceServiceImpl implements PriceService {

    private static final String DATE_FORMAT = "yyyyMMddHHmmss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    //Filters
    public static final String DOS_PUNTOS = ":";
    public static final String FILTER_EQUALS = "eq";
    public static final String FILTER_NOT_EQUALS = "neq";
    public static final String FILTER_GREATHER_THAN = "gt";
    public static final String FILTER_LESS_THAN = "lt";
    public static final String BETWEEN = "bw";

    //Fields
    public static final String START_DATE = "startDate";
    public static final String PRODUCT_ID = "productId";
    public static final String BRAND_ID = "brandId";
    public static final String END_DATE = "endDate";
    public static final String PRIORITY = "priority";


    @Autowired
    private PriceRepository pricesRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private PriceMapper mapper;

    @Getter
    @Autowired
    private ServiceProperties properties;

    @PersistenceContext
    private EntityManager entityManager;

    public List<PriceDTO> findAll(String date, String productId, String brandId, boolean orderByPriority) throws CustomException {
        var prices = pricesRepository.findAll(getSpecifications(date, productId, brandId, orderByPriority));
        return !prices.isEmpty() ? mapper.fromEntityList(prices) : Collections.emptyList();

    }


    public PriceDTO findById(int id) {
        var pricesOpt = pricesRepository.findById(id);
        if (pricesOpt.isEmpty()) {
            throw new CustomException(ServicePropertyConst.PRECIO_NO_EXISTENTE, properties.getStatusMessage(ServicePropertyConst.PRECIO_NO_EXISTENTE));
        }
        return mapper.fromEntity(pricesOpt.get());
    }

    public PriceDTO createNewPrice(CreatePriceRequestDTO pricesDTO) {
        var brand = brandRepository.findById(pricesDTO.getBrandId());
        if (brand.isPresent()) {
            var startDate = getDateformatted(pricesDTO.getStartDate());
            var endDate = getDateformatted(pricesDTO.getEndDate());

            var price = new Price();
            price.setBrandId(pricesDTO.getBrandId());
            price.setStartDate(startDate);
            price.setEndDate(endDate);
            price.setPriceList(pricesDTO.getPriceList());
            price.setProductId(pricesDTO.getProductId());
            price.setPrecio(pricesDTO.getPrice());
            price.setCurr(pricesDTO.getCurr());
            price.setPriority(pricesDTO.getPriority());

            return mapper.fromEntity(pricesRepository.saveAndFlush(price));
        }
        throw new CustomException(ServicePropertyConst.BRAND_NO_EXISTENTE, properties.getStatusMessage(ServicePropertyConst.BRAND_NO_EXISTENTE));
    }

    public void deletePrice(int id) {
        var priceOpt = pricesRepository.findById(id);
        if (priceOpt.isPresent()) {
            var price = priceOpt.get();
            pricesRepository.delete(price);
        }
        throw new CustomException(ServicePropertyConst.PRECIO_NO_EXISTENTE, properties.getStatusMessage(ServicePropertyConst.PRECIO_NO_EXISTENTE));
    }

    public PriceDTO updatePrice(int id, CreatePriceRequestDTO priceDTO) {

        var brand = brandRepository.findById(priceDTO.getBrandId());
        if (brand.isPresent()) {
            var priceOpt = pricesRepository.findById(id);
            if (priceOpt.isPresent()) {
                var price = priceOpt.get();
                price.setStartDate(getDateformatted(priceDTO.getStartDate()));
                price.setEndDate(getDateformatted(priceDTO.getEndDate()));
                price.setPrecio(priceDTO.getPrice());
                price.setPriceList(priceDTO.getPriceList());
                price.setPriority(priceDTO.getPriority());
                price.setCurr(priceDTO.getCurr());
                price.setBrandId(priceDTO.getBrandId());
                price.setProductId(priceDTO.getProductId());

                return mapper.fromEntity(pricesRepository.saveAndFlush(price));
            }
            throw new CustomException(ServicePropertyConst.PRECIO_NO_EXISTENTE, properties.getStatusMessage(ServicePropertyConst.PRECIO_NO_EXISTENTE));
        }
        throw new CustomException(ServicePropertyConst.BRAND_NO_EXISTENTE, properties.getStatusMessage(ServicePropertyConst.BRAND_NO_EXISTENTE));
    }

    private Specification<Price> getSpecifications(String date, String productId, String brandId, boolean orderByPriority) throws CustomException {
        List<Specification<Price>> specifications = new ArrayList<>();
        if (date != null && !date.isEmpty()) {
            specifications.add(splitFilterAndGetSpec(date, START_DATE));
        }
        if (productId != null && !productId.isEmpty()) {
            specifications.add(splitFilterAndGetSpec(productId, PRODUCT_ID));
        }
        if (brandId != null && !brandId.isEmpty()) {
            specifications.add(splitFilterAndGetSpec(brandId, BRAND_ID));
        }

        // Si la consulta viene ordenada por prioridad filtramos los resultados por prioridad 1.
        if (orderByPriority) {
            return getOrderByPriority(specifications);
        }
        return specifications.stream().reduce(Specification::and).orElse(null);
    }

    /**
     * Método que combina los filtros anteriores para generar una query y filtrar por prioridad a 1 y devolver el resultado, en este caso 1
     * unico elemento.
     *
     * @param specifications Filtros anteriores.
     * @return
     */
    private Specification<Price> getOrderByPriority(List<Specification<Price>> specifications) {
        var combinedSpecification = specifications.stream().reduce(Specification::and).orElse(null);

        return (root, query, cb) -> {
            if (combinedSpecification != null) {
                query.where(combinedSpecification.toPredicate(root, query, cb));
            }
            query.orderBy(cb.desc(root.get(PRIORITY)));
            var queryResult = entityManager.createQuery(query).setMaxResults(1).getResultList();
            return queryResult.isEmpty() ? null : cb.equal(root, queryResult.get(0));
        };
    }

    /**
     * Método que filtra segun los filtros EQUALS, NOT EQUALS, GREATHER THAN y LESS THAN.
     * <p>
     * Si el campo es startDate y el filtro es bw (BETWEEN) haremos una comparacion comprendida entre startDate y endDate
     * y sino, solo compararemos con el startDate dado que el endpoint solo admite 3 parametros.
     *
     * @param fieldValue valor del campo a comparar
     * @param fieldName  Como se llama la variable en la tabla Price
     * @return Specification<Price>
     * @throws CustomException
     */
    private Specification<Price> splitFilterAndGetSpec(String fieldValue, String fieldName) throws CustomException {

        if (fieldValue.contains(DOS_PUNTOS)) {
            var fieldSeparated = fieldValue.split(DOS_PUNTOS);
            var filter = fieldSeparated[0];

            if (START_DATE.equals(fieldName) && BETWEEN.equals(filter)) {
                return compareBetweenFilterForStartDateAndEndDate(fieldSeparated);
            } else {
                var value = START_DATE.equals(fieldName) ? getDateformatted(fieldSeparated[1]) : fieldSeparated[1];
                return switch (filter) {
                    case FILTER_EQUALS -> (Root<Price> root, CriteriaQuery<?> cq, CriteriaBuilder cb) ->
                            cb.equal(root.get(fieldName), value);
                    case FILTER_NOT_EQUALS -> (Root<Price> root, CriteriaQuery<?> cq, CriteriaBuilder cb) ->
                            cb.notEqual(root.get(fieldName), value);
                    case FILTER_GREATHER_THAN -> (Root<Price> root, CriteriaQuery<?> cq, CriteriaBuilder cb) ->
                            cb.greaterThanOrEqualTo(root.get(fieldName), (Comparable) value);
                    case FILTER_LESS_THAN -> (Root<Price> root, CriteriaQuery<?> cq, CriteriaBuilder cb) ->
                            cb.lessThanOrEqualTo(root.get(fieldName), (Comparable) value);
                    default ->
                            throw new CustomException(ServicePropertyConst.FILTRO_NO_ENCONTRADO, properties.getStatusMessage(ServicePropertyConst.FILTRO_NO_ENCONTRADO));
                };
            }
        }
        throw new CustomException(ServicePropertyConst.NO_FILTRO, properties.getStatusMessage(ServicePropertyConst.NO_FILTRO));
    }

    private Specification<Price> compareBetweenFilterForStartDateAndEndDate(String[] fieldSeparated) {
        var value = getDateformatted(fieldSeparated[1]);
        return (Root<Price> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.and(
                        cb.lessThanOrEqualTo(root.get(START_DATE), value),
                        cb.greaterThanOrEqualTo(root.get(END_DATE), value)
                );
    }

    private LocalDateTime getDateformatted(String date) {
        try {
            return LocalDateTime.parse(date, formatter);
        } catch (DateTimeParseException ex) {
            throw new CustomException(ServicePropertyConst.DATE_FORMAT_ERROR, properties.getStatusMessage(ServicePropertyConst.DATE_FORMAT_ERROR));
        }
    }
}
