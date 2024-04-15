package com.t3t.apigateway.repository;

import com.t3t.apigateway.entity.Blacklist;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlacklistRepository extends CrudRepository<Blacklist, String> {
}
