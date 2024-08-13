package GInternational.server.api.service;

import GInternational.server.api.dto.LevelAccountSettingReqDTO;
import GInternational.server.api.dto.LevelAccountSettingResDTO;
import GInternational.server.api.entity.LevelAccountSetting;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.LevelAccountSettingRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LevelAccountSettingService {

    private final LevelAccountSettingRepository levelAccountSettingRepository;
    private final UserRepository userRepository;

    // 레벨별 설정 추가
    public List<LevelAccountSettingResDTO> createLevelAccountSettings(List<LevelAccountSettingReqDTO> reqDTOs, PrincipalDetails principalDetails) {
        List<LevelAccountSettingResDTO> createdSettings = new ArrayList<>();
        for (LevelAccountSettingReqDTO reqDTO : reqDTOs) {
            LevelAccountSetting setting = new LevelAccountSetting();
            setting.setLv(reqDTO.getLv());
            setting.setBankName(reqDTO.getBankName());
            setting.setAccountNumber(reqDTO.getAccountNumber());
            setting.setOwnerName(reqDTO.getOwnerName());
            setting.setCsNumber(reqDTO.getCsNumber());
            LevelAccountSetting savedSetting = levelAccountSettingRepository.save(setting);
            createdSettings.add(convertToResDTO(savedSetting));
        }
        return createdSettings;
    }

    // 레벨별 설정 업데이트
    public List<LevelAccountSettingResDTO> updateLevelAccountSettings(List<LevelAccountSettingReqDTO> reqDTOs, PrincipalDetails principalDetails) {
        List<LevelAccountSettingResDTO> updatedSettings = new ArrayList<>();
        for (LevelAccountSettingReqDTO reqDTO : reqDTOs) {
            LevelAccountSetting setting = levelAccountSettingRepository.findByLv(reqDTO.getLv())
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "해당 레벨의 계좌 설정을 찾을 수 없습니다"));
            setting.setBankName(reqDTO.getBankName());
            setting.setAccountNumber(reqDTO.getAccountNumber());
            setting.setOwnerName(reqDTO.getOwnerName());
            setting.setCsNumber(reqDTO.getCsNumber());
            LevelAccountSetting savedSetting = levelAccountSettingRepository.save(setting);
            updatedSettings.add(convertToResDTO(savedSetting));
        }
        return updatedSettings;
    }

    // 모든 레벨 설정 조회
    public List<LevelAccountSettingResDTO> getAllLevelAccountSettings(PrincipalDetails principalDetails) {
        return levelAccountSettingRepository.findAll().stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    // 특정 레벨 설정 조회
    public LevelAccountSettingResDTO getLevelAccountSettingByLevel(int lv, PrincipalDetails principalDetails) {
        LevelAccountSetting setting = levelAccountSettingRepository.findByLv(lv)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "해당 레벨의 계좌 설정이 존재하지 않습니다."));
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow(()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        if (!user.isAccountVisible()) {
            throw new RestControllerException(ExceptionCode.PERMISSION_DENIED, "고객센터로 문의해주세요.");
        }
        return convertToResDTO(setting);
    }

    private LevelAccountSettingResDTO convertToResDTO(LevelAccountSetting setting) {
        LevelAccountSettingResDTO resDTO = new LevelAccountSettingResDTO();
        resDTO.setId(setting.getId());
        resDTO.setLv(setting.getLv());
        resDTO.setBankName(setting.getBankName());
        resDTO.setAccountNumber(setting.getAccountNumber());
        resDTO.setOwnerName(setting.getOwnerName());
        resDTO.setCsNumber(setting.getCsNumber());
        return resDTO;
    }
}
