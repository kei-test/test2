package GInternational.server.api.controller;

import GInternational.server.api.dto.ExpSettingReqDTO;
import GInternational.server.api.dto.ExpSettingResDTO;
import GInternational.server.api.service.ExpSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class ExpSettingController {

    private final ExpSettingService expSettingService;

    @PostMapping("/managers/exp-settings")
    public ResponseEntity<ExpSettingResDTO> createExpSetting(@RequestBody ExpSettingReqDTO reqDTO) {
        ExpSettingResDTO createdExpSetting = expSettingService.createExpSetting(reqDTO);
        return ResponseEntity.ok(createdExpSetting);
    }

    @PutMapping("/managers/exp-settings/update/{id}")
    public ResponseEntity<ExpSettingResDTO> updateExpSetting(@PathVariable Long id, @RequestBody ExpSettingReqDTO reqDTO) {
        ExpSettingResDTO updatedExpSetting = expSettingService.updateExpSetting(id, reqDTO);
        return ResponseEntity.ok(updatedExpSetting);
    }

    @GetMapping("/managers/exp-settings")
    public ResponseEntity<List<ExpSettingResDTO>> getAllExpSettings() {
        List<ExpSettingResDTO> expSettings = expSettingService.getAllExpSettings();
        return ResponseEntity.ok(expSettings);
    }
}
