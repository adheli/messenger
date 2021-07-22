package com.tavares.messenger.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {
    private String name;
    private String username;
}
