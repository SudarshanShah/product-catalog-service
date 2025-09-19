package com.ecom.product.dto;

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
public class ProductDto {
	private Long id;
	private String sku;
	private String title;
	private String description;
	private Long price;
	private String currency;
	private String category;
	private String imageKey;
}
