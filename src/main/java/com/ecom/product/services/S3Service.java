package com.ecom.product.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Presigner presigner;
	private final S3Client s3;

	@Value("${app.s3.bucket}")
	private String bucket;

	public URI presignUploadUrl(String key, Duration expiry, String contentType) throws URISyntaxException {
		PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

		PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .putObjectRequest(putReq)
				.signatureDuration(expiry)
                .build();

		PresignedPutObjectRequest presigned = presigner.presignPutObject(presignReq);

		return presigned.url().toURI();
	}

	public URI presignDownloadUrl(String key, Duration expiry) throws URISyntaxException {
		GetObjectRequest getReq = GetObjectRequest.builder().bucket(bucket).key(key).build();

		GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder().getObjectRequest(getReq)
				.signatureDuration(expiry).build();

		PresignedGetObjectRequest presigned = presigner.presignGetObject(presignReq);
		return presigned.url().toURI();
	}

	// Optional server-side upload fallback (backend uploads bytes)
	public void uploadObjectServerSide(String key, byte[] bytes, String contentType) {
		PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
		s3.putObject(request, RequestBody.fromBytes(bytes));
	}
}
