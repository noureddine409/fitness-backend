package com.metamafitness.fitnessbackend.repository;

import com.metamafitness.fitnessbackend.model.User;

import java.util.Optional;

public interface UserRepository extends GenericRepository<User>{
    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationCode(String verificationCode);

}
