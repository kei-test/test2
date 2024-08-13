package GInternational.server.api.service;

import GInternational.server.api.dto.DailyLimitDTO;
import GInternational.server.api.dto.JoinPointDTO;
import GInternational.server.api.entity.DailyLimit;
import GInternational.server.api.entity.JoinPoint;
import GInternational.server.api.mapper.JoinPointMapper;
import GInternational.server.api.repository.JoinPointRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class JoinPointService {

    private final JoinPointRepository joinPointRepository;
    private final JoinPointMapper joinPointMapper;

    public JoinPointDTO createJoinPoint(JoinPointDTO joinPointDTO, PrincipalDetails principalDetails) {
        JoinPoint joinPoint = joinPointMapper.toEntity(joinPointDTO);
        JoinPoint savedJoinPoint = joinPointRepository.save(joinPoint);
        return joinPointMapper.toDto(savedJoinPoint);
    }

    public JoinPointDTO updateJoinPoint(Long id, JoinPointDTO joinPointDTO, PrincipalDetails principalDetails) {
        JoinPoint existingJoinPoint = joinPointRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "DailyLimit not found"));

        existingJoinPoint.setPoint(joinPointDTO.getPoint());

        JoinPoint updatedJoinPoint = joinPointRepository.save(existingJoinPoint);
        return joinPointMapper.toDto(updatedJoinPoint);
    }
}
