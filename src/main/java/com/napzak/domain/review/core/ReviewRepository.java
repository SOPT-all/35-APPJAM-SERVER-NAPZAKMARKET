package com.napzak.domain.review.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.napzak.domain.review.core.entity.ReviewEntity;

import feign.Param;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

	@Query("""
			SELECT r
			FROM ReviewEntity r
			WHERE r.revieweeId = :storeId
		""")
	List<ReviewEntity> findAllByStoreId(
		@Param("storeId") Long storeId
	);

	@Query("""
		    SELECT r.id, s.nickname
		    FROM ReviewEntity r
		    JOIN StoreEntity s ON r.reviewerId = s.id
		    WHERE r.revieweeId = :storeId
				AND r.id IN :reviewIds
		""")
	List<Object[]> findReviewerNicknamesByStoreIdAndReviewIds(
		@Param("storeId") Long storeId,
		@Param("reviewIds") List<Long> reviewIds
	);

}
