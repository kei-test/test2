package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonMessageResponseDTO;
import GInternational.server.api.dto.AuditInfoDTO;
import GInternational.server.api.utilities.AuthenticationFacade;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.entity.AuditLog;
import GInternational.server.api.repository.AuditLogRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 왼쪽메뉴 [16] 관리자 관리, 85 활동로그/관리자 활동 로그
 */
@Aspect
@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuthenticationFacade authenticationFacade;

    /**
     * 어노테이션을 기반으로 메소드 실행 후 활동 로그를 생성합니다.
     *
     * @param joinPoint 조인 포인트 정보
     * @param result 메소드 실행 결과
     * @param audit 활동 로그 어노테이션 정보
     */
    @AfterReturning(value = "@annotation(audit)", returning = "result")
    public void logActivity(JoinPoint joinPoint, Object result, Audit audit) {
        Authentication authentication = authenticationFacade.getAuthentication();
        String adminUsername = authentication.getName(); // 현재 관리자의 사용자명

        String action = null; // 수행된 액션의 이름
        String details = null; // 상세 내용
        String targetId = null; // 처리 대상 사용자의 ID
        String username = null; // 처리 대상 사용자의 이름
        String ip = null; // 관리자의 IP 주소

        // 메서드 이름 확인
        String methodName = joinPoint.getSignature().getName();

        // 통합된 처리
        if (methodName.equals("sendAmazonMessage") ||                            // 총판 쪽지 보내기
                methodName.equals("deleteSelectedAmazonAdminMessages") ||        // 총판 쪽지 삭제
                methodName.equals("insertArticle") ||                            // 게시글 생성
                methodName.equals("updateArticle") ||                            // 게시글 수정
                methodName.equals("deleteArticle") ||                            // 게시글 삭제
                methodName.equals("modifyPoints") ||                             // 포인트 처리
                methodName.equals("modifySportsBalance") ||                      // 머니 처리
                methodName.equals("insertEvent") ||                              // 이벤트 생성
                methodName.equals("updateEvent") ||                              // 이벤트 수정
                methodName.equals("deleteEvent") ||                              // 이벤트 삭제
                methodName.equals("updateEventStatus") ||                        // 이벤트 상태값 변경
                methodName.equals("createCoupon") ||                             // 쿠폰 생성
                methodName.equals("cancelCouponTransaction") ||                  // 쿠폰 취소
                methodName.equals("directMoney") ||                              // 머니 직접 지급
                methodName.equals("updateAllAppleSettings") ||                   // 사과줍기 설정 변경
                methodName.equals("updateAllAttendanceRouletteSettings") ||      // 출석체크룰렛 설정 변경
                methodName.equals("updateAllRouletteSettings") ||                // 룰렛 설정 변경
                methodName.equals("cancelApprovedRollingTransaction") ||         // 롤링적립신청 취소
                methodName.equals("statusUpdate") ||                             // 자동충전 신청건 상태값 업데이트
                methodName.equals("deleteAccount") ||                            // 자동충전계좌 삭제
                methodName.equals("updateTransactionStatus") ||                  // 자동충전 신청건 상태 업데이트
                methodName.equals("createDedicatedAccount") ||                   // 전용계좌 생성
                methodName.equals("updateDedicatedAccount") ||                   // 전용계좌 수정
                methodName.equals("deleteDedicatedAccount") ||                   // 전용계좌 삭제
                methodName.equals("setActive") ||                                // 전용계좌 활성화 상태값 변경
                methodName.equals("updateExchangeTransactionStatusToWaiting") || // 환전 상태값 변경
                methodName.equals("updateExchangeSportsBalance") ||              // 환전 승인
                methodName.equals("cancelExchangeTransaction") ||                // 환전 취소
                methodName.equals("updateRechargeTransactionStatusToWaiting") || // 충전 상태값 변경
                methodName.equals("updateRechargeSportsBalance") ||              // 충전 승인
                methodName.equals("cancelRechargeTransaction") ||                // 충전 취소
                methodName.equals("updateCouponTransactionStatus") ||            // 머니쿠폰/행운복권 상태값 업데이트
                methodName.equals("updateWallet") ||                             // 지갑 업데이트
                methodName.equals("blockIp") ||                                  // 아이피 차단
                methodName.equals("deleteIp") ||                                 // 아이피 차단 해제
                methodName.equals("sendMessage") ||                              // 쪽지 보내기
                methodName.equals("deleteSelectedAdminMessages") ||              // 쪽지 삭제
                methodName.equals("resetVisitCount") ||                          // 관리자 로그인 카운트 초기화
                methodName.equals("updateAmazonUserStatus") ||                   // 총판 유저 상태값 변경
                methodName.equals("updateAmazonUserPassword") ||                 // 총판 유저 비밀번호 변경
                methodName.equals("deleteUser") ||                               // 회원 삭제
                methodName.equals("updateUser") ||                               // 유저 정보 업데이트
                methodName.equals("createManager") ||                            // 중간 관리자 생성 (manager)
                methodName.equals("updateUserRole") ||                           // 유저 역할 변경 (게스트->유저)
                methodName.equals("updateUserLevel") ||                          // 유저 레벨 변경
                methodName.equals("updateUserGubun") ||                          // 유저 구분 변경
                methodName.equals("deleteUpdate") ||                             // 유저 탈퇴 취소처리
                methodName.equals("updateInquiryStatusAndMemo") ||               // 비밀번호 문의 처리
                methodName.equals("updateApproveIp") ||                          // 관리자 접속가능 ip 변경
                methodName.equals("deleteApproveIp") ||                          // 관리자 접속가능 ip 삭제
                methodName.equals("updateUserMonitoringStatus")                  // 유저 모니터링 상태 변경
        ) {


            AuditInfoDTO info = extractAuditInfo(AuditContextHolder.getContext());
            if (info != null) {
                targetId = info.getTargetId();
                username = info.getUsername();
                ip = info.getIp();
                details = info.getDetails();
                adminUsername = info.getAdminUsername();
            }
        }

        // 어노테이션에서 정의된 액션 이름
        action = audit.value();

        // 결과 타입에 따른 추가 처리 (필요한 경우)
        if (result instanceof AmazonMessageResponseDTO) {
            AmazonMessageResponseDTO response = (AmazonMessageResponseDTO) result;
            targetId = String.valueOf(response.getId());
        }

        // AuditLog 엔티티 생성 및 저장
        AuditLog log = AuditLog.builder()
                .action(action)
                .details(details)
                .username(username)
                .adminUsername(adminUsername)
                .targetId(targetId)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(log); // 로그 저장
        AuditContextHolder.clear(); // 컨텍스트 클리어
    }

    /**
     * AuditContext로부터 AuditInfo 객체를 추출.
     *
     * 주어진 AuditContext 객체의 정보를 사용하여 새로운 AuditInfo 객체를 생성.
     * AuditContext에는 활동 로그를 생성할 때 필요한 다양한 정보들이 포함되어 있으며,
     * 이 정보들을 바탕으로 활동 로그에 기록될 대상의 ID, 사용자 이름, IP 주소, 상세 정보,
     * 그리고 관리자 사용자 이름을 포함한 AuditInfo 객체를 반환.
     *
     * @param context 활동 로그 정보가 담긴 AuditContext 객체
     * @return AuditInfo 활동 로그에 기록될 정보가 담긴 객체, 만약 context가 null이면 null 반환
     */
    private AuditInfoDTO extractAuditInfo(AuditContext context) {
        if (context != null) {
            return new AuditInfoDTO(
                    context.getTargetId(),
                    context.getUsername(),
                    context.getIp(),
                    context.getDetails(),
                    context.getAdminUsername()
            );
        }
        return null;
    }

    /**
     * 모든 활동 로그를 조회하거나 필터링된 활동 로그를 조회.
     *
     * @param action         활동 제목 (옵션)
     * @param details        처리 내용 (옵션)
     * @param ip             IP 주소 (옵션)
     * @param username       관리자가 활동한 대상의 사용자 이름 (옵션)
     * @param adminUsername  관리자의 사용자 이름 (옵션)
     * @param principalDetails 인증된 사용자 정보
     * @return 필터링된 활동 로그 목록
     */
    public List<AuditLog> getAllAuditLogs(String action, String details, String ip, String username, String adminUsername, LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        Specification<AuditLog> spec = Specification.where(null);

        if (action != null && !action.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("action"), action));
        }

        if (details != null && !details.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("details"), details));
        }

        if (ip != null && !ip.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("ip"), ip));
        }

        if (username != null && !username.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("username"), username));
        }

        if (adminUsername != null && !adminUsername.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("adminUsername"), adminUsername));
        }

        if (startDate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate));
        }

        if (endDate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate));
        }

        return auditLogRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "timestamp"));
    }

    /**
     * 활동 로그를 위한 어노테이션. 메소드에 적용하여 활동 로그를 생성할 수 있음.
     */
    @Target(ElementType.METHOD) // 메서드에 적용됨을 명시
    @Retention(RetentionPolicy.RUNTIME) // 런타임에도 어노테이션 정보가 유지됨을 명시
    public @interface Audit {
        String value() default ""; // 어노테이션 사용 시 값을 설정할 수 있는 속성
    }
}
