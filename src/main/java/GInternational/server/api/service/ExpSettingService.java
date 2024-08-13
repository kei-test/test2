package GInternational.server.api.service;

import GInternational.server.api.dto.ExpSettingReqDTO;
import GInternational.server.api.dto.ExpSettingResDTO;
import GInternational.server.api.entity.ExpSetting;
import GInternational.server.api.repository.ExpSettingRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class ExpSettingService {

    private final ExpSettingRepository expSettingRepository;

    public ExpSettingResDTO createExpSetting(ExpSettingReqDTO reqDTO) {
        ExpSetting expSetting = new ExpSetting();
        expSetting.setMinExp(reqDTO.getMinExp());
        expSetting.setMaxExp(reqDTO.getMaxExp());
        expSetting.setLv(reqDTO.getLv());
        ExpSetting savedExpSetting = expSettingRepository.save(expSetting);
        return new ExpSettingResDTO(savedExpSetting.getId(), savedExpSetting.getMinExp(), savedExpSetting.getMaxExp(), savedExpSetting.getLv());
    }

    public ExpSettingResDTO updateExpSetting(Long id, ExpSettingReqDTO reqDTO) {
        ExpSetting expSetting = expSettingRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));
        expSetting.setMinExp(reqDTO.getMinExp());
        expSetting.setMaxExp(reqDTO.getMaxExp());
        expSetting.setLv(reqDTO.getLv());
        ExpSetting updatedExpSetting = expSettingRepository.save(expSetting);
        return new ExpSettingResDTO(updatedExpSetting.getId(), updatedExpSetting.getMinExp(), updatedExpSetting.getMaxExp(), updatedExpSetting.getLv());
    }

    public List<ExpSettingResDTO> getAllExpSettings() {
        List<ExpSetting> expSettings = expSettingRepository.findAll();
        return expSettings.stream()
                .map(expSetting -> new ExpSettingResDTO(expSetting.getId(), expSetting.getMinExp(), expSetting.getMaxExp(), expSetting.getLv()))
                .collect(Collectors.toList());
    }
}
