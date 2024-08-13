package GInternational.server.api.service;

import GInternational.server.api.vo.AppStatus;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.AccountReqDTO;
import GInternational.server.api.entity.Account;
import GInternational.server.api.repository.AccountRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * 사용자 ID와 계좌 요청 데이터를 기반으로 새로운 계좌를 생성하고 저장.
     *
     * @param userId 사용자 ID
     * @param accountReqDTO 계좌 생성 요청 데이터
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 생성된 계좌 엔티티
     * @throws RestControllerException 사용자를 찾을 수 없거나 이미 등록된 계좌가 있는 경우 예외 발생
     */
    public Account insertAccount(Long userId, AccountReqDTO accountReqDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        if (user.getAccount() == null) {
            Account account = Account.builder()
                    .bank(accountReqDTO.getBank())
                    .owner(accountReqDTO.getOwner())
                    .number(accountReqDTO.getNumber())
                    .site("test")
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .status(AppStatus.WAIT)
                    .build();
            return accountRepository.save(account);
        }
        throw new RestControllerException(ExceptionCode.ACCOUNT_ALREADY_EXISTS, "등록 신청한 계좌가 있습니다.");
    }

    /**
     * 대기 상태인 계좌의 상태를 승인으로 업데이트.
     *
     * @param userId 사용자 ID
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @param request 상태값을 변경한 관리자의 ip
     * @return 상태가 업데이트된 계좌 엔티티
     * @throws RestControllerException 사용자를 찾을 수 없거나 대기 중인 상태의 신청 건만 승인 가능할 때 예외 발생
     */
    @AuditLogService.Audit("자동충전 신청건 상태값 변경")
    public Account statusUpdate(Long userId, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        if (user.getAccount().getStatus() == AppStatus.WAIT) {
            Account account = user.getAccount();
            account.setStatus(AppStatus.OK);
            account.setUser(user);
            account.setProcessedAt(LocalDateTime.now());

            AuditContext context = AuditContextHolder.getContext();
            String clientIp = request.getRemoteAddr();
            context.setIp(clientIp);
            context.setTargetId(String.valueOf(user.getId()));
            context.setUsername(user.getUsername());
            context.setDetails("자동충전 신청건 상태값 변경, " + "대상 아이디: " + user.getUsername());
            context.setAdminUsername(principalDetails.getUsername());
            context.setTimestamp(LocalDateTime.now());

            return accountRepository.save(account);
        }

        throw new RestControllerException(ExceptionCode.ONLY_WAITING_TRANSACTIONS_CAN_BE_APPROVED, "대기 중인 상태의 신청 건만 승인 가능합니다.");
    }

    /**
     * 주어진 계좌 ID 목록에 해당하는 계좌들을 삭제.
     *
     * @param accountIds 삭제할 계좌 ID 목록
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     */
    @AuditLogService.Audit("자동충전 계좌 삭제")
    public void deleteAccount(List<Long> accountIds, PrincipalDetails principalDetails, HttpServletRequest request) {
        List<Account> accountsToDelete = accountRepository.findAllById(accountIds);

        LocalDateTime now = LocalDateTime.now();
        String clientIp = request.getRemoteAddr();
        String adminUsername = principalDetails.getUsername();

        for (Account account : accountsToDelete) {
            AuditContext context = AuditContextHolder.getContext();
            context.setIp(clientIp);
            context.setTargetId(String.valueOf(account.getUser().getId()));
            context.setUsername(account.getUser().getUsername());
            context.setDetails("자동충전 계좌 삭제 - 계좌 ID: " + account.getUser().getUsername());
            context.setAdminUsername(adminUsername);
            context.setTimestamp(now);
        }

        accountRepository.deleteAll(accountsToDelete);
    }

    /**
     * 주어진 상태와 페이지 정보에 따라 계좌 목록을 페이징하여 조회.
     *
     * @param status 조회할 계좌의 상태
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 조회된 계좌 페이지
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public Page<Account> getAccounts(AppStatus status, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Account> pages = accountRepository.searchByAppStatus(status, pageable);
        return new PageImpl<>(pages.getContent(), pageable, pages.getTotalElements());
    }
}
