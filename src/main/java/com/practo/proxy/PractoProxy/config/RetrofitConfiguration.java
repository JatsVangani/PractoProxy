package com.practo.proxy.PractoProxy.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practo.commons.security.config.SecureProperties;
import com.practo.commons.security.config.SecureProperties.ServiceCredential;
import com.practo.commons.webutils.enums.Subdomain;
import com.practo.commons.webutils.generator.api.UrlGenerator;
import com.practo.proxy.PractoProxy.client.InternalServiceHttpClient;
import com.practo.proxy.PractoProxy.utility.HmacInterceptor;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@RequiredArgsConstructor
public class RetrofitConfiguration {
    
    private static final int TIMEOUT_IN_SECONDS = 10;

    private final ObjectMapper objectMapper;
    private final SecureProperties secureProperties;
    private final UrlGenerator urlGenerator;

    private static final Logger logger = LoggerFactory.getLogger(RetrofitConfiguration.class);

    @Bean
    public Map<String, InternalServiceHttpClient> serviceClients() {
        Map<String, InternalServiceHttpClient> clients = new HashMap<>();
        // Titan Service
        ServiceCredential titanCredential = secureProperties.getServiceCredential("titan");
        logger.info("Titan credentials: {}", titanCredential);
        String titanBaseUrl = urlGenerator.getUrl(Subdomain.titan);
        logger.info("Generated Titan base URL: {}", titanBaseUrl);
        InternalServiceHttpClient titanClient = getHmacRetrofitBuild(titanCredential, titanBaseUrl, TIMEOUT_IN_SECONDS)
                .create(InternalServiceHttpClient.class);
        logger.info("Created Titan client with base URL: {}", titanBaseUrl);
        clients.put("titan", titanClient);

        // Fabric Service
        ServiceCredential fabricCredential = secureProperties.getServiceCredential("fabric");
        String fabricBaseUrl = urlGenerator.getUrl(Subdomain.fabric);
        logger.info("Generated Fabric base URL: {}", fabricBaseUrl);
        clients.put("fabric", getHmacRetrofitBuild(fabricCredential, fabricBaseUrl, TIMEOUT_IN_SECONDS)
                .create(InternalServiceHttpClient.class));

        // Consult Service
        ServiceCredential consultCredential = secureProperties.getServiceCredential("consult");
        String consultBaseUrl = urlGenerator.getUrl(Subdomain.consult);
        logger.info("Generated Consult base URL: {}", consultBaseUrl);
        clients.put("consult", getHmacRetrofitBuild(consultCredential, consultBaseUrl, TIMEOUT_IN_SECONDS)
                .create(InternalServiceHttpClient.class));

        return clients;
    }

    private Retrofit getHmacRetrofitBuild(ServiceCredential serviceCredential, String baseUrl, int readTimeoutInSeconds) {
        Interceptor interceptor = new HmacInterceptor(serviceCredential);

        OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(Duration.ofSeconds(readTimeoutInSeconds))
            .build();
        
        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(httpClient)
            .build();
    }
}
