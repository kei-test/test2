package GInternational.server.common.advice;



import GInternational.server.common.exception.RestControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * 전역 예외 처리를 위한 어드바이스 클래스.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    /**
     * 메서드 인자 유효성 검사 예외 처리.
     * @param e 발생한 예외
     * @return 오류 응답
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final ErrorResponse response = ErrorResponse.of(e.getBindingResult());
        return response;
    }

    /**
     * 제약 조건 위반 예외 처리.
     * @param e 발생한 예외
     * @return 오류 응답
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(ConstraintViolationException e) {
        final ErrorResponse response = ErrorResponse.of(e.getConstraintViolations());
        return response;
    }

    /**
     * 비즈니스 로직 실행 중 발생한 사용자 정의 예외 처리.
     * @param e 발생한 예외
     * @return ResponseEntity 객체
     */
    @ExceptionHandler
    public ResponseEntity handleBusinessLogicException(RestControllerException e) {
        final ErrorResponse response = ErrorResponse.of(e.getExceptionCode());
        return new ResponseEntity<>(HttpStatus.valueOf(e.getExceptionCode().getStatus()));
    }

    /**
     * 지원하지 않는 HTTP 메서드 호출 예외 처리.
     * @param e 발생한 예외
     * @return 오류 응답
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodArgumentNotValidException(HttpRequestMethodNotSupportedException e) {
        final ErrorResponse response = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED);
        return response;
    }

    /**
     * HTTP 메시지 읽기 실패 예외 처리.
     * @param e 발생한 예외
     * @return 오류 응답
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        final ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST,
                "Required request body is missing");
        return response;
    }

    /**
     * 필수 서블릿 요청 매개변수 누락 예외 처리.
     * @param e 발생한 예외
     * @return 오류 응답
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        final ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
        return response;
    }
}