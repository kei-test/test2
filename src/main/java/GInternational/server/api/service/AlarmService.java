package GInternational.server.api.service;

import GInternational.server.api.dto.AlarmReqDTO;
import GInternational.server.api.dto.AlarmResDTO;
import GInternational.server.api.entity.Alarm;
import GInternational.server.api.entity.ExpSetting;
import GInternational.server.api.mapper.AlarmResMapper;
import GInternational.server.api.repository.AlarmRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final AlarmResMapper alarmResMapper;

    public AlarmResDTO createAlarm(AlarmReqDTO alarmReqDTO, PrincipalDetails principalDetails) {
        Alarm alarm = new Alarm();
        alarm.setAlarmEnum(alarmReqDTO.getAlarmEnum());
        alarm.setSound(alarmReqDTO.getSound());
        Alarm savedAlarm = alarmRepository.save(alarm);
        return alarmResMapper.toDto(savedAlarm);
    }

    public List<AlarmResDTO> getAllAlarm(PrincipalDetails principalDetails) {
        List<Alarm> alarms = alarmRepository.findAll();
        return alarms.stream()
                .map(alarmResMapper::toDto)
                .collect(Collectors.toList());
    }

    public AlarmResDTO updateAlarm(Long id, AlarmReqDTO alarmReqDTO, PrincipalDetails principalDetails) {
        Alarm alarm = alarmRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "알람을 찾을 수 없습니다."));

        if (alarmReqDTO.getAlarmEnum() != null) {
            alarm.setAlarmEnum(alarmReqDTO.getAlarmEnum());
        }

        if (alarmReqDTO.getSound() != null) {
            alarm.setSound(alarmReqDTO.getSound());
        }

        Alarm updatedAlarm = alarmRepository.save(alarm);
        return alarmResMapper.toDto(updatedAlarm);
    }

    public void deleteAlarmById(Long id, PrincipalDetails principalDetails) {
        if (alarmRepository.existsById(id)) {
            alarmRepository.deleteById(id);
        } else {
            throw new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "알람을 찾을 수 없습니다.");
        }
    }

    public AlarmResDTO clearAlarmSound(Long id, PrincipalDetails principalDetails) {
        Alarm alarm = alarmRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "알람을 찾을 수 없습니다."));
        alarm.setSound("");
        Alarm updatedAlarm = alarmRepository.save(alarm);
        return alarmResMapper.toDto(updatedAlarm);
    }
}
