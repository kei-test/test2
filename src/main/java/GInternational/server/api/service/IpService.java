package GInternational.server.api.service;

import GInternational.server.api.dto.IpReqDTO;
import GInternational.server.api.entity.Ip;
import GInternational.server.api.repository.IpRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class IpService {

    private final IpRepository ipRepository;

    /**
     * IP를 차단. 요청된 IP 정보를 받아 새로운 IP 엔티티를 생성하고 저장.
     *
     * @param ipReqDTO 차단할 IP의 정보를 담고 있는 DTO
     * @param principalDetails 사용자 인증 정보
     * @return 저장된 Ip 엔티티
     */
    @AuditLogService.Audit("아이피 차단")
    public Ip blockIp(IpReqDTO ipReqDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        if (ipReqDTO.getIpContent() == null || ipReqDTO.getIpContent().trim().isEmpty()) {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "아이피를 입력하세요");
        }
        if (ipReqDTO.getNote() == null || ipReqDTO.getNote().trim().isEmpty()) {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "비고를 입력하세요");
        }

        Optional<Ip> existingIp = Optional.ofNullable(ipRepository.findByIpContent(ipReqDTO.getIpContent()));
        if (existingIp.isPresent()) {
            throw new RestControllerException(ExceptionCode.DUPLICATE_ENTRY, "이미 차단된 아이피입니다.");
        }

        Ip ip = new Ip();
        ip.setIpContent(ipReqDTO.getIpContent());
        ip.setNote(ipReqDTO.getNote());
        ip.setEnabled(true);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails(ipReqDTO.getIpContent() + " 아이피 차단");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return ipRepository.save(ip);
    }

    /**
     * IP 차단 해제.
     *
     * @param id 차단 해제할 IP의 ID
     * @param principalDetails 사용자 인증 정보
     */
    @AuditLogService.Audit("아이피 차단 해제")
    public void deleteIp(Long id, PrincipalDetails principalDetails, HttpServletRequest request) {;
        Ip ip = validateIp(id);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails(ip.getIpContent() + " 아이피 차단 해제");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        ipRepository.delete(ip);
    }

    /**
     * 주어진 ID에 해당하는 IP가 존재하는지 검증하고, 존재한다면 해당 IP 엔티티를 반환.
     *
     * @param id 검증할 IP의 ID
     * @return 검증된 Ip 엔티티
     */
    public Ip validateIp(Long id) {
        Optional<Ip> ip = ipRepository.findById(id);
        Ip findIp = ip.orElseThrow(()-> new RuntimeException("해당 ip 없음"));
        return findIp;
    }

    /**
     * 지정된 날짜 범위 내에 생성된 모든 IP를 조회.
     *
     * @param startDate 조회 시작 날짜 (옵션)
     * @param endDate 조회 종료 날짜 (옵션)
     * @param principalDetails 사용자 인증 정보
     * @param ipContent IP 내용 (옵션)
     * @param note 노트 (옵션)
     * @return 조회된 Ip 엔티티의 리스트
     */
    public List<Ip> findIpsByFilters(LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails,
                                     String ipContent, String note) {
        return ipRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
            }
            if (ipContent != null && !ipContent.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("ipContent"), ipContent));
            }
            if (note != null && !note.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("note"), "%" + note + "%"));
            }

            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
