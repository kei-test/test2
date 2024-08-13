package GInternational.server.api.controller;

import GInternational.server.api.vo.CouponTypeEnum;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.api.dto.CouponTransactionResDTO;
import GInternational.server.api.entity.CouponTransaction;
import GInternational.server.api.mapper.CouponTransactionResponseMapper;
import GInternational.server.api.service.CouponTransactionService;
import GInternational.server.api.vo.CouponTransactionEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class CouponTransactionController {

    private final CouponTransactionService couponTransactionService;
    private final CouponTransactionResponseMapper mapper;

    /**
     * 특정 사용자의 특정 기간 동안의 쿠폰 트랜잭션을 조회.
     *
     * @param userId           사용자 ID
     * @param startDate        시작 날짜
     * @param endDate          종료 날짜
     * @param authentication   인증 정보
     * @return 특정 사용자의 특정 기간 동안의 쿠폰 트랜잭션 정보를 담은 ResponseEntity
     */
    @GetMapping("/users/coupon/{userId}")
    public ResponseEntity<MultiResponseDto<CouponTransactionResDTO>> getCouponTransactionByDate(@PathVariable("userId") @Positive Long userId,
                                                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<CouponTransaction> couponTransactionsList = couponTransactionService.getCouponTransactionsByUserIdAndDate(userId, startDate, endDate);
        List<CouponTransactionResDTO> couponTransactionResDTOList = mapper.toDto(couponTransactionsList);
        MultiResponseDto<CouponTransactionResDTO> response = new MultiResponseDto<>(couponTransactionResDTOList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 특정 기간 동안의 모든 쿠폰 트랜잭션을 조회.
     *
     * @param startDate        시작 날짜 (옵션)
     * @param endDate          종료 날짜 (옵션)
     * @param username         사용자 이름 (옵션)
     * @param nickname         닉네임 (옵션)
     * @param couponTypeEnum   쿠폰 타입 (옵션)
     * @param authentication   인증 정보
     * @return 특정 기간 동안의 모든 쿠폰 트랜잭션 정보를 담은 ResponseEntity
     */
    @GetMapping("/managers/coupon/all")
    public ResponseEntity<MultiResponseDto<CouponTransactionResDTO>> getAllCouponTransactionByDate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String couponName,
            @RequestParam(required = false) CouponTypeEnum couponTypeEnum,
            @RequestParam(required = false) CouponTransactionEnum status,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<CouponTransaction> couponTransactionsList = couponTransactionService.findAllCouponTransactionByCriteria(startDate, endDate, username, nickname, couponName, couponTypeEnum, status, principal);
        List<CouponTransactionResDTO> couponTransactionResDTOList = mapper.toDto(couponTransactionsList);
        MultiResponseDto<CouponTransactionResDTO> response = new MultiResponseDto<>(couponTransactionResDTOList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 특정 상태와 기간 동안의 쿠폰 트랜잭션을 조회.
     *
     * @param status           쿠폰 트랜잭션 상태
     * @param startDate        시작 날짜
     * @param endDate          종료 날짜
     * @param authentication   인증 정보
     * @return 특정 상태와 기간 동안의 쿠폰 트랜잭션 정보를 담은 ResponseEntity
     */
    @GetMapping("/managers/coupon/status/{status}")
    public ResponseEntity<MultiResponseDto<CouponTransactionResDTO>> getCouponTransactionsByStatusAndDate(@PathVariable CouponTransactionEnum status,
                                                                                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<CouponTransaction> transactionsList = couponTransactionService.getCouponTransactionsByStatusAndDate(status, startDate, endDate, principal);
        List<CouponTransactionResDTO> dtoList = mapper.toDto(transactionsList);
        MultiResponseDto<CouponTransactionResDTO> response = new MultiResponseDto<>(dtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 쿠폰 트랜잭션 상태를 업데이트.
     *
     * @param transactionId  트랜잭션 ID
     * @param newStatus      새로운 상태
     * @param authentication 인증 정보
     * @return ResponseEntity
     */
    @PutMapping("/managers/coupon/update-status/{transactionId}")
    public ResponseEntity<String> updateCouponTransactionStatus(@PathVariable Long transactionId,
                                                                @RequestBody CouponTransactionEnum newStatus,
                                                                HttpServletRequest request,
                                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        couponTransactionService.updateCouponTransactionStatus(transactionId, newStatus, principal, request);
        return ResponseEntity.ok("트랜잭션 상태가 업데이트 되었습니다");
    }
}


