package com.practo.proxy.PractoProxy.controller;

import com.practo.proxy.PractoProxy.client.InternalServiceHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;


@RestController
// @RequestMapping("")
@RequiredArgsConstructor
public class ProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);
    private final Map<String, InternalServiceHttpClient> serviceClients;
    private final InternalServiceHttpClient titanHttpClient;


    @GetMapping("/enter")
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping("/{service}/**")
    public ResponseEntity<Object> proxyGet(
            @PathVariable String service,
            @RequestParam(required = false) Map<String, String> queryParams,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        logger.info("Received GET request for service: {}, path: {}", service, request.getRequestURI());

        // You might not need the service logic anymore since we're directly using titanHttpClient
        String path = getPath(service, request);
        logger.info("Forwarding request to path: {}", path);
        
        try {
            Response<Object> response = titanHttpClient.get(path, headers, queryParams).execute();

            // Log the response status and body (if successful)
            if (response.isSuccessful()) {
                logger.info("Received successful response from external service: Status = {}, Body = {}", 
                        response.code(), response.body());
            } else {
                logger.warn("Received unsuccessful response: Status = {}, Error Message={}", 
                        response.code(), response.message());
                        System.out.println("JJK error message: " + response.message());
            }
    
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            logger.error("Error processing request", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // @GetMapping("/{service}/**")
    // public ResponseEntity<Object> proxyGet(
    //         @PathVariable String service,
    //         @RequestParam(required = false) Map<String, String> queryParams,
    //         @RequestHeader Map<String, String> headers,
    //         HttpServletRequest request) {
    //     logger.info("Received GET request for service: {}, path: {}", service, request.getRequestURI());
    //     InternalServiceHttpClient client = serviceClients.get(service);
    //     if (client == null) {
    //         logger.warn("No client found for service: {}", service);
    //         return ResponseEntity.notFound().build();
    //     }

    //     String path = getPath(service, request);
    //     logger.info("Forwarding request to path: {}", path);
    //     try {
    //         return ResponseEntity.ok(client.get(path, headers, queryParams).execute().body());
    //     } catch (Exception e) {
    //         logger.error("Error processing request", e);
    //         return ResponseEntity.internalServerError().body(e.getMessage());
    //     }
    // }

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
            return ResponseEntity.ok(client.post(path, headers, body).execute().body());
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
            return ResponseEntity.ok(client.put(path, headers, body).execute().body());
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
            return ResponseEntity.ok(client.patch(path, headers, body).execute().body());
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
            return ResponseEntity.ok(client.delete(path, headers).execute().body());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private String getPath(String service, HttpServletRequest request) {
        String fullPath = request.getRequestURI(); // e.g., /titan/v1/user
        String prefix = "/" + service;
        return fullPath.substring(prefix.length()); // returns /v1/user
        // return "https://latest-titan.practo.com/content/v1/providers/501889a4-9690-4868-bcfe-3a75e4c99482/establishments";
    }
} 