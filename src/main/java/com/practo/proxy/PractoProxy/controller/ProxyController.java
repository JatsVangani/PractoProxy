package com.practo.proxy.PractoProxy.controller;

import com.practo.proxy.PractoProxy.client.InternalServiceHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/proxy")
@RequiredArgsConstructor
public class ProxyController {

    private final Map<String, InternalServiceHttpClient> serviceClients;

    @GetMapping("/{service}/**")
    public ResponseEntity<Object> proxyGet(
            @PathVariable String service,
            @RequestParam(required = false) Map<String, String> queryParams,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        InternalServiceHttpClient client = serviceClients.get(service);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }

        String path = getPath(service, request);
        try {
            return ResponseEntity.ok(client.get(path, queryParams).execute().body());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
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
        String fullPath = request.getRequestURI(); // e.g., /proxy/titan/v1/user
        String prefix = "/proxy/" + service;
        return fullPath.substring(prefix.length()); // returns /v1/user
    }
} 