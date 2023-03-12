package com.example.FinFlow.repository;

import com.example.FinFlow.model.Company;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompanyRepository extends CrudRepository<Company,Integer> {
    Optional<Company> findByName(String name);
    Optional<Company> findByInviteLink(String code);
}
