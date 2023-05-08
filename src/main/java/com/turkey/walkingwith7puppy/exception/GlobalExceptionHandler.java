package com.turkey.walkingwith7puppy.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({RestApiException.class})
	public ResponseEntity<ErrorResponse> handleRestApiException(final RestApiException exception) {

		log.warn("RestApiException occur: ", exception);

		return this.makeErrorResponseEntity(exception.getErrorCode());
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorResponse> handleException(final RestApiException exception) {

		log.warn("Exception occur: ", exception);

		return this.makeErrorResponseEntity(CommonErrorCode.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		final MethodArgumentNotValidException ex,
		final HttpHeaders headers,
		final HttpStatus status,
		final WebRequest request) {

		final List<String> errorList = ex.getBindingResult()
			.getAllErrors()
			.stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.toList());

		log.warn("Invalid Request Parameter errors : {}", errorList);

		return this.makeErrorResponseEntity(errorList.toString());
	}

	private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final ErrorCode errorCode) {

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(new ErrorResponse(errorCode.name(), errorCode.getMessage()));
	}

	private ResponseEntity<Object> makeErrorResponseEntity(final String errorDescription) {

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorDescription));
	}

	@Getter
	@RequiredArgsConstructor
	static class ErrorResponse {

		private final String codeName;
		private final String message;
	}
}