package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.dto.PasswordInquiryRequestDTO;
import GInternational.server.api.dto.PasswordInquiryResponseDTO;
import GInternational.server.api.entity.PasswordInquiry;
import GInternational.server.api.mapper.PasswordInquiryResponseMapper;
import GInternational.server.api.repository.PasswordInquiryRepository;
import GInternational.server.api.vo.PasswordInquiryStatusEnum;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class PasswordInquiryService {


    private final UserRepository userRepository;
    private final PasswordInquiryRepository passwordInquiryRepository;
    private final PasswordInquiryResponseMapper passwordInquiryResponseMapper;

    /**
     * 비밀번호 문의를 생성하고 처리 결과 반환.
     *
     * @param passwordInquiryDTO 비밀번호 문의 요청 정보를 담은 DTO
     * @param request 클라이언트의 HTTP 요청 정보
     * @return PasswordInquiryResponseDTO 생성된 비밀번호 문의 정보를 담은 응답 DTO
     */
    public PasswordInquiryResponseDTO passwordInquiry(PasswordInquiryRequestDTO passwordInquiryDTO, HttpServletRequest request) {
        User user = userRepository.findByUsername(passwordInquiryDTO.getUsername());
        if (user == null) {
            throw new RestControllerException(ExceptionCode.USER_NOT_FOUND, "일치하는 id가 없습니다. id를 다시 확인하세요.");
        }
        String userIpAddress = request.getRemoteAddr();

        PasswordInquiry passwordInquiry = new PasswordInquiry();
        passwordInquiry.setUsername(passwordInquiryDTO.getUsername());
        passwordInquiry.setOwnerName(passwordInquiryDTO.getOwnerName());
        passwordInquiry.setIp(userIpAddress);
        passwordInquiry.setPhone(passwordInquiryDTO.getPhone());
        passwordInquiry.setStatus(PasswordInquiryStatusEnum.WAITING);
        passwordInquiry.setCreatedAt(LocalDateTime.now());

        PasswordInquiry savedPasswordInquiry = passwordInquiryRepository.save(passwordInquiry);

        return passwordInquiryResponseMapper.toDto(savedPasswordInquiry);
    }

    /**
     * 모든 비밀번호 문의 조회.
     *
     * @param principalDetails 인증된 사용자의 상세 정보
     * @return List<PasswordInquiryResponseDTO> 조회된 비밀번호 문의 목록
     */
    public List<PasswordInquiryResponseDTO> getAllPasswordInquiries(PrincipalDetails principalDetails) {
        List<PasswordInquiry> inquiries = passwordInquiryRepository.findAll();
        return inquiries.stream()
                .map(passwordInquiryResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 비밀번호 문의의 상태와 관리자 메모 업데이트.
     *
     * @param inquiryId 업데이트할 비밀번호 문의 ID
     * @param adminMemo 관리자가 추가할 메모
     * @param principalDetails 인증된 사용자의 상세 정보
     * @return PasswordInquiryResponseDTO 업데이트된 비밀번호 문의 정보를 담은 응답 DTO
     */
    @AuditLogService.Audit("비밀번호 문의 게시글 처리")
    public PasswordInquiryResponseDTO updateInquiryStatusAndMemo(Long inquiryId, String adminMemo, PrincipalDetails principalDetails, HttpServletRequest request) {
        PasswordInquiry inquiry = passwordInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.INQUIRY_NOT_FOUND));

        inquiry.setAdminMemo(adminMemo);
        inquiry.setStatus(PasswordInquiryStatusEnum.PROCESSED);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(inquiry.getUsername());
        context.setDetails(inquiry.getUsername() + "의 비밀번호 문의 처리");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        PasswordInquiry updatedInquiry = passwordInquiryRepository.save(inquiry);

        return mapToResponseDTO(updatedInquiry);
    }

    /**
     * PasswordInquiry 엔티티를 PasswordInquiryResponseDTO로 변환.
     *
     * @param inquiry 변환할 PasswordInquiry 엔티티
     * @return PasswordInquiryResponseDTO 변환된 DTO
     */
    private PasswordInquiryResponseDTO mapToResponseDTO(PasswordInquiry inquiry) {
        PasswordInquiryResponseDTO dto = new PasswordInquiryResponseDTO();
        dto.setId(inquiry.getId());
        dto.setUsername(inquiry.getUsername());
        dto.setOwnerName(inquiry.getOwnerName());
        dto.setPhone(inquiry.getPhone());
        dto.setIp(inquiry.getIp());
        dto.setCreatedAt(inquiry.getCreatedAt());
        dto.setAdminMemo(inquiry.getAdminMemo());
        dto.setStatus(inquiry.getStatus());
        return dto;
    }
}
