package GInternational.server.api.service;

import GInternational.server.api.dto.LevelBonusPointSettingReqDTO;
import GInternational.server.api.dto.LevelBonusPointSettingResDTO;
import GInternational.server.api.entity.LevelBonusPointSetting;
import GInternational.server.api.repository.LevelBonusPointSettingRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LevelBonusPointSettingService {

    private final LevelBonusPointSettingRepository levelBonusPointSettingRepository;

    /**
     * 1~10 레벨에 대한 설정을 일괄적으로 생성.
     *
     * @param reqDTOList 요청 DTO 목록
     * @param principalDetails 인증된 사용자 정보
     * @return 생성된 설정의 응답 DTO 목록
     */
    public List<LevelBonusPointSettingResDTO> createBulkSettingsForLevels(List<LevelBonusPointSettingReqDTO> reqDTOList,
                                                                          PrincipalDetails principalDetails) {
        List<LevelBonusPointSettingResDTO> resDTOList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            final int level = i;
            LevelBonusPointSettingReqDTO reqDTO = reqDTOList.stream()
                    .filter(dto -> dto.getLv() == level)
                    .findFirst()
                    .orElse(null);
            if (reqDTO != null) {
                LevelBonusPointSetting setting = new LevelBonusPointSetting();
                BeanUtils.copyProperties(reqDTO, setting);
                LevelBonusPointSetting savedSetting = levelBonusPointSettingRepository.save(setting);
                LevelBonusPointSettingResDTO resDTO = new LevelBonusPointSettingResDTO();
                BeanUtils.copyProperties(savedSetting, resDTO);
                resDTOList.add(resDTO);
            }
        }
        return resDTOList;
    }

    /**
     * 지정된 유저의 설정을 일괄적으로 업데이트.
     *
     * @param reqDTOList 요청 DTO 목록
     * @param principalDetails 인증된 사용자 정보
     * @return 업데이트된 설정의 응답 DTO 목록
     */
    public List<LevelBonusPointSettingResDTO> updateBulkSettingsForLevels(List<LevelBonusPointSettingReqDTO> reqDTOList,
                                                                          PrincipalDetails principalDetails) {
        List<LevelBonusPointSettingResDTO> resDTOList = new ArrayList<>();
        for (LevelBonusPointSettingReqDTO reqDTO : reqDTOList) {
            LevelBonusPointSetting setting = levelBonusPointSettingRepository.findById(reqDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Setting not found for ID: " + reqDTO.getId()));

            if (reqDTO.getLv() != 0) setting.setLv(reqDTO.getLv());
            if (reqDTO.getFirstRecharge() != 0) setting.setFirstRecharge(reqDTO.getFirstRecharge());
            if (reqDTO.getTodayRecharge() != 0) setting.setTodayRecharge(reqDTO.getTodayRecharge());
            if (reqDTO.getLossAmount() != 0) setting.setLossAmount(reqDTO.getLossAmount());
            if (reqDTO.getReferrerLossAmount() != 0) setting.setReferrerLossAmount(reqDTO.getReferrerLossAmount());

            LevelBonusPointSetting updatedSetting = levelBonusPointSettingRepository.save(setting);
            LevelBonusPointSettingResDTO resDTO = new LevelBonusPointSettingResDTO();
            BeanUtils.copyProperties(updatedSetting, resDTO);
            resDTOList.add(resDTO);
        }
        return resDTOList;
    }

    /**
     * 모든 레벨 설정 조회
     *
     * @param principalDetails 인증된 사용자 정보
     * @return 모든 설정의 응답 DTO 목록
     */
    public List<LevelBonusPointSettingResDTO> findAllSettings(PrincipalDetails principalDetails) {
        List<LevelBonusPointSetting> settings = levelBonusPointSettingRepository.findAll();
        return settings.stream()
                .map(setting -> {
                    LevelBonusPointSettingResDTO resDTO = new LevelBonusPointSettingResDTO();
                    BeanUtils.copyProperties(setting, resDTO);
                    return resDTO;
                }).collect(Collectors.toList());
    }

    /**
     * 1~10 레벨 중 입력된 레벨의 보너스 적용 여부만 false로 설정.
     *
     * @param levelIds 업데이트할 레벨 ID 목록
     * @param principalDetails 인증된 사용자 정보
     * @return 업데이트된 설정의 응답 DTO 목록
     */
    public List<LevelBonusPointSettingResDTO> updateBonusActivation(List<Long> levelIds, PrincipalDetails principalDetails) {
        List<LevelBonusPointSettingResDTO> resDTOList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            final Long levelId = (long) i;
            LevelBonusPointSetting setting = levelBonusPointSettingRepository.findById(levelId)
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));

            setting.setBonusActive(!levelIds.contains(levelId));

            LevelBonusPointSetting updatedSetting = levelBonusPointSettingRepository.save(setting);
            LevelBonusPointSettingResDTO resDTO = new LevelBonusPointSettingResDTO();
            BeanUtils.copyProperties(updatedSetting, resDTO);
            resDTOList.add(resDTO);
        }
        return resDTOList;
    }
}
