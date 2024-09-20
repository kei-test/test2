package GInternational.server.api.controller;

import GInternational.server.api.dto.ExpSettingReqDTO;
import GInternational.server.api.dto.ExpSettingResDTO;
import GInternational.server.api.service.ExpSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class ExpSettingController {

    private final ExpSettingService expSettingService;

    @PostMapping("/managers/exp-settings")
    public ResponseEntity<List<ExpSettingResDTO>> createExpSettings(@RequestBody List<ExpSettingReqDTO> reqDTOList) {
        List<ExpSettingResDTO> createdExpSettings = expSettingService.createExpSettings(reqDTOList);
        return ResponseEntity.ok(createdExpSettings);
    }

    @PutMapping("/managers/exp-settings/update")
    public ResponseEntity<List<ExpSettingResDTO>> updateExpSetting(@RequestBody List<ExpSettingReqDTO> reqDTOList) {
        List<ExpSettingResDTO> updatedExpSettings = expSettingService.updateExpSettings(reqDTOList);
        return ResponseEntity.ok(updatedExpSettings);
    }

    @GetMapping("/managers/exp-settings")
    public ResponseEntity<List<ExpSettingResDTO>> getAllExpSettings() {
        List<ExpSettingResDTO> expSettings = expSettingService.getAllExpSettings();
        return ResponseEntity.ok(expSettings);
    }
}
