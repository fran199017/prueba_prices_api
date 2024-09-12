package com.francisconicolau.pruebainditex.domain.repository;


import com.francisconicolau.pruebainditex.domain.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

}
