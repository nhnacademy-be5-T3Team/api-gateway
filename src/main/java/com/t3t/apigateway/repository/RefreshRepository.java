package com.t3t.apigateway.repository;

import com.t3t.apigateway.entity.Refresh;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshRepository extends CrudRepository<Refresh, String> {
    Optional<Refresh> findByUuid(String uuid);
}
