package com.napzak.domain.store.core.vo;

import com.napzak.domain.store.core.entity.StoreEntity;
import com.napzak.domain.store.core.entity.enums.Role;
import com.napzak.domain.store.core.entity.enums.SocialType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Store {
    private final Long id;
    private final String nickname;
    private final String phoneNumber;
    private final String photo;
    private final Role role;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;
    private final Long socialId;
    private final SocialType socialType;

    public Store(
            Long id,
            String nickname,
            String phoneNumber,
            String photo,
            Role role,
            String description,
            LocalDateTime createdAt,
            LocalDateTime deletedAt,
            Long socialId,
            SocialType socialType
    ) {
        this.id = id;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.role = role;
        this.description = description;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.socialId = socialId;
        this.socialType = socialType;
    }

    public static Store fromEntity(StoreEntity storeEntity) {
        return new Store(
                storeEntity.getId(),
                storeEntity.getNickname(),
                storeEntity.getPhoneNumber(),
                storeEntity.getPhoto(),
                storeEntity.getRole(),
                storeEntity.getDescription(),
                storeEntity.getCreatedAt(),
                storeEntity.getDeletedAt(),
                storeEntity.getSocialId(),
                storeEntity.getSocialType()
        );
    }
}