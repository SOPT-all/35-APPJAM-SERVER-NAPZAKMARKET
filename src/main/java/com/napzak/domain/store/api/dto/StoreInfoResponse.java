package com.napzak.domain.store.api.dto;

import java.util.List;

public record StoreInfoResponse(
	Long storeId,
	String storeNickName,
	String storeDescription,
	String storePhoto,
	String storeCover,
	List<GenrePreferenceResponse> genrePreferences
) {
	public static StoreInfoResponse of(
		final Long storeId,
		final String storeNickName,
		final String storeDescription,
		final String storePhoto,
		final String storeCover,
		final List<GenrePreferenceResponse> genrePreferenceList
	) {
		return new StoreInfoResponse(storeId, storeNickName, storeDescription, storePhoto, storeCover,
			genrePreferenceList);
	}
}