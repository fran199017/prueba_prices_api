package com.francisconicolau.pruebainditex.domain.repository;


import com.francisconicolau.pruebainditex.domain.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Integer>, JpaSpecificationExecutor<Price> {

}
