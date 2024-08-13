package GInternational.server.api.controller;

import GInternational.server.api.dto.AdjustmentDTO;
import GInternational.server.api.service.AdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AdjustmentController {

    private final AdjustmentService adjustmentService;

    @GetMapping("/managers/adjustments/{userId}")
    public ResponseEntity<AdjustmentDTO> getAdjustments(@PathVariable Long userId) {
        AdjustmentDTO adjustments = adjustmentService.calculateAdjustments(userId);
        return ResponseEntity.ok(adjustments);
    }
}
