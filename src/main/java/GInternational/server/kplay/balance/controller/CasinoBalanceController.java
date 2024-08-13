package GInternational.server.kplay.balance.controller;

import GInternational.server.kplay.balance.dto.CasinoBalanceRequestDTO;
import GInternational.server.kplay.balance.dto.CasinoBalanceResponseDTO;
import GInternational.server.kplay.balance.repository.CasinoBalanceRepository;
import GInternational.server.kplay.balance.service.CasinoBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CasinoBalanceController {

    private final CasinoBalanceService casinoBalanceService;
    private final CasinoBalanceRepository casinoBalanceRepository;

    /**
     * 카지노 잔액 조회.
     *
     * @param casinoBalanceRequestDTO 카지노 잔액 조회 요청 정보를 담은 DTO
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return ResponseEntity 카지노 잔액 응답 DTO를 담은 ResponseEntity 객체
     */
    @PostMapping("/balance")
    public ResponseEntity calledBalance(@RequestBody CasinoBalanceRequestDTO casinoBalanceRequestDTO,
                                        @RequestHeader ("secret-key") String secretHeader) {
        CasinoBalanceResponseDTO response = casinoBalanceService.calledBalance(casinoBalanceRequestDTO,secretHeader);
        return ResponseEntity.ok(response);
    }
}