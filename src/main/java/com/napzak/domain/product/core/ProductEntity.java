package com.napzak.domain.product.core;


import com.napzak.domain.genre.core.GenreEntity;
import com.napzak.domain.product.core.enums.ProductCondition;
import com.napzak.domain.product.core.enums.TradeStatus;
import com.napzak.domain.product.core.enums.TradeType;
import com.napzak.domain.store.core.StoreEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.napzak.domain.product.core.ProductTableConstants.*;

@Table(name = TABLE_PRODUCT)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity {

    @Id
    @Column(name = COLUMN_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COLUMN_TITLE, nullable = false)
    private String title;

    @Column(name = COLUMN_DESCRIPTION, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_TRADE_TYPE, nullable = false)
    private TradeType tradeType;

    @Column(name = COLUMN_PRICE, nullable = false)
    private int price;

    @Column(name = COLUMN_CREATED_AT, nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = COLUMN_VIEW_COUNT, nullable = false)
    private int viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_TRADE_STATUS, nullable = false)
    private TradeStatus tradeStatus;

    @Column(name = COLUMN_IS_PRICE_NEGOTIABLE, nullable = true)
    private Boolean isPriceNegotiable;

    @Column(name = COLUMN_IS_DELIVERY_INCLUDED, nullable = true)
    private Boolean isDeliveryIncluded;

    @Column(name = COLUMN_STANDARD_DELIVERY_FEE, nullable = true)
    private int standardDeliveryFee;

    @Column(name = COLUMN_HALF_DELIVERY_FEE, nullable = true)
    private int halfDeliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_PRODUCT_CONDITION, nullable = true)
    private ProductCondition productCondition;

    @ManyToOne
    @JoinColumn(name = COLUMN_STORE_ID, nullable = false)
    private StoreEntity store;

    @OneToOne
    @JoinColumn(name = COLUMN_GENRE_ID, nullable = false)
    private GenreEntity genre;

    @Builder
    private ProductEntity(Long id, String title, String description, TradeType tradeType, int price, TradeStatus tradeStatus, Boolean isPriceNegotiable, Boolean isDeliveryIncluded, int standardDeliveryFee, int halfDeliveryFee, ProductCondition productCondition, StoreEntity store, GenreEntity genre
    ){
        this.id = id;
        this.title = title;
        this.description = description;
        this.tradeType = tradeType;
        this.price = price;
        this.tradeStatus = tradeStatus;
        this.isPriceNegotiable = isPriceNegotiable;
        this.isDeliveryIncluded = isDeliveryIncluded;
        this.standardDeliveryFee = standardDeliveryFee;
        this.halfDeliveryFee = halfDeliveryFee;
        this.productCondition = productCondition;
        this.store = store;
        this.genre = genre;
    }


    public static ProductEntity create(
            final String title,
            final StoreEntity store,
            final String description,
            final TradeType tradeType,
            final TradeStatus tradeStatus,
            final int price,
            final Boolean isPriceNegotiable,
            final Boolean isDeliveryIncluded,
            final int standardDeliveryFee,
            final int halfDeliveryFee,
            final ProductCondition productCondition,
            final GenreEntity genre
    ) {
        return ProductEntity.builder()
                .title(title)
                .store(store)
                .description(description)
                .tradeType(tradeType)
                .tradeStatus(tradeStatus)
                .price(price)
                .isPriceNegotiable(isPriceNegotiable)
                .isDeliveryIncluded(isDeliveryIncluded)
                .standardDeliveryFee(standardDeliveryFee)
                .halfDeliveryFee(halfDeliveryFee)
                .productCondition(productCondition)
                .genre(genre)
                .build();
    }


}
