package com.ecom.product.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.ecom.product.dto.PresignRequest;
import com.ecom.product.services.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.product.dto.CreateProductRequest;
import com.ecom.product.dto.ProductDto;
import com.ecom.product.dto.UpdateProductRequest;
import com.ecom.product.services.ProductService;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

	private final ProductService productService;
    private final S3Service s3Service;

    @Value("${presign.upload-expiry-minutes}")
    int uploadExpiry;

    @Value("${presign.download-expiry-minutes}")
    int downloadExpiry;

	public ProductController(final ProductService productService, S3Service s3Service) {
		this.productService = productService;
        this.s3Service = s3Service;
    }

	@PostMapping
	public ResponseEntity<ProductDto> addProduct(@RequestBody CreateProductRequest productRequest) {
		ProductDto productDto = productService.createProduct(productRequest);
		return ResponseEntity.status(201).body(productDto);
	}

	@GetMapping
	public ResponseEntity<List<ProductDto>> getProducts() {
		return ResponseEntity.status(200).body(productService.listProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
		return ResponseEntity.status(200).body(productService.getProduct(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductDto> updateProduct(@RequestBody UpdateProductRequest productRequest,
			@PathVariable Long id) {
		return ResponseEntity.status(200).body(productService.updateProduct(id, productRequest));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.status(200).body("Product deleted successfully");
	}

    // 1) Request presigned upload URL
    @PostMapping("/{id}/presign-upload")
    public ResponseEntity<?> presignUpload(@PathVariable Long id, @RequestBody PresignRequest req) throws URISyntaxException {
        // validate product exists & current user can update it
        String filename = Paths.get(req.getFilename()).getFileName().toString();
        String key = String.format("products/%s/%d-%s", id, System.currentTimeMillis(), filename);
        URI url = s3Service.presignUploadUrl(key, Duration.ofMinutes(uploadExpiry), req.getContentType());
        return ResponseEntity.ok(Map.of("key", key, "url", url.toString(), "expiresIn", uploadExpiry * 60));
    }

    // 2) Confirm upload (store key in product record)
    @PostMapping("/{id}/confirm-upload")
    public ResponseEntity<?> confirmUpload(@PathVariable Long id, @RequestBody Map<String,String> body) {
        String key = body.get("key");
        productService.setImageKey(id, key);
        return ResponseEntity.ok(Map.of("message","saved"));
    }

    // 3) Get presigned GET to display image
    @GetMapping("/{id}/image-url")
    public ResponseEntity<?> getImageUrl(@PathVariable Long id) throws URISyntaxException {
        String key = productService.getImageKey(id);
        if (key == null) return ResponseEntity.notFound().build();
        URI url = s3Service.presignDownloadUrl(key, Duration.ofMinutes(downloadExpiry));
        return ResponseEntity.ok(Map.of("url", url.toString(), "expiresIn", downloadExpiry * 60));
    }

}
