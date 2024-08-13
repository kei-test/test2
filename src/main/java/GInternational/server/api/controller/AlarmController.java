package GInternational.server.api.controller;

import GInternational.server.api.dto.AlarmReqDTO;
import GInternational.server.api.dto.AlarmResDTO;
import GInternational.server.api.service.AlarmService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/managers/alarms/create")
    public ResponseEntity<AlarmResDTO> createAlarm(@RequestBody AlarmReqDTO alarmReqDTO,
                                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AlarmResDTO createdAlarm = alarmService.createAlarm(alarmReqDTO, principal);
        return new ResponseEntity<>(createdAlarm, HttpStatus.CREATED);
    }

    @GetMapping("/managers/alarms/get")
    public ResponseEntity<List<AlarmResDTO>> getAllAlarms(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AlarmResDTO> alarms = alarmService.getAllAlarm(principal);
        return new ResponseEntity<>(alarms, HttpStatus.OK);
    }

    @PutMapping("/managers/alarms/{id}")
    public ResponseEntity<AlarmResDTO> updateAlarm(@PathVariable Long id,
                                                   @RequestBody AlarmReqDTO alarmReqDTO,
                                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AlarmResDTO updatedAlarm = alarmService.updateAlarm(id, alarmReqDTO, principal);
        return ResponseEntity.ok(updatedAlarm);
    }

    @DeleteMapping("/managers/alarms/delete/{id}")
    public ResponseEntity<Void> deleteAlarmById(@PathVariable Long id,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        alarmService.deleteAlarmById(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/managers/alarms/{id}/clear-sound")
    public ResponseEntity<AlarmResDTO> clearAlarmSound(@PathVariable Long id,
                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AlarmResDTO updatedAlarm = alarmService.clearAlarmSound(id, principal);
        return ResponseEntity.ok(updatedAlarm);
    }
}