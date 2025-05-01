package com.utility;

import java.io.IOException;

import com.practo.commons.security.SignatureGenerator;
import com.practo.commons.security.nonce.Nonce;
import com.practo.commons.security.util.SecurityConstants;
import com.practo.commons.security.config.SecureProperties;
import com.practo.commons.security.config.SecureProperties.ServiceCredential;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.springframework.http.HttpMethod;


public class HmacInterceptor implements Interceptor {

    private static final String SELF = "proxy";

    private final ServiceCredential credential;

    public HmacInterceptor(SecureProperties.ServiceCredential credential) {
        this.credential = credential;
      }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        SignatureGenerator signatureGenerator = SignatureGenerator.builder()
            .method(HttpMethod.valueOf(originalRequest.method()))
            .urlPath(originalRequest.url().encodedPath())
            .nonce(Nonce.create().toString())
            .secret(credential.getSecret())
            .build();

        return chain.proceed(chain.request().newBuilder()
            .header(SecurityConstants.AuthHeader.NONCE, signatureGenerator.getNonce())
            .header(SecurityConstants.AuthHeader.SERVICE, SELF)
            .header(SecurityConstants.AuthHeader.SIGNATURE, signatureGenerator.generate())
            .header(SecurityConstants.AuthHeader.VERSION, "V4")
            .build());
    }
}