package GInternational.server.api.controller;

import GInternational.server.api.dto.TransformStatisticResDTO;
import GInternational.server.api.service.TransformStatisticService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/v2/managers")
@RequiredArgsConstructor
public class TransformStatisticController {

    private final TransformStatisticService transformStatisticService;

    @GetMapping("/transform/statistics")
    public ResponseEntity<TransformStatisticResDTO> getMonthlyStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                                                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDate startOfMonth = month.atDay(1);
        TransformStatisticResDTO statistics = transformStatisticService.calculateMonthlyStatistics(startOfMonth, principal);
        return ResponseEntity.ok(statistics);
    }
}
