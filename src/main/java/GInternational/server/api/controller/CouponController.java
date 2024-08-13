package GInternational.server.api.controller;

import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.CouponRequestDTO;
import GInternational.server.api.dto.CouponResponseDTO;
import GInternational.server.api.service.CouponService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * 관리자가 사용자에게 머니쿠폰을 생성하고 지급.
     *
     * @param requestDTO 머니쿠폰 생성에 필요한 요청 데이터
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 생성된 머니쿠폰에 대한 응답 데이터 및 "쿠폰이 발급되었습니다" 메시지와 함께 HTTP 상태 코드를 반환.
     */
    @PostMapping("/managers/coupon/create")
    public ResponseEntity<?> createCoupon(@RequestBody @Valid CouponRequestDTO requestDTO,
                                          HttpServletRequest request,
                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            CouponResponseDTO response = couponService.createCoupon(requestDTO, principal, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "쿠폰이 발급되었습니다",
                    "data", response
            ));
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(e.getExceptionCode().getStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자가 쪽지를 통해 받은 머니쿠폰을 처리.
     *
     * @param transactionId 처리할 머니쿠폰 트랜잭션의 ID
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 처리된 머니쿠폰에 대한 응답 데이터 및 "머니쿠폰이 정상적으로 처리되었습니다" 메세지와 함께 HTTP 상태 코드를 반환.
     */
    @PutMapping("/users/coupon/process/money-coupon/{transactionId}")
    public ResponseEntity<?> processMoneyCoupon(@PathVariable Long transactionId,
                                                HttpServletRequest request,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            CouponResponseDTO response = couponService.processMoneyCoupon(transactionId, principal, request);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "머니쿠폰이 정상적으로 처리되었습니다.",
                    "data", response
            ));
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(e.getExceptionCode().getStatus())
                    .body(Map.of(
                            "message", e.getMessage(),
                            "status", e.getExceptionCode().getStatus()
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "서버 내부 오류가 발생했습니다.",
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
    }

    /**
     * 사용자가 쪽지를 통해 받은 행운복권을 처리.
     *
     * @param transactionId 처리할 행운복권 트랜잭션의 ID
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 처리된 행운복권에 대한 응답 데이터 및 "행운복권이 정상적으로 처리되었습니다" 메세지와 함께 HTTP 상태 코드를 반환.
     */
    @PutMapping("/users/coupon/process/lucky-lottery/{transactionId}")
    public ResponseEntity<?> processLuckyLottery(@PathVariable Long transactionId,
                                                 HttpServletRequest request,
                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            CouponResponseDTO response = couponService.processLuckyLottery(transactionId, principal, request);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "행운복권이 정상적으로 처리되었습니다.",
                    "data", response
            ));
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(e.getExceptionCode().getStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다.");
        }
    }

    /**
     * 관리자가 특정 쿠폰 트랜잭션을 취소.
     *
     * @param transactionId 취소할 쿠폰 트랜잭션의 ID
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 취소된 쿠폰 트랜잭션에 대한 응답 데이터 및 "쿠폰이 취소되었습니다" 메세지와 함께 HTTP 상태 코드를 반환.
     */
    @PutMapping("/managers/coupon/cancel/{transactionId}")
    public ResponseEntity<?> cancelCouponTransaction(@PathVariable Long transactionId,
                                                     HttpServletRequest request,
                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            CouponResponseDTO response = couponService.cancelCouponTransaction(transactionId, principal, request);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "쿠폰이 취소되었습니다.",
                    "data", response
            ));
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(e.getExceptionCode().getStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다.");
        }
    }
}
