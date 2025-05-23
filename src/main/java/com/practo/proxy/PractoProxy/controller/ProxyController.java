package com.practo.proxy.PractoProxy.controller;

import com.practo.proxy.PractoProxy.client.InternalServiceHttpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import okhttp3.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class ProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);
    private final Map<String, InternalServiceHttpClient> serviceClients;

    @Autowired
    public ProxyController(Map<String, InternalServiceHttpClient> serviceClients) {
        this.serviceClients = serviceClients;
    }

    @GetMapping("/{service}/**")
    public ResponseEntity<Object> proxyGet(
            @PathVariable String service,
            @RequestParam(required = false) Map<String, String> queryParams,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        logger.info("Received GET request for service: {}, path: {}", service, request.getRequestURI());
        InternalServiceHttpClient client = serviceClients.get(service);
        if (client == null) {
            logger.warn("No client found for service: {}", service);
            return ResponseEntity.notFound().build();
        }

        String path = getPath(service, request);
        logger.info("Forwarding request to path: {}", path);
        try {
            Response<ResponseBody> response = client.get(path, queryParams).execute();
            String responseBody = response.body() != null
                                   ? response.body().string()
                                   : response.errorBody() != null ? response.errorBody().string() : "";
            
            HttpStatus status = HttpStatus.resolve(response.code());
            if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

            return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(responseBody);
        } catch (Exception e) {
            logger.error("Error processing request", e);
            return ResponseEntity.internalServerError().body("IO Error: " + e.getMessage());
        }
    }

    @PostMapping("/{service}/**")
    public ResponseEntity<Object> proxyPost(
            @PathVariable String service,
            @RequestBody(required = false) Object body,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        InternalServiceHttpClient client = serviceClients.get(service);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }

        String path = getPath(service, request);
        try {
            return ResponseEntity.ok(client.post(path, body).execute().body());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{service}/**")
    public ResponseEntity<Object> proxyPut(
            @PathVariable String service,
            @RequestBody(required = false) Object body,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        InternalServiceHttpClient client = serviceClients.get(service);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }

        String path = getPath(service, request);
        try {
            return ResponseEntity.ok(client.put(path, body).execute().body());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/{service}/**")
    public ResponseEntity<Object> proxyPatch(
            @PathVariable String service,
            @RequestBody(required = false) Object body,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        InternalServiceHttpClient client = serviceClients.get(service);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }

        String path = getPath(service, request);
        try {
            return ResponseEntity.ok(client.patch(path, body).execute().body());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{service}/**")
    public ResponseEntity<Object> proxyDelete(
            @PathVariable String service,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        InternalServiceHttpClient client = serviceClients.get(service);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }

        String path = getPath(service, request);
        try {
            return ResponseEntity.ok(client.delete(path).execute().body());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private String getPath(String service, HttpServletRequest request) {
        String fullPath = request.getRequestURI(); // e.g., /titan/v1/user
        String prefix = "/" + service;
        return fullPath.substring(prefix.length()); // returns /v1/user
    }
} 