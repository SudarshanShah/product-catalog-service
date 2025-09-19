package com.ecom.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

	@NotBlank
	private String sku;

	@NotBlank
	private String title;

	private String description;

	@NotNull
	@Min(value = 0)
	private Long price;

	@NotBlank
	private String currency;

	@NotBlank
	private String category;
}
