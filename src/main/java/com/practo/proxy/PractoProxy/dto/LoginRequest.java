package com.practo.proxy.PractoProxy.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Builder
@Data
public class LoginRequest {
    
    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
