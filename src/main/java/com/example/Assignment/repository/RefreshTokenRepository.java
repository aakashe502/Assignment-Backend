package com.example.Assignment.repository;


import com.example.Assignment.models.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken,Integer> {
    Optional<RefreshToken> findByToken(String token);
}