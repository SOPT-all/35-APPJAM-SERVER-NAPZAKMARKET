package com.napzak.global.common.exception.base;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
	HttpStatus getHttpStatus();

	String getMessage();
}
