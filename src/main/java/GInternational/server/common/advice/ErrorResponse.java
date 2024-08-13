package GInternational.server.common.advice;

import GInternational.server.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {

    private int status; // HTTP 상태 코드
    private String message; // 오류 메시지
    private List<FieldError> fieldErrorList; // 필드 유효성 검사 오류 목록
    private List<ConstraintViolationError> violationErrors; // 제약 조건 위반 오류 목록

    /**
     * 기본 오류 응답 생성자.
     * @param status HTTP 상태 코드
     * @param message 오류 메시지
     */
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * 필드 오류와 제약 조건 위반 오류를 포함하는 오류 응답 생성자.
     * @param fieldErrorList 필드 오류 목록
     * @param violationErrors 제약 조건 위반 오류 목록
     */
    private ErrorResponse(List<FieldError> fieldErrorList, List<ConstraintViolationError> violationErrors) {
        this.fieldErrorList = fieldErrorList;
        this.violationErrors = violationErrors;
    }

    /**
     * BindingResult를 기반으로 ErrorResponse 객체 생성.
     * @param bindingResult 스프링 MVC의 BindingResult
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(BindingResult bindingResult) {
        return new ErrorResponse(FieldError.of(bindingResult), null);
    }

    /**
     * 제약 조건 위반 세트를 기반으로 ErrorResponse 객체 생성.
     * @param violationSet 제약 조건 위반 세트
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(Set<ConstraintViolation<?>> violationSet) {
        return new ErrorResponse(null, ConstraintViolationError.of(violationSet));
    }

    /**
     * ExceptionCode를 기반으로 ErrorResponse 객체 생성.
     * @param exceptionCode 사용자 정의 예외 코드
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return new ErrorResponse(exceptionCode.getStatus(), exceptionCode.getMessage());
    }

    /**
     * HttpStatus를 기반으로 ErrorResponse 객체 생성.
     * @param httpStatus HTTP 상태 코드
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(HttpStatus httpStatus) {
        return new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
    }

    /**
     * HttpStatus와 사용자 정의 메시지를 기반으로 ErrorResponse 객체 생성.
     * @param httpStatus HTTP 상태 코드
     * @param message 사용자 정의 오류 메시지
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return new ErrorResponse(httpStatus.value(), message);
    }

    /**
     * 필드 유효성 검사 오류를 나타내는 내부 클래스.
     */
    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private Object rejectedValue;
        private String reason;

        /**
         * BindingResult를 기반으로 FieldError 리스트 생성.
         * @param bindingResult 스프링 MVC의 BindingResult
         * @return FieldError 리스트
         */
        public static List<FieldError> of(BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors =
                    bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ?
                                    "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 제약 조건 위반 오류를 나타내는 내부 클래스.
     */
    @Getter
    @AllArgsConstructor
    public static class ConstraintViolationError {
        private String propertyPath;
        private Object  rejectedValue;
        private String reason;

        /**
         * 제약 조건 위반 세트를 기반으로 ConstraintViolationError 리스트 생성.
         * @param constraintViolations 제약 조건 위반 세트
         * @return ConstraintViolationError 리스트
         */
        public static List<ConstraintViolationError> of(
                Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                    .map(constraintViolation -> new ConstraintViolationError(
                            constraintViolation.getPropertyPath().toString(),
                            constraintViolation.getInvalidValue().toString(),
                            constraintViolation.getMessage()
                    )).collect(Collectors.toList());
        }
    }
}