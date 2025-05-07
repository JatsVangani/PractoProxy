package com.practo.proxy.PractoProxy.utility;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutboundLoggingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(OutboundLoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        // Get the final request *after* all interceptors (including Hmac) have run
        Request request = response.request();

        logger.info("➡️ Outbound Request: {} {}", request.method(), request.url());
        logger.debug("➡️ Headers: {}", request.headers());

        String responseBodyString = null;
        if (response.body() != null && response.body().contentLength() < 10000) {
            responseBodyString = response.peekBody(10000).string();
        }

        logger.info("⬅️ Response: {} {} (StatuX: {}) (St: {})", request.method(), request.url(), request.headers(), response.code());
        logger.debug("⬅️ Response Body: {}", responseBodyString);

        
        return response;
    }
}
