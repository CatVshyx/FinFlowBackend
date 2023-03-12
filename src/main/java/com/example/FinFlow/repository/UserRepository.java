package com.example.FinFlow.repository;

import com.example.FinFlow.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Integer> {
    User findByName(String name);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationCode(String code);
}
