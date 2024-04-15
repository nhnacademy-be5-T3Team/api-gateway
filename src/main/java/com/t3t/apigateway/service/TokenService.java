package com.t3t.apigateway.service;

import com.t3t.apigateway.common.JwtUtils;
import com.t3t.apigateway.entity.Refresh;
import com.t3t.apigateway.exception.TokenNotExistExceptions;
import com.t3t.apigateway.repository.BlacklistRepository;
import com.t3t.apigateway.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TokenService {
    private final BlacklistRepository blacklistRepository;
    private final RefreshRepository refreshRepository;

    public Refresh getRefreshByUUID(String uuid){
        if(refreshRepository.findByUuid(uuid).isEmpty()){
            throw new TokenNotExistExceptions("UUID Match Failed");
        }
        return refreshRepository.findByUuid(uuid).get();
    }

    public Boolean findBlackList(String token){
        return blacklistRepository.findById(token).isPresent();
    }

    public Boolean refreshTokenExists(String uuid){
        return refreshRepository.findByUuid(uuid).isPresent();
    }
}
