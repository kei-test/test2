package GInternational.server.api.service;

import GInternational.server.api.dto.AutoRechargeDTO;
import GInternational.server.api.entity.AutoRecharge;
import GInternational.server.api.mapper.AutoRechargeMapper;
import GInternational.server.api.repository.AutoRechargeRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AutoRechargeService {

    private final AutoRechargeRepository autoRechargeRepository;
    private final AutoRechargeMapper autoRechargeMapper;
    private static final Logger logger = LoggerFactory.getLogger(AutoRechargeService.class);

    public List<AutoRechargeDTO> findAutoRecharges(LocalDateTime startDateTime, LocalDateTime endDateTime, String status, String depositor, String bankName, Long number, PrincipalDetails principalDetails) {
        final LocalDateTime effectiveEndDateTime = (endDateTime == null) ? LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay() : endDateTime;

        logger.debug("Service Start DateTime: {}", startDateTime);
        logger.debug("Service End DateTime: {}", effectiveEndDateTime);

        Specification<AutoRecharge> spec = Specification.where(null);

        if (startDateTime != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                logger.debug("Adding greaterThanOrEqualTo condition for startDateTime: {}", startDateTime);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDateTime);
            });
        }

        if (effectiveEndDateTime != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                logger.debug("Adding lessThanOrEqualTo condition for endDateTime: {}", effectiveEndDateTime);
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), effectiveEndDateTime);
            });
        }

        if (StringUtils.hasText(status)) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                logger.debug("Adding equal condition for status: {}", status);
                return criteriaBuilder.equal(root.get("status"), status);
            });
        }

        if (StringUtils.hasText(depositor)) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                logger.debug("Adding equal condition for depositor: {}", depositor);
                return criteriaBuilder.equal(root.get("depositor"), depositor);
            });
        }

        if (StringUtils.hasText(bankName)) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                logger.debug("Adding equal condition for bankName: {}", bankName);
                return criteriaBuilder.equal(root.get("bankName"), bankName);
            });
        }

        if (number != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                logger.debug("Adding equal condition for number: {}", number);
                return criteriaBuilder.equal(root.get("number"), number);
            });
        }

        logger.debug("Generated Specification: {}", spec);

        return autoRechargeRepository.findAll(spec).stream()
                .map(autoRechargeMapper::toDto)
                .collect(Collectors.toList());
    }
}