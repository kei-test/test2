package GInternational.server.api.service;

import GInternational.server.api.dto.DailyLimitDTO;
import GInternational.server.api.entity.DailyLimit;
import GInternational.server.api.mapper.DailyLimitMapper;
import GInternational.server.api.repository.DailyLimitRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class DailyLimitService {

    private final DailyLimitRepository dailyLimitRepository;
    private final DailyLimitMapper dailyLimitMapper;

    public DailyLimitDTO createDailyLimit(DailyLimitDTO dailyLimitDTO, PrincipalDetails principalDetails) {
        DailyLimit dailyLimit = dailyLimitMapper.toEntity(dailyLimitDTO);
        DailyLimit savedDailyLimit = dailyLimitRepository.save(dailyLimit);
        return dailyLimitMapper.toDto(savedDailyLimit);
    }

    public DailyLimitDTO updateDailyLimit(Long id, DailyLimitDTO dailyLimitDTO, PrincipalDetails principalDetails) {
        DailyLimit existingDailyLimit = dailyLimitRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "DailyLimit not found"));

        existingDailyLimit.setDailyArticleLimit(dailyLimitDTO.getDailyArticleLimit());
        existingDailyLimit.setDailyCommentLimit(dailyLimitDTO.getDailyCommentLimit());
        existingDailyLimit.setDailyArticlePoint(dailyLimitDTO.getDailyArticlePoint());
        existingDailyLimit.setDailyCommentPoint(dailyLimitDTO.getDailyCommentPoint());

        DailyLimit updatedDailyLimit = dailyLimitRepository.save(existingDailyLimit);
        return dailyLimitMapper.toDto(updatedDailyLimit);
    }
}
