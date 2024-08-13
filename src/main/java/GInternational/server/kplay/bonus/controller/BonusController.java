package GInternational.server.kplay.bonus.controller;

import GInternational.server.kplay.bonus.dto.BonusRequestDTO;
import GInternational.server.kplay.bonus.dto.BonusResponseDTO;
import GInternational.server.kplay.bonus.service.BonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BonusController {

    private final BonusService bonusService;

    /**
     * 사용자에게 보너스를 지급.
     *
     * @param bonusRequestDTO 보너스 지급 요청 정보를 담은 DTO
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return ResponseEntity 보너스 지급 응답 DTO를 담은 ResponseEntity 객체
     */
    @PostMapping("/bonus")
    public ResponseEntity calledBonus(@RequestBody BonusRequestDTO bonusRequestDTO,
                                      @RequestHeader ("secret-key") String secretHeader) {
        BonusResponseDTO response = bonusService.calledBonus(bonusRequestDTO,secretHeader);
        return ResponseEntity.ok(response);
    }
}