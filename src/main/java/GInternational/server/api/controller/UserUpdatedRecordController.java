package GInternational.server.api.controller;

import GInternational.server.api.entity.UserUpdatedRecord;
import GInternational.server.api.service.UserUpdatedRecordService;
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
import java.util.List;

@RestController
@RequestMapping("/api/v2/managers")
@RequiredArgsConstructor
public class UserUpdatedRecordController {

    private final UserUpdatedRecordService userUpdatedRecordService;

    @GetMapping("/changed-record")
    public ResponseEntity<List<UserUpdatedRecord>> findByConditions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Boolean passwordChanged,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer lv,
            @RequestParam(required = false) String referredBy,
            @RequestParam(required = false) String distributor,
            @RequestParam(required = false) String userGubun,
            @RequestParam(required = false) String store,
            @RequestParam(required = false) Boolean isAmazonUser,
            @RequestParam(required = false) Boolean isDstUser,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        List<UserUpdatedRecord> records = userUpdatedRecordService.findByConditions(
                startDate, endDate, username, nickname, passwordChanged, bankName, email, lv, referredBy, distributor, userGubun, store, isAmazonUser, isDstUser, principal);
        return ResponseEntity.ok(records);
    }
}
