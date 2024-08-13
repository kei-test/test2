package GInternational.server.api.service;

import GInternational.server.api.dto.LevelBetSettingReqDTO;
import GInternational.server.api.dto.LevelBetSettingResDTO;
import GInternational.server.api.entity.LevelBetSetting;
import GInternational.server.api.mapper.LevelBetSettingResMapper;
import GInternational.server.api.repository.LevelBetSettingRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LevelBetSettingService {

    private final LevelBetSettingRepository levelBetSettingRepository;
    private final LevelBetSettingResMapper levelBetSettingResMapper;

    public List<LevelBetSettingResDTO> createOrUpdateAllLevels(List<LevelBetSettingReqDTO> levelBetSettingReqDTOs, PrincipalDetails principalDetails) {
        List<LevelBetSettingResDTO> result = new ArrayList<>();
        for (LevelBetSettingReqDTO reqDTO : levelBetSettingReqDTOs) {
            LevelBetSetting levelBetSetting = levelBetSettingRepository.findByLv(reqDTO.getLv())
                    .orElse(new LevelBetSetting());

            updateNonNullFields(levelBetSetting, reqDTO);

            LevelBetSetting savedLevelBetSetting = levelBetSettingRepository.save(levelBetSetting);
            result.add(levelBetSettingResMapper.toDto(savedLevelBetSetting));
        }
        return result;
    }

    public List<LevelBetSettingResDTO> getAllLevelBetSettings(PrincipalDetails principalDetails) {
        List<LevelBetSetting> levelBetSettings = levelBetSettingRepository.findAll();
        return levelBetSettings.stream()
                .map(levelBetSettingResMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LevelBetSettingResDTO> updateSelectedLevels(List<LevelBetSettingReqDTO> levelBetSettingReqDTOs, PrincipalDetails principalDetails) {
        List<LevelBetSettingResDTO> result = new ArrayList<>();
        for (LevelBetSettingReqDTO reqDTO : levelBetSettingReqDTOs) {
            LevelBetSetting levelBetSetting = levelBetSettingRepository.findByLv(reqDTO.getLv())
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "레벨 설정을 찾을 수 없습니다."));

            updateNonNullFields(levelBetSetting, reqDTO);

            LevelBetSetting updatedLevelBetSetting = levelBetSettingRepository.save(levelBetSetting);
            result.add(levelBetSettingResMapper.toDto(updatedLevelBetSetting));
        }
        return result;
    }

    private void updateNonNullFields(LevelBetSetting target, LevelBetSettingReqDTO source) {
        for (Field field : LevelBetSettingReqDTO.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(source);
                if (value != null) {
                    Field targetField = LevelBetSetting.class.getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException("Failed to update field", e);
            }
        }
    }
}
