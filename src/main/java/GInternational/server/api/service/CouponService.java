package GInternational.server.api.service;

import GInternational.server.api.vo.CouponTypeEnum;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.CouponRequestDTO;
import GInternational.server.api.dto.CouponResponseDTO;
import GInternational.server.api.mapper.CouponResponseMapper;
import GInternational.server.api.entity.CouponTransaction;
import GInternational.server.api.repository.CouponTransactionRepository;
import GInternational.server.api.vo.CouponTransactionEnum;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class CouponService {

    private final CouponTransactionRepository couponTransactionRepository;
    private final CouponResponseMapper couponResponseMapper;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PointLogService pointLogService;
    private final MoneyLogService moneyLogService;

    /**
     * 머니쿠폰/행운복권 트랜잭션 생성 (관리자가 쪽찌로 유저에게 머니쿠폰/행운복권을 지급)
     *
     * @param requestDTO 머니쿠폰/행운복권 요청 데이터
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 생성된 트랜잭션에 대한 응답 데이터
     * @throws RestControllerException 사용자를 찾을 수 없는 경우 예외 발생
     */
    @AuditLogService.Audit("머니쿠폰/행운복권 발급")
    public CouponResponseDTO createCoupon(@Valid CouponRequestDTO requestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findByUsername(requestDTO.getUsername());
        if (user == null) {
            throw new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        validateCouponRequest(requestDTO);

        List<CouponTransaction> savedTransactions = new ArrayList<>();
        for (int i = 0; i < requestDTO.getQuantity(); i++) {
            CouponTransaction transaction = new CouponTransaction();
            transaction.setUser(user);
            transaction.setSite("test");
            transaction.setNickname(user.getNickname());
            transaction.setUsername(user.getUsername());
            transaction.setCouponName(requestDTO.getCouponName());
            transaction.setCouponTypeEnum(requestDTO.getCouponTypeEnum());
            transaction.setSportsBalance(requestDTO.getSportsBalance());
            transaction.setPoint(requestDTO.getPoint());
            transaction.setMemo(requestDTO.getMemo());
            transaction.setExpirationDateTime(requestDTO.getExpirationDateTime());
            transaction.setStatus(CouponTransactionEnum.WAITING);
            savedTransactions.add(couponTransactionRepository.save(transaction));
        }

        String couponType = requestDTO.getCouponTypeEnum().equals("머니쿠폰") ? "머니" : "포인트";
        String couponValue = requestDTO.getCouponTypeEnum().equals("머니쿠폰") ? String.valueOf(requestDTO.getSportsBalance()) : String.valueOf(requestDTO.getPoint());
        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(requestDTO.getCouponName() + " 발급, 발급대상: " + user.getUsername() + ", 발급금액: " + couponValue + " (" + couponType + "), 수량: " + requestDTO.getQuantity());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return couponResponseMapper.toDto(savedTransactions.get(0));
    }

    /**
     * 머니쿠폰 트랜잭션을 처리하여 사용자의 스포츠 밸런스를 업데이트. (유저가 쪽지에서 머니쿠폰을 받음)
     *
     * @param transactionId 처리할 트랜잭션 ID
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 처리된 트랜잭션에 대한 응답 데이터
     * @throws RestControllerException 트랜잭션 관련 예외 발생 시
     */
    public CouponResponseDTO processMoneyCoupon(Long transactionId, PrincipalDetails principalDetails, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        CouponTransaction transaction = validateAndFetchCouponTransaction(transactionId, CouponTypeEnum.머니쿠폰);

        long sportsBalanceToAdd = transaction.getSportsBalance();
        updateWalletBalance(transaction.getUser(), sportsBalanceToAdd, 0, clientIp);

        return couponResponseMapper.toDto(transaction);
    }

    /**
     * 행운복권 트랜잭션을 처리하여 사용자의 포인트를 업데이트. (유저가 쪽지에서 행운복권을 받음)
     *
     * @param transactionId 처리할 트랜잭션 ID
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 처리된 트랜잭션에 대한 응답 데이터
     * @throws RestControllerException 트랜잭션 관련 예외 발생 시
     */

    public CouponResponseDTO processLuckyLottery(Long transactionId, PrincipalDetails principalDetails, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();

        CouponTransaction transaction = validateAndFetchCouponTransaction(transactionId, CouponTypeEnum.행운복권);

        long pointsToAdd = transaction.getPoint();
        updateWalletBalance(transaction.getUser(), 0, pointsToAdd, clientIp);

        return couponResponseMapper.toDto(transaction);
    }

    /**
     * 쿠폰 트랜잭션을 취소.
     * 이 메서드는 주어진 트랜잭션 ID에 해당하는 쿠폰 트랜잭션을 찾아 취소 상태로 변경.
     * 이미 취소되었거나 만료된 트랜잭션은 취소할 수 없으며, 이 경우 적절한 예외가 발생.
     *
     * @param transactionId 취소할 트랜잭션의 ID
     * @param principalDetails 현재 인증된 사용자의 상세 정보를 담고 있는 객체
     * @return 취소된 트랜잭션에 대한 응답 데이터를 담고 있는 DTO
     * @throws RestControllerException 트랜잭션을 찾을 수 없거나 이미 취소/만료된 경우 예외를 발생시킴.
     */
    @AuditLogService.Audit("머니쿠폰/행운복권 발급 취소")
    public CouponResponseDTO cancelCouponTransaction(Long transactionId, PrincipalDetails principalDetails, HttpServletRequest request) {
        CouponTransaction transaction = couponTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.APPLIED_TRANSACTION_NOT_FOUNT, "트랜잭션을 찾을 수 없습니다."));

        if (transaction.getStatus().equals(CouponTransactionEnum.CANCELLATION) || transaction.getStatus().equals(CouponTransactionEnum.EXPIRED)) {
            throw new RestControllerException(ExceptionCode.TRANSACTION_ALREADY_PROCESSED, "이미 취소되었거나 만료된 트랜잭션은 취소할 수 없습니다.");
        }

        transaction.setStatus(CouponTransactionEnum.CANCELLATION);
        CouponTransaction savedTransaction = couponTransactionRepository.save(transaction);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(transaction.getUser().getId()));
        context.setUsername(transaction.getUser().getUsername());
        context.setDetails(transaction.getCouponName() + " 발급 취소, 취소대상: " + transaction.getUser().getUsername());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return couponResponseMapper.toDto(savedTransaction);
    }

    /**
     * 주어진 트랜잭션 ID에 해당하는 쿠폰 트랜잭션을 검증하고 가져옴.
     * 트랜잭션이 만료되었거나 이미 처리되었거나 예상한 쿠폰 타입과 일치하지 않는 경우 예외를 발생시킴.
     *
     * @param transactionId 검증하고자 하는 트랜잭션 ID
     * @param expectedCouponType 예상하는 쿠폰 타입
     * @return 검증된 쿠폰 트랜잭션
     * @throws RestControllerException 트랜잭션을 찾을 수 없거나, 이미 만료되었거나, 이미 처리되었거나, 잘못된 쿠폰 타입인 경우 예외 발생
     */
    private CouponTransaction validateAndFetchCouponTransaction(Long transactionId, CouponTypeEnum expectedCouponType) {
        CouponTransaction transaction = couponTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.APPLIED_TRANSACTION_NOT_FOUNT, "신청건을 찾을 수 없습니다"));

        if (transaction.getExpirationDateTime().isBefore(LocalDateTime.now())) {
            transaction.setStatus(CouponTransactionEnum.EXPIRED);
            couponTransactionRepository.save(transaction);
            throw new RestControllerException(ExceptionCode.MONEY_COUPON_EXPIRED, "쿠폰의 유효기간이 만료되었습니다.");
        }

        if (transaction.getStatus() != CouponTransactionEnum.WAITING) {
            throw new RestControllerException(ExceptionCode.TRANSACTION_ALREADY_PROCESSED, "이미 처리된 트랜잭션입니다.");
        }

        if (transaction.getCouponTypeEnum() != expectedCouponType) {
            throw new RestControllerException(ExceptionCode.INVALID_COUPON_TYPE, "잘못된 쿠폰 종류입니다.");
        }

        transaction.setStatus(CouponTransactionEnum.APPROVAL);
        couponTransactionRepository.save(transaction);

        return transaction;
    }

    /**
     * 사용자와 사용자의 지갑에 스포츠 밸런스와 포인트를 추가.
     * 변경사항은 데이터베이스에 저장.
     *
     * @param user 사용자 객체
     * @param sportsBalanceToAdd 추가할 스포츠 밸런스 양
     * @param pointsToAdd 추가할 포인트 양
     */
    private void updateWalletBalance(User user, long sportsBalanceToAdd, long pointsToAdd, String clientIp) {
        if (sportsBalanceToAdd > 0) {
            user.getWallet().setSportsBalance(user.getWallet().getSportsBalance() + sportsBalanceToAdd);
            moneyLogService.recordMoneyUsage(user.getId(),
                                             sportsBalanceToAdd,
                                             user.getWallet().getSportsBalance() + sportsBalanceToAdd,
                                             MoneyLogCategoryEnum.머니쿠폰,
                                             "");
        }

        if (pointsToAdd > 0) {
            user.getWallet().setPoint(user.getWallet().getPoint() + pointsToAdd);
            pointLogService.recordPointLog(user.getId(), pointsToAdd, PointLogCategoryEnum.행운복권, clientIp, "");
        }
        userRepository.save(user);
    }


    /**
     * 쿠폰 요청에 대한 필수값 검증을 수행.
     * "머니쿠폰"은 sportsBalance를, "행운복권"은 point를 필수로 포함해야 함.
     *
     * @param requestDTO 쿠폰 요청 데이터
     * @throws RestControllerException 필수 필드가 누락된 경우 예외 발생
     */
    private void validateCouponRequest(CouponRequestDTO requestDTO) {
        if (CouponTypeEnum.머니쿠폰.equals(requestDTO.getCouponTypeEnum()) && requestDTO.getSportsBalance() == null) {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "머니쿠폰 생성 시 스포츠 밸런스를 포함해야 합니다.");
        } else if (CouponTypeEnum.행운복권.equals(requestDTO.getCouponTypeEnum()) && requestDTO.getPoint() == null) {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "행운복권 생성 시 포인트를 포함해야 합니다.");
        }
    }
}
