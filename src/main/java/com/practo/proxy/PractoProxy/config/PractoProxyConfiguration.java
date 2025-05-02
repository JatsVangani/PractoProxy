package com.practo.proxy.PractoProxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.practo.commons.security.nonce.NoOpNonceStorage;
import com.practo.commons.security.nonce.NonceStorage;

// import java.time.Duration;

@Configuration
public class PractoProxyConfiguration {

    @Bean
    protected ObjectMapper objectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // @Bean
    // protected NonceStorage nonceStorage(CacheManager cacheManager) {
    //     Cache nonceCache = cacheManager.getCache(Nonce.NAME);

    //     return new CacheBasedNonceStorage(nonceCache) {
    //         @Override
    //         public Duration getStoreTtl() {
    //             return Nonce.TTL;
    //         }
    //     };
    // }
    @Bean
    protected NonceStorage nonceStorage() {
        return new NoOpNonceStorage();
    }
}
