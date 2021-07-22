package com.tavares.messenger.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Response<T> {
    private String code;
    private String message;
    private T data;
}
