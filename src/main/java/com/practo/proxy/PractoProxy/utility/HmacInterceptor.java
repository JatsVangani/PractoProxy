package com.practo.proxy.PractoProxy.utility;

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

    private static final String SELF = "reach";

    private final ServiceCredential credential;

    public HmacInterceptor(SecureProperties.ServiceCredential credential) {
        this.credential = credential;
      }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        SignatureGenerator generator = SignatureGenerator.builder()
            .method(HttpMethod.valueOf(request.method()))
            .nonce(Nonce.create().toString())
            .urlPath(request.url().encodedPath())
            .secret(credential.getSecret())
            .build();

        return chain.proceed(chain.request()
            .newBuilder()
            .header(SecurityConstants.AuthHeader.NONCE, generator.getNonce())
            .header(SecurityConstants.AuthHeader.SERVICE, SELF)
            .header(SecurityConstants.AuthHeader.SIGNATURE, generator.generate())
            .header(SecurityConstants.AuthHeader.VERSION, "V4")
            .build());
    }
}