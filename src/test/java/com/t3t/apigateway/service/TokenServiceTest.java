package com.t3t.apigateway.service;

import com.t3t.apigateway.entity.Blacklist;
import com.t3t.apigateway.entity.Refresh;
import com.t3t.apigateway.exception.TokenNotExistExceptions;
import com.t3t.apigateway.repository.BlacklistRepository;
import com.t3t.apigateway.repository.RefreshRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private BlacklistRepository blacklistRepository;
    @Mock
    private RefreshRepository refreshRepository;
    @InjectMocks
    private TokenService tokenService;

    @Test
    void getRefreshByUUID_failed() {
        String uuid = "1";
        Mockito.when(refreshRepository.findByUuid(Mockito.any())).thenReturn(Optional.empty());
        Exception e = Assertions.assertThrows(TokenNotExistExceptions.class, () -> tokenService.getRefreshByUUID(uuid));
        Assertions.assertEquals(e.getMessage(), "UUID Match Failed");
    }

    @Test
    void getRefreshByUUID_Success(){
        String uuid = "1";
        Refresh refresh = Refresh.builder().token("r.r.r").uuid("1").build();
        Mockito.when(refreshRepository.findByUuid(Mockito.any())).thenReturn(Optional.of(refresh));

        Refresh newRefresh = tokenService.getRefreshByUUID(uuid);

        Assertions.assertAll(
                () -> Assertions.assertEquals(uuid, newRefresh.getUuid()),
                () -> Assertions.assertEquals("r.r.r", newRefresh.getToken())
        );
    }

    @Test
    void findBlackList_Success() {
        Blacklist token = Blacklist.builder().blackList("a.a.a").build();
        Mockito.when(blacklistRepository.findById(Mockito.anyString())).thenReturn(Optional.of(token));
        Boolean answer = tokenService.findBlackList(token.getBlackList());
        Assertions.assertTrue(answer);
    }

    @Test
    void findBlackList_Failed(){
        Blacklist token = Blacklist.builder().blackList("a.a.a").build();
        Mockito.when(blacklistRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        Boolean answer = tokenService.findBlackList(token.getBlackList());
        Assertions.assertFalse(answer);
    }

    @Test
    void refreshTokenExists_Success() {
        Refresh refresh = Refresh.builder().token("r.r.r").uuid("1").build();
        Mockito.when(refreshRepository.findByUuid(Mockito.anyString())).thenReturn(Optional.of(refresh));
        Boolean answer = tokenService.refreshTokenExists("1");
        Assertions.assertTrue(answer);
    }

    @Test
    void refreshTokenExists_Failed(){
        Refresh refresh = Refresh.builder().token("r.r.r").uuid("1").build();
        Mockito.when(refreshRepository.findByUuid(Mockito.anyString())).thenReturn(Optional.empty());
        Boolean answer = tokenService.refreshTokenExists("1");
        Assertions.assertFalse(answer);
    }
}