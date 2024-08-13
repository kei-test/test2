package GInternational.server.api.service;

import GInternational.server.api.dto.LevelPointLimitDTO;
import GInternational.server.api.entity.LevelPointLimit;
import GInternational.server.api.mapper.LevelPointLimitMapper;
import GInternational.server.api.repository.LevelPointLimitRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LevelPointLimitService {

    private final LevelPointLimitRepository levelPointLimitRepository;
    private final LevelPointLimitMapper levelPointLimitMapper;

    /**
     * 레벨별 포인트 한도 설정을 생성.
     *
     * @param levelPointLimitDTO 생성할 레벨별 포인트 한도 데이터
     * @return 생성된 레벨별 포인트 한도
     */
    public LevelPointLimitDTO createLevelPointLimit(LevelPointLimitDTO levelPointLimitDTO, PrincipalDetails principalDetails) {
        // 항상 ID 1로 생성
        levelPointLimitDTO.setId(1L);
        LevelPointLimit levelPointLimit = levelPointLimitMapper.toEntity(levelPointLimitDTO);
        LevelPointLimit savedEntity = levelPointLimitRepository.save(levelPointLimit);
        return levelPointLimitMapper.toDto(savedEntity);
    }

    /**
     * 레벨별 포인트 한도 설정을 업데이트.
     * 값이 있는 부분만 업데이트하고, 나머지는 기존 값을 유지.
     *
     * @param levelPointLimitDTO 업데이트할 레벨별 포인트 한도 데이터
     * @return 업데이트된 레벨별 포인트 한도
     */
    public LevelPointLimitDTO updateLevelPointLimit(LevelPointLimitDTO levelPointLimitDTO, PrincipalDetails principalDetails) {
        LevelPointLimit existingEntity = levelPointLimitRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "레벨 포인트 한도 설정을 찾을 수 없습니다."));

        // DTO의 값이 있는 필드만 업데이트
        if (levelPointLimitDTO.getLevel1() != 0) {
            existingEntity.setLevel1(levelPointLimitDTO.getLevel1());
        }
        if (levelPointLimitDTO.getLevel2() != 0) {
            existingEntity.setLevel2(levelPointLimitDTO.getLevel2());
        }
        if (levelPointLimitDTO.getLevel3() != 0) {
            existingEntity.setLevel3(levelPointLimitDTO.getLevel3());
        }
        if (levelPointLimitDTO.getLevel4() != 0) {
            existingEntity.setLevel4(levelPointLimitDTO.getLevel4());
        }
        if (levelPointLimitDTO.getLevel5() != 0) {
            existingEntity.setLevel5(levelPointLimitDTO.getLevel5());
        }
        if (levelPointLimitDTO.getLevel6() != 0) {
            existingEntity.setLevel6(levelPointLimitDTO.getLevel6());
        }
        if (levelPointLimitDTO.getLevel7() != 0) {
            existingEntity.setLevel7(levelPointLimitDTO.getLevel7());
        }
        if (levelPointLimitDTO.getLevel8() != 0) {
            existingEntity.setLevel8(levelPointLimitDTO.getLevel8());
        }
        if (levelPointLimitDTO.getLevel9() != 0) {
            existingEntity.setLevel9(levelPointLimitDTO.getLevel9());
        }
        if (levelPointLimitDTO.getLevel10() != 0) {
            existingEntity.setLevel10(levelPointLimitDTO.getLevel10());
        }

        LevelPointLimit updatedEntity = levelPointLimitRepository.save(existingEntity);
        return levelPointLimitMapper.toDto(updatedEntity);
    }
}
