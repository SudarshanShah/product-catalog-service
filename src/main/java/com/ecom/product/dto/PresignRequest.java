package com.ecom.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresignRequest {
    @NotBlank
    private String filename;
    @NotBlank private String contentType;
}

