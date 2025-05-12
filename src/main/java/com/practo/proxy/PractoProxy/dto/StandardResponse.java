package com.practo.proxy.PractoProxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class StandardResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private Date timestamp;


  public StandardResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.timestamp = new Date();
  }

  public static <T> StandardResponse<T> success(String message, T data) {
    return new StandardResponse<>(true, message, data);
  }

  public static <T> StandardResponse<T> error(String message) {
    return new StandardResponse<>(false, message, null);
  }
}