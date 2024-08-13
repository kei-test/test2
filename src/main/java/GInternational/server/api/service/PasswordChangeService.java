package GInternational.server.api.service;

import GInternational.server.api.dto.PasswordChangeRequestDTO;
import GInternational.server.api.entity.PasswordChangeTransaction;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.PasswordChangeTransactionRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class PasswordChangeService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PasswordChangeTransactionRepository passwordChangeTransactionRepository;

    /**
     * 비밀번호 변경 신청 처리
     *
     * @param passwordChangeDTO 비밀번호 변경 요청 데이터
     * @param principalDetails 요청을 수행하는 사용자 인증 정보
     * @param httpServletRequest 클라이언트 요청 정보, IP 추출용
     */
    public void applyChangePassword(PasswordChangeRequestDTO passwordChangeDTO, PrincipalDetails principalDetails, HttpServletRequest httpServletRequest) {
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow(
                () -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
        String clientIp = httpServletRequest.getRemoteAddr();

        if (user == null) {
            throw new RestControllerException(ExceptionCode.USER_NOT_FOUND);
        }

        if (!bCryptPasswordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            throw new RestControllerException(ExceptionCode.PASSWORD_NOT_MATCH);
        }

        PasswordChangeTransaction request = new PasswordChangeTransaction();
        request.setUserId(user.getId());
        request.setUsername(user.getUsername());
        request.setNickname(user.getNickname());
        request.setStatus("대기");
        request.setPhone(user.getPhone());
        request.setOwnerName(user.getWallet().getOwnerName());
        request.setNumber(user.getWallet().getNumber());
        request.setIp(clientIp);
        request.setLastAccessedIp(user.getLastAccessedIp());
        request.setCreatedAt(LocalDateTime.now());

        request.setCurrentPassword(bCryptPasswordEncoder.encode(passwordChangeDTO.getCurrentPassword()));
        request.setNewPassword(bCryptPasswordEncoder.encode(passwordChangeDTO.getNewPassword()));

        passwordChangeTransactionRepository.save(request);
    }

    /**
     * 비밀번호 변경 신청 승인 처리
     *
     * @param transactionId 승인할 비밀번호 변경 신청 ID
     * @param principalDetails 승인을 수행하는 관리자 인증 정보
     */
    public void approvePasswordChange(Long transactionId, PrincipalDetails principalDetails) {
        PasswordChangeTransaction transaction = passwordChangeTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "비밀번호 변경 신청을 찾을 수 없습니다."));
        User user = userRepository.findById(transaction.getUserId()).orElseThrow(
                () -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));

        user.setPassword(transaction.getNewPassword());
        userRepository.save(user);

        transaction.setStatus("완료");
        transaction.setProcessedUsername(principalDetails.getUsername());
        transaction.setProcessedAt(LocalDateTime.now());
        passwordChangeTransactionRepository.save(transaction);
    }

    /**
     * 비밀번호 변경 신청 취소 처리
     *
     * @param transactionId 취소할 비밀번호 변경 신청 ID
     * @param principalDetails 취소를 수행하는 관리자 인증 정보
     */
    public void cancelPasswordChange(Long transactionId, PrincipalDetails principalDetails) {
        PasswordChangeTransaction transaction = passwordChangeTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "비밀번호 변경 신청을 찾을 수 없습니다."));

        if (transaction.getStatus().equals("대기")) {
            transaction.setStatus("취소");
            transaction.setProcessedUsername(principalDetails.getUsername());
            transaction.setProcessedAt(LocalDateTime.now());
            passwordChangeTransactionRepository.save(transaction);
        } else {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "대기상태의 신청건만 취소할 수 있습니다");
        }
    }

    /**
     * 비밀번호 변경 신청 목록 조회
     *
     * @param status 조회할 신청 상태
     * @param username 조회할 사용자명
     * @param nickname 조회할 닉네임
     * @param startDate 조회할 시작시간
     * @param endDate 조회할 종료시간
     * @return 조건에 맞는 비밀번호 변경 신청 목록
     */
    public List<PasswordChangeTransaction> searchPasswordChangeTransactions(String status, String username, String nickname,
                                                                            LocalDateTime startDate, LocalDateTime endDate,
                                                                            PrincipalDetails principalDetails) {
        return passwordChangeTransactionRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (username != null && !username.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
            }
            if (nickname != null && !nickname.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + nickname + "%"));
            }
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
            }

            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
