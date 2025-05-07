package com.practo.proxy.PractoProxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.practo.commons.security.nonce.NoOpNonceStorage;
import com.practo.commons.security.nonce.NonceStorage;

@Configuration
public class PractoProxyConfiguration {

    @Bean
    protected NonceStorage nonceStorage() {
        return new NoOpNonceStorage();
    }
}
