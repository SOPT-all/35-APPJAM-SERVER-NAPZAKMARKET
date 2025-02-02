package com.napzak.domain.product.api.dto.request;

import java.util.List;

import com.napzak.domain.product.api.annotation.ValidSequence;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductBuyCreateRequest(
	@NotNull
	@Size(min = 1, max =10, message = "사진은 최소 1개 이상, 최대 10개까지 등록할 수 있습니다.")
	@Valid
	@ValidSequence
	List<ProductPhotoRequestDto> productPhotoList,

	@NotNull
	long genreId,

	@NotBlank
	@Size(max = 50, message = "상품 제목은 50자를 초과할 수 없습니다.")
	String title,

	@NotBlank
	@Size(max = 250, message = "상품 설명은 250자를 초과할 수 없습니다.")
	String description,

	@NotNull
	@Positive(message = "가격은 0보다 커야합니다.")
	int price,

	boolean isPriceNegotiable
) {
}
