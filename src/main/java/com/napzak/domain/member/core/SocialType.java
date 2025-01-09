package com.napzak.domain.member.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialType {
	KAKAO("KAKAO"),
	APPLE("APPLE");


	private final String type;
}
