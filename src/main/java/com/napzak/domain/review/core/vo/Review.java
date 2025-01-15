package com.napzak.domain.review.core.vo;

import com.napzak.domain.product.core.vo.Product;
import com.napzak.domain.review.core.entity.ReviewEntity;
import com.napzak.domain.store.core.vo.Store;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Review {
    private final Long id;
    private final Integer rate;
    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Product product;
    private final Store reviewer;
    private final Store reviewee;

    public Review(Long id, Integer rate, String comment, LocalDateTime createdAt, LocalDateTime updatedAt, Product product, Store reviewer, Store reviewee) {
        this.id = id;
        this.rate = rate;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.product = product;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
    }

    public static Review fromEntity(ReviewEntity reviewEntity){
        return new Review(
                reviewEntity.getId(),
                reviewEntity.getRate(),
                reviewEntity.getComment(),
                reviewEntity.getCreatedAt(),
                reviewEntity.getUpdatedAt(),
                Product.fromEntity(reviewEntity.getProductEntity()),
                Store.fromEntity(reviewEntity.getReviewer()),
                Store.fromEntity(reviewEntity.getReviewee())
        );
    }
}
