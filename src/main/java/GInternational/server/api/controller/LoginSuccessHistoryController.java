package GInternational.server.api.controller;

import GInternational.server.api.entity.LoginSuccessHistory;
import GInternational.server.api.service.LoginSuccessHistoryService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/managers/login-success")
public class LoginSuccessHistoryController {

    private final LoginSuccessHistoryService loginSuccessHistoryService;

    @GetMapping("/histories/{userId}")
    public ResponseEntity<List<LoginSuccessHistory>> getLoginHistories(@PathVariable("userId") @Positive Long userId,
                                                                       @RequestParam(required = false) String loginIp,
                                                                       @RequestParam(required = false) LocalDate startDate,
                                                                       @RequestParam(required = false) LocalDate endDate,
                                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LoginSuccessHistory> histories = loginSuccessHistoryService.findLoginHistories(userId, loginIp, startDate, endDate, principal);
        return ResponseEntity.ok(histories);
    }
}
