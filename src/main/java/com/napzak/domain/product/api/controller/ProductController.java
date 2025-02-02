package com.napzak.domain.product.api.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.napzak.domain.product.api.ProductGenreFacade;
import com.napzak.domain.product.api.ProductInterestFacade;
import com.napzak.domain.product.api.ProductReviewFacade;
import com.napzak.domain.product.api.ProductStoreFacade;
import com.napzak.domain.product.api.dto.request.ProductBuyCreateRequest;
import com.napzak.domain.product.api.dto.request.ProductPhotoRequestDto;
import com.napzak.domain.product.api.dto.request.ProductSellCreateRequest;
import com.napzak.domain.product.api.dto.request.cursor.HighPriceCursor;
import com.napzak.domain.product.api.dto.request.cursor.LowPriceCursor;
import com.napzak.domain.product.api.dto.request.cursor.PopularCursor;
import com.napzak.domain.product.api.dto.request.cursor.RecentCursor;
import com.napzak.domain.product.api.dto.response.ProductBuyListResponse;
import com.napzak.domain.product.api.dto.response.ProductBuyResponse;
import com.napzak.domain.product.api.dto.response.ProductChatResponse;
import com.napzak.domain.product.api.dto.response.ProductDetailDto;
import com.napzak.domain.product.api.dto.response.ProductDetailResponse;
import com.napzak.domain.product.api.dto.response.ProductPhotoDto;
import com.napzak.domain.product.api.dto.response.ProductRecommendListResponse;
import com.napzak.domain.product.api.dto.response.ProductSellListResponse;
import com.napzak.domain.product.api.dto.response.ProductSellResponse;
import com.napzak.domain.product.api.exception.ProductSuccessCode;
import com.napzak.domain.product.api.service.ProductPagination;
import com.napzak.domain.product.api.service.ProductService;
import com.napzak.domain.product.api.service.enums.SortOption;
import com.napzak.domain.product.core.entity.enums.TradeType;
import com.napzak.domain.product.core.vo.Product;
import com.napzak.domain.product.core.vo.ProductPhoto;
import com.napzak.domain.product.core.vo.ProductWithFirstPhoto;
import com.napzak.domain.review.api.dto.response.StoreReviewDto;
import com.napzak.domain.review.core.vo.Review;
import com.napzak.domain.store.api.dto.response.StoreStatusDto;
import com.napzak.global.auth.annotation.CurrentMember;
import com.napzak.global.common.exception.NapzakException;
import com.napzak.global.common.exception.code.ErrorCode;
import com.napzak.global.common.exception.dto.SuccessResponse;
import com.napzak.global.common.util.TimeUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
public class ProductController implements ProductApi {
	private final ProductService productService;

	private final ProductInterestFacade productInterestFacade;
	private final ProductGenreFacade productGenreFacade;
	private final ProductReviewFacade productReviewFacade;
	private final ProductStoreFacade productStoreFacade;

	@Override
	@GetMapping("/sell")
	public ResponseEntity<SuccessResponse<ProductSellListResponse>> getSellProducts(
		@RequestParam(defaultValue = "RECENT") String sortOption,
		@RequestParam(defaultValue = "false") Boolean isOnSale,
		@RequestParam(defaultValue = "false") Boolean isUnopened,
		@RequestParam(required = false) List<Long> genreId,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "100") int size,
		@CurrentMember Long storeId
	) {
		// 1. 요청 파라미터 파싱
		SortOption parsedSortOption = parseSortOption(sortOption);
		CursorValues cursorValues = parseCursorValues(cursor, parsedSortOption);

		// 2. 상품 데이터 조회
		ProductPagination pagination = productService.getSellProducts(
			parsedSortOption,
			cursorValues.getCursorProductId(),
			cursorValues.getCursorOptionalValue(),
			size,
			isOnSale,
			isUnopened,
			genreId
		);

		// 3. 관심, 장르이름 정보 조회
		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, storeId);
		Map<Long, String> genreMap = fetchGenreMap(pagination);

		// 4. 응답 생성
		ProductSellListResponse response = ProductSellListResponse.from(
			parsedSortOption, pagination, interestMap, genreMap, storeId
		);

		// 5. 응답 반환
		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_LIST_RETRIEVE_SUCCESS,
				response
			)
		);
	}

	@Override
	@GetMapping("/buy")
	public ResponseEntity<SuccessResponse<ProductBuyListResponse>> getBuyProducts(
		@RequestParam(defaultValue = "RECENT") String sortOption,
		@RequestParam(defaultValue = "false") Boolean isOnSale,
		@RequestParam(required = false) List<Long> genreId,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "100") int size,
		@CurrentMember Long storeId
	) {
		SortOption parsedSortOption = parseSortOption(sortOption);
		CursorValues cursorValues = parseCursorValues(cursor, parsedSortOption);

		ProductPagination pagination = productService.getBuyProducts(
			parsedSortOption,
			cursorValues.getCursorProductId(),
			cursorValues.getCursorOptionalValue(),
			size,
			isOnSale,
			genreId
		);

		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, storeId);
		Map<Long, String> genreMap = fetchGenreMap(pagination);

		ProductBuyListResponse response = ProductBuyListResponse.from(
			parsedSortOption, pagination, interestMap, genreMap, storeId
		);

		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_LIST_RETRIEVE_SUCCESS,
				response
			)
		);
	}

	@Override
	@GetMapping("/sell/search")
	public ResponseEntity<SuccessResponse<ProductSellListResponse>> searchSellProducts(
		@RequestParam String searchWord,
		@RequestParam(defaultValue = "RECENT") String sortOption,
		@RequestParam(defaultValue = "false") Boolean isOnSale,
		@RequestParam(defaultValue = "false") Boolean isUnopened,
		@RequestParam(required = false) List<Long> genreId,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "100") int size,
		@CurrentMember Long storeId
	) {
		SortOption parsedSortOption = parseSortOption(sortOption);
		CursorValues cursorValues = parseCursorValues(cursor, parsedSortOption);

		ProductPagination pagination = productService.searchSellProducts(
			searchWord,
			parsedSortOption,
			cursorValues.getCursorProductId(),
			cursorValues.getCursorOptionalValue(),
			size,
			isOnSale,
			isUnopened,
			genreId
		);

		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, storeId);
		Map<Long, String> genreMap = fetchGenreMap(pagination);

		ProductSellListResponse response = ProductSellListResponse.from(
			parsedSortOption, pagination, interestMap, genreMap, storeId
		);

		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_LIST_SEARCH_SUCCESS,
				response
			)
		);
	}

	@Override
	@GetMapping("/buy/search")
	public ResponseEntity<SuccessResponse<ProductBuyListResponse>> searchBuyProducts(
		@RequestParam String searchWord,
		@RequestParam(defaultValue = "RECENT") String sortOption,
		@RequestParam(defaultValue = "false") Boolean isOnSale,
		@RequestParam(required = false) List<Long> genreId,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "100") int size,
		@CurrentMember Long storeId
	) {
		SortOption parsedSortOption = parseSortOption(sortOption);
		CursorValues cursorValues = parseCursorValues(cursor, parsedSortOption);

		ProductPagination pagination = productService.searchBuyProducts(
			searchWord,
			parsedSortOption,
			cursorValues.getCursorProductId(),
			cursorValues.getCursorOptionalValue(),
			size,
			isOnSale,
			genreId
		);

		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, storeId);
		Map<Long, String> genreMap = fetchGenreMap(pagination);

		ProductBuyListResponse response = ProductBuyListResponse.from(
			parsedSortOption, pagination, interestMap, genreMap, storeId
		);

		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_LIST_RETRIEVE_SUCCESS,
				response
			)
		);
	}

	@Override
	@GetMapping("sell/stores/{storeOwnerId}")
	public ResponseEntity<SuccessResponse<ProductSellListResponse>> getStoreSellProducts(
		@RequestParam(defaultValue = "RECENT") String sortOption,
		@RequestParam(defaultValue = "false") Boolean isOnSale,
		@RequestParam(defaultValue = "false") Boolean isUnopened,
		@RequestParam(required = false) List<Long> genreId,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "100") int size,
		@PathVariable Long storeOwnerId,
		@CurrentMember Long currentStoreId
	) {
		SortOption parsedSortOption = parseSortOption(sortOption);
		CursorValues cursorValues = parseCursorValues(cursor, parsedSortOption);

		ProductPagination pagination = productService.getStoreSellProducts(
			storeOwnerId,
			parsedSortOption,
			cursorValues.getCursorProductId(),
			cursorValues.getCursorOptionalValue(),
			size,
			isOnSale,
			isUnopened,
			genreId
		);

		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, currentStoreId);
		Map<Long, String> genreMap = fetchGenreMap(pagination);

		ProductSellListResponse response = ProductSellListResponse.from(
			parsedSortOption, pagination, interestMap, genreMap, currentStoreId
		);

		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_LIST_RETRIEVE_SUCCESS,
				response
			)
		);
	}

	@Override
	@GetMapping("/buy/stores/{storeOwnerId}")
	public ResponseEntity<SuccessResponse<ProductBuyListResponse>> getStoreBuyProducts(
		@RequestParam(defaultValue = "RECENT") String sortOption,
		@RequestParam(defaultValue = "false") Boolean isOnSale,
		@RequestParam(required = false) List<Long> genreId,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "100") int size,
		@PathVariable Long storeOwnerId,
		@CurrentMember Long currentStoreId
	) {
		SortOption parsedSortOption = parseSortOption(sortOption);
		CursorValues cursorValues = parseCursorValues(cursor, parsedSortOption);

		ProductPagination pagination = productService.getStoreBuyProducts(
			storeOwnerId,
			parsedSortOption,
			cursorValues.getCursorProductId(),
			cursorValues.getCursorOptionalValue(),
			size,
			isOnSale,
			genreId
		);

		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, currentStoreId);
		Map<Long, String> genreMap = fetchGenreMap(pagination);

		ProductBuyListResponse response = ProductBuyListResponse.from(
			parsedSortOption, pagination, interestMap, genreMap, currentStoreId
		);

		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_LIST_RETRIEVE_SUCCESS,
				response
			)
		);
	}

	@Override
	@PostMapping("/sell")
	public ResponseEntity<SuccessResponse<ProductSellResponse>> createSellProduct(
		@Valid @RequestBody ProductSellCreateRequest productSellCreateRequest,
		@CurrentMember Long storeId
	) {
		Product product = productService.createSellProduct(
			productSellCreateRequest.title(),
			storeId,
			productSellCreateRequest.description(),
			productSellCreateRequest.price(),
			productSellCreateRequest.isDeliveryIncluded(),
			productSellCreateRequest.standardDeliveryFee(),
			productSellCreateRequest.halfDeliveryFee(),
			productSellCreateRequest.productCondition(),
			productSellCreateRequest.genreId()
		);

		Map<Integer, String> photoData = productSellCreateRequest.productPhotoList().stream()
			.collect(Collectors.toMap(
				ProductPhotoRequestDto::sequence,
				ProductPhotoRequestDto::photoUrl,
				(existing, replacement) -> existing,  // 중복 키가 발생하면 기존 값 유지
				LinkedHashMap::new  // 순서 유지
			));

		List<ProductPhoto> productPhotoList = productService.createProductPhotos(product.getId(), photoData);

		ProductSellResponse response = ProductSellResponse.from(product, productPhotoList);

		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_CREATE_SUCCESS,
				response
			)
		);
	}

	@Override
	@PostMapping("/buy")
	public ResponseEntity<SuccessResponse<ProductBuyResponse>> createBuyProduct(
		@Valid @RequestBody ProductBuyCreateRequest productBuyCreateRequest,
		@CurrentMember Long storeId
	) {
		Product product = productService.createBuyProduct(
			productBuyCreateRequest.title(),
			storeId,
			productBuyCreateRequest.description(),
			productBuyCreateRequest.price(),
			productBuyCreateRequest.isPriceNegotiable(),
			productBuyCreateRequest.genreId()
		);

		Map<Integer, String> photoData = productBuyCreateRequest.productPhotoList().stream()
			.collect(Collectors.toMap(
				ProductPhotoRequestDto::sequence,
				ProductPhotoRequestDto::photoUrl,
				(existing, replacement) -> existing,
				LinkedHashMap::new
			));

		List<ProductPhoto> productPhotoList = productService.createProductPhotos(product.getId(), photoData);

		ProductBuyResponse response = ProductBuyResponse.from(product, productPhotoList);

		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_CREATE_SUCCESS,
				response
			)
		);
	}

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<SuccessResponse<ProductDetailResponse>> getDetailProductInfo(
		@PathVariable("productId") Long productId,
		@CurrentMember final Long currentStoreId
	) {

		Product product = productService.getProduct(productId);
		List<Review> reviewList = productReviewFacade.findAllByStoreId(product.getStoreId());

		boolean isInterested = productInterestFacade.getIsInterested(productId, currentStoreId); //좋아요 여부
		String uploadTime = TimeUtils.calculateUploadTime(product.getCreatedAt()); //업로드 시간
		String genreName = productGenreFacade.getGenreName(product.getGenreId()); //장르 이름
		boolean isOwnedByCurrentUser = currentStoreId.equals(product.getStoreId()); //자신의 상품 여부

		//각각의 dto 생성
		ProductDetailDto productDetailDto = ProductDetailDto.from(product, uploadTime, genreName,
			isOwnedByCurrentUser);

		List<ProductPhotoDto> photoDtoList = productService.getProductPhotos(productId).stream()
			.map(ProductPhotoDto::from)
			.toList();

		StoreStatusDto storeStatus = productStoreFacade.findStoreStatusDtoByStoreId(product.getStoreId());

		List<Long> reviewIds = reviewList.stream()
			.map(Review::getId)
			.toList();

		Map<Long, String> reviewerNicknames = productReviewFacade.findReviewerNamesByReviewId(product.getStoreId(),
			reviewIds);

		List<StoreReviewDto> storeReviewDtoList = reviewList.stream()
			.map(review -> {
				String reviewerNickname = reviewerNicknames.get(review.getId());
				return StoreReviewDto.from(review, reviewerNickname, product);
			})
			.toList();

		ProductDetailResponse response = new ProductDetailResponse(isInterested, productDetailDto, photoDtoList,
			storeStatus,
			storeReviewDtoList);

		return ResponseEntity.ok(
			SuccessResponse.of(
				ProductSuccessCode.PRODUCT_DETAIL_SUCCESS,
				response
			)
		);
	}

	@Override
	@GetMapping("/home/recommend")
	public ResponseEntity<SuccessResponse<ProductRecommendListResponse>> getRecommendProducts(
		@CurrentMember Long currentStoreId
	) {
		List<Long> genreIds = productStoreFacade.getGenrePreferenceIds(currentStoreId);

		ProductPagination pagination = productService.getHomeRecommendProducts(currentStoreId, genreIds);

		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, currentStoreId);
		interestMap.putAll(fetchInterestMap(pagination, currentStoreId));

		Map<Long, String> genreMap = fetchGenreMap(pagination);
		genreMap.putAll(fetchGenreMap(pagination));

		ProductRecommendListResponse productListResponse = ProductRecommendListResponse.from(pagination, interestMap,
			genreMap, currentStoreId);

		return ResponseEntity.ok()
			.body(SuccessResponse.of(ProductSuccessCode.RECOMMEND_PRODUCT_GET_SUCCESS, productListResponse));
	}

	@Override
	@GetMapping("/home/sell")
	public ResponseEntity<SuccessResponse<ProductSellListResponse>> getTopSellProducts(
		@RequestParam(defaultValue = "4") int size,
		@CurrentMember Long currentStoreId
	) {

		SortOption parsedSortOption = SortOption.POPULAR;

		ProductPagination pagination = productService.getHomePopularProducts(
			parsedSortOption, size, TradeType.SELL, currentStoreId);

		// 3. 관심, 장르이름 정보 조회
		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, currentStoreId);
		Map<Long, String> genreMap = fetchGenreMap(pagination);

		// 4. 응답 생성
		ProductSellListResponse response = ProductSellListResponse.from(
			parsedSortOption, pagination, interestMap, genreMap, currentStoreId
		);

		return ResponseEntity.ok()
			.body(SuccessResponse.of(ProductSuccessCode.TOP_SELL_PRODUCT_GET_SUCCESS, response));
	}

	@Override
	@GetMapping("/home/buy")
	public ResponseEntity<SuccessResponse<ProductBuyListResponse>> getTopBuyProducts(
		@RequestParam(defaultValue = "4") int size,
		@CurrentMember Long currentStoreId
	) {

		SortOption parsedSortOption = SortOption.POPULAR;

		ProductPagination pagination = productService.getHomePopularProducts(
			parsedSortOption, size, TradeType.BUY, currentStoreId);

		// 3. 관심, 장르이름 정보 조회
		Map<Long, Boolean> interestMap = fetchInterestMap(pagination, currentStoreId);
		Map<Long, String> genreMap = fetchGenreMap(pagination);

		// 4. 응답 생성
		ProductBuyListResponse response = ProductBuyListResponse.from(
			parsedSortOption, pagination, interestMap, genreMap, currentStoreId
		);

		return ResponseEntity.ok()
			.body(SuccessResponse.of(ProductSuccessCode.TOP_BUY_PRODUCT_GET_SUCCESS, response));
	}

	@Override
	@GetMapping("/chat/{productId}")
	public ResponseEntity<SuccessResponse<ProductChatResponse>> getProductChatInfo(
		@PathVariable Long productId,
		@CurrentMember Long storeId
	) {
		ProductWithFirstPhoto product = productService.getProductChatInfo(productId);
		String nickname = productStoreFacade.getStoreNickname(storeId);
		ProductChatResponse productChatResponse = ProductChatResponse.from(product, nickname);

		return ResponseEntity.ok()
			.body(SuccessResponse.of(ProductSuccessCode.PRODUCT_RETRIEVE_SUCCESS, productChatResponse));

	}

	private Map<Long, Boolean> fetchInterestMap(ProductPagination pagination, Long storeId) {
		List<Long> productIds = pagination.getProductList().stream()
			.map(ProductWithFirstPhoto::getId)
			.distinct()
			.toList();
		return productInterestFacade.getIsInterestedMap(productIds, storeId);
	}

	private Map<Long, String> fetchGenreMap(ProductPagination pagination) {
		List<Long> genreIds = pagination.getProductList().stream()
			.map(ProductWithFirstPhoto::getGenreId)
			.distinct()
			.toList();
		return productGenreFacade.getGenreNames(genreIds);
	}

	private SortOption parseSortOption(String sortOption) {
		try {
			return SortOption.valueOf(sortOption.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new NapzakException(ErrorCode.INVALID_SORT_OPTION);
		}
	}

	private CursorValues parseCursorValues(String cursor, SortOption sortOption) {
		if (cursor == null || cursor.isBlank()) {
			return new CursorValues(null, null);
		}

		try {
			switch (sortOption) {
				case RECENT -> {
					Long id = RecentCursor.fromString(cursor).getid();
					return new CursorValues(id, null);
				}
				case POPULAR -> {
					PopularCursor popularCursor = PopularCursor.fromString(cursor);
					return new CursorValues(popularCursor.getId(), popularCursor.getInterestCount());
				}
				case LOW_PRICE -> {
					LowPriceCursor priceCursor = LowPriceCursor.fromString(cursor);
					return new CursorValues(priceCursor.getId(), priceCursor.getPrice());
				}
				case HIGH_PRICE -> {
					HighPriceCursor priceCursor = HighPriceCursor.fromString(cursor);
					return new CursorValues(priceCursor.getId(), priceCursor.getPrice());
				}
				default -> throw new NapzakException(ErrorCode.INVALID_SORT_OPTION);
			}
		} catch (IllegalArgumentException e) {
			throw new NapzakException(ErrorCode.INVALID_CURSOR_FORMAT);
		}
	}

	private static class CursorValues {
		private final Long cursorProductId;
		private final Integer cursorOptionalValue;

		public CursorValues(Long cursorProductId, Integer cursorOptionalValue) {
			this.cursorProductId = cursorProductId;
			this.cursorOptionalValue = cursorOptionalValue;
		}

		public Long getCursorProductId() {
			return cursorProductId;
		}

		public Integer getCursorOptionalValue() {
			return cursorOptionalValue;
		}
	}
}
	
