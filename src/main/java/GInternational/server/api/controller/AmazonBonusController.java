package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonBonusDTO;
import GInternational.server.api.service.AmazonBonusService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class AmazonBonusController {

    private final AmazonBonusService amazonBonusService;

    /**
     * 보너스 설정 업데이트
     * 첫 충전 보너스율, 일일 첫 충전 보너스율, 충전 보너스율, 일일 충전 한도 업데이트.
     *
     * @param settings 보너스 설정 정보를 담은 DTO
     * @return 설정 업데이트 성공 메시지
     */
    @PostMapping("/managers/amazon/bonus/settings")
    public ResponseEntity<?> updateBonusSettings(@RequestBody AmazonBonusDTO settings,
                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonBonusService.setBonusSettings(
                settings.getFirstRechargeRate(),
                settings.getDailyFirstRechargeRate(),
                settings.getRechargeRate(),
                settings.getDailyRechargeCap(),
                principal
        );
        return ResponseEntity.ok("보너스 설정이 업데이트되었습니다.");
    }
}
