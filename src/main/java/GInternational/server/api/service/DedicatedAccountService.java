package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.DedicatedAccountRequestDTO;
import GInternational.server.api.dto.DedicatedAccountResponseDTO;
import GInternational.server.api.entity.DedicatedAccount;
import GInternational.server.api.mapper.DedicatedAccountRequestMapper;
import GInternational.server.api.mapper.DedicatedAccountResponseMapper;
import GInternational.server.api.repository.DedicatedAccountRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class DedicatedAccountService {

    private final DedicatedAccountRepository dedicatedAccountRepository;
    private final DedicatedAccountRequestMapper dedicatedAccountRequestMapper;
    private final DedicatedAccountResponseMapper dedicatedAccountResponseMapper;
    private final UserRepository userRepository;

    /**
     * 새 전용계좌 생성
     *
     * @param requestDTO 전용 계좌 생성 요청 데이터
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 생성된 전용 계좌의 정보
     * @throws DataIntegrityViolationException 데이터 유효성 오류 발생 시
     * @throws Exception 내부 서버 오류 발생 시
     */
    @AuditLogService.Audit("전용계좌 생성")
    public DedicatedAccountResponseDTO createDedicatedAccount(DedicatedAccountRequestDTO requestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        try {
            DedicatedAccount dedicatedAccount = dedicatedAccountRequestMapper.INSTANCE.toEntity(requestDTO);
            dedicatedAccount.setActive(true);
            dedicatedAccount = dedicatedAccountRepository.save(dedicatedAccount);

            AuditContext context = AuditContextHolder.getContext();
            String clientIp = request.getRemoteAddr();
            context.setIp(clientIp);
            context.setTargetId(null);
            context.setUsername(null);
            context.setDetails("전용계좌 생성");
            context.setAdminUsername(principalDetails.getUsername());
            context.setTimestamp(LocalDateTime.now());

            return dedicatedAccountResponseMapper.INSTANCE.toDto(dedicatedAccount);
        } catch (DataIntegrityViolationException e) {
            throw new RestControllerException(ExceptionCode.DATA_INTEGRITY_VIOLATION, "데이터 유효성 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new RestControllerException(ExceptionCode.INTERNAL_ERROR, "내부 서버 오류입니다.");
        }
    }

    /**
     * 지정된 ID의 전용계좌 수정
     *
     * @param id 업데이트할 전용계좌의 ID
     * @param requestDTO 전용 계좌 업데이트 요청 데이터
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 업데이트된 전용 계좌의 정보
     * @throws RestControllerException 전용계좌를 찾을 수 없을 때
     */
    @AuditLogService.Audit("전용계좌 수정")
    public DedicatedAccountResponseDTO updateDedicatedAccount(Long id, DedicatedAccountRequestDTO requestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        DedicatedAccount dedicatedAccount = dedicatedAccountRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DEDICATED_ACCOUNT_NOT_FOUND, "수정할 전용계좌를 찾을 수 없습니다. 전용계좌 id를 다시 확인하세요."));

        List<String> changes = new ArrayList<>();

        if (requestDTO.getBankName() != null && !requestDTO.getBankName().equals(dedicatedAccount.getBankName())) {
            changes.add("은행명: " + dedicatedAccount.getBankName() + " -> " + requestDTO.getBankName());
            dedicatedAccount.setBankName(requestDTO.getBankName());
        }
        if (requestDTO.getOwnerName() != null && !requestDTO.getOwnerName().equals(dedicatedAccount.getOwnerName())) {
            changes.add("소유주명: " + dedicatedAccount.getOwnerName() + " -> " + requestDTO.getOwnerName());
            dedicatedAccount.setOwnerName(requestDTO.getOwnerName());
        }
        if (requestDTO.getNumber() != null && !requestDTO.getNumber().equals(dedicatedAccount.getNumber())) {
            changes.add("계좌번호: " + dedicatedAccount.getNumber() + " -> " + requestDTO.getNumber());
            dedicatedAccount.setNumber(requestDTO.getNumber());
        }
        if (requestDTO.getLv() != null && !requestDTO.getLv().equals(dedicatedAccount.getLevels())) {
            changes.add("레벨 변경");
            dedicatedAccount.setLevels(new HashSet<>(requestDTO.getLv()));
        }

        String changeDetails = String.join(", ", changes);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        if (!changes.isEmpty()) {
            context.setDetails("전용계좌 수정: " + changeDetails);
        } else {
            context.setDetails("전용계좌 수정: 변경사항 없음");
        }
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        dedicatedAccountRepository.save(dedicatedAccount);
        return dedicatedAccountResponseMapper.INSTANCE.toDto(dedicatedAccount);
    }

    /**
     * 지정된 ID의 전용계좌 삭제
     *
     * @param id 삭제할 전용계좌의 ID
     * @param principalDetails 현재 사용자의 인증 정보
     * @throws RestControllerException 전용계좌를 찾을 수 없을 때
     */
    @AuditLogService.Audit("전용계좌 삭제")
    public void deleteDedicatedAccount(Long id, PrincipalDetails principalDetails, HttpServletRequest request) {
        DedicatedAccount dedicatedAccount = dedicatedAccountRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DEDICATED_ACCOUNT_NOT_FOUND, "삭제할 전용계좌를 찾을 수 없습니다. 전용계좌 id를 다시 확인하세요."));

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("전용계좌 삭제");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        dedicatedAccountRepository.delete(dedicatedAccount);
    }

    /**
     * 특정 유저 레벨에 해당하는 활성화된 전용계좌 조회
     *
     * @param lv 조회할 사용자 레벨
     * @return 해당 레벨에 활성화된 전용 계좌들의 목록
     * @throws RestControllerException 설정된 전용계좌가 없을 때
     */
    public List<DedicatedAccountResponseDTO> getActiveDedicatedAccountsForUserLevel(int lv, PrincipalDetails principalDetails) {
        List<DedicatedAccount> accounts = dedicatedAccountRepository.findByLevelsContainsAndIsActive(lv, true);

        if (accounts.isEmpty()) {
            throw new RestControllerException(ExceptionCode.DEDICATED_ACCOUNT_NOT_FOUND, "설정된 전용계좌가 없습니다. 관리자에게 문의하세요.");
        }
        return accounts.stream()
                .map(dedicatedAccountResponseMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 전용계좌의 활성화/비활성화 상태 변경
     *
     * @param id 상태를 변경할 전용계좌의 ID
     * @param isActive 활성화 여부
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 상태가 변경된 전용 계좌의 정보
     * @throws RestControllerException 전용계좌를 찾을 수 없을 때
     */
    @AuditLogService.Audit("전용계좌 활성화 상태값 변경")
    public DedicatedAccountResponseDTO setActive(Long id, boolean isActive, PrincipalDetails principalDetails, HttpServletRequest request) {
        DedicatedAccount account = dedicatedAccountRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DEDICATED_ACCOUNT_NOT_FOUND, "활성상태를 변경 할 전용계좌를 찾을 수 없습니다. 전용계좌 id를 다시 확인하세요."));
        account.setActive(isActive);

        String active;
        if (account.isActive()) {
            active = "활성화";
        } else {
            active = "비활성화";
        }

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("전용계좌 활성화 상태값 " + active + "로 변경");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        dedicatedAccountRepository.save(account);


        return dedicatedAccountResponseMapper.INSTANCE.toDto(account);
    }
}
