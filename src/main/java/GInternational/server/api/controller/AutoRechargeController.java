package GInternational.server.api.controller;

import GInternational.server.api.dto.AutoRechargeDTO;
import GInternational.server.api.entity.AutoRecharge;
import GInternational.server.api.service.AutoRechargeService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AutoRechargeController {

    private final AutoRechargeService autoRechargeService;
    private static final Logger logger = LoggerFactory.getLogger(AutoRechargeController.class);

    @GetMapping("/managers/auto/search")
    public ResponseEntity<List<AutoRechargeDTO>> searchAutoRecharges(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String depositor,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) Long number,
            Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime;

        if (endDate != null) {
            endDateTime = endDate.plusDays(1).atStartOfDay();
        } else if (startDate != null) {
            endDateTime = startDate.plusDays(1).atStartOfDay();
        } else {
            endDateTime = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();
        }

        logger.debug("Controller Start DateTime: {}", startDateTime);
        logger.debug("Controller End DateTime: {}", endDateTime);

        List<AutoRechargeDTO> autoRecharges = autoRechargeService.findAutoRecharges(startDateTime, endDateTime, status, depositor, bankName, number, principalDetails);
        return ResponseEntity.ok(autoRecharges);
    }
}
