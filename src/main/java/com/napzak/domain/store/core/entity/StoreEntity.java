package com.napzak.domain.store.core.entity;

import static com.napzak.domain.store.core.entity.StoreTableConstants.*;

import java.time.LocalDateTime;

import com.napzak.domain.store.core.entity.enums.Role;
import com.napzak.domain.store.core.entity.enums.SocialType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = TABLE_STORE, indexes = {@Index(name = "uk1", columnList = "phone", unique = true)})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreEntity {

	@Column(name = COLUMN_CREATED_AT, nullable = false)
	private final LocalDateTime createdAt = LocalDateTime.now();

	@Id
	@Column(name = COLUMN_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = COLUMN_NICKNAME, nullable = true)
	private String nickname;

	@Column(name = COLUMN_PHONE_NUMBER, nullable = true)
	private String phoneNumber;

	@Column(name = COLUMN_PHOTO, nullable = true)
	private String photo;

	@Column(name = COLUMN_COVER, nullable = true)
	private String cover;

	@Enumerated(EnumType.STRING)
	@Column(name = COLUMN_ROLE, nullable = false, columnDefinition = "varchar(10) default 'STORE'")
	private Role role;

	@Column(name = COLUMN_DESCRIPTION, nullable = true, columnDefinition = "varchar(58)")
	private String description;

	@Column(name = COLUMN_DELETED_AT, nullable = true)
	private LocalDateTime deletedAt;

	@Column(name = COLUMN_SOCIAL_ID, nullable = true)
	private Long socialId;

	@Enumerated(EnumType.STRING)
	@Column(name = COLUMN_SOCIAL_TYPE, nullable = true)
	private SocialType socialType;

	@Builder
	private StoreEntity(String nickname, String phoneNumber, Role role, String description, Long socialId,
		SocialType socialType, String photo, String cover) {
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.photo = photo;
		this.cover = cover;
		this.role = role;
		this.description = description;
		this.socialId = socialId;
		this.socialType = socialType;
	}

	public static StoreEntity create(final String nickname, final String phoneNumber, final Role role,
		final String description, final Long socialId, final SocialType socialType, final String photo,
		final String cover) {
		return StoreEntity.builder().
			nickname(nickname).
			phoneNumber(phoneNumber).
			role(role).
			description(description).
			socialId(socialId).
			socialType(socialType).
			photo(photo).
			cover(cover).build();
	}
}
