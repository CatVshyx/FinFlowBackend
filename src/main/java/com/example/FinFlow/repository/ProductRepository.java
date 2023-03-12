package com.example.FinFlow.repository;

import com.example.FinFlow.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Integer> {
    Optional<Product> findByProductName(String name);

    @Override
    <S extends Product> S save(S entity);
}
