package GInternational.server.api.controller;

import GInternational.server.api.dto.MoneyLogResponseDTO;
import GInternational.server.api.service.MoneyLogService;
import GInternational.server.api.vo.MoneyLogCategoryEnum;

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
@RequestMapping("/api/v2/managers/money-log")
@RequiredArgsConstructor
public class MoneyLogController {

    private final MoneyLogService moneyLogService;

    /**
     * 모든 머니 내역을 조회하거나, 필요한 경우 카테고리별로 조회.
     * @param userId     사용자 ID (옵션)
     * @param category   카테고리 (옵션)
     * @param startDate  시작 날짜 (옵션)
     * @param endDate    종료 날짜 (옵션)
     * @param username   사용자 이름 (옵션)
     * @param nickname   닉네임 (옵션)
     * @param distributor 총판 (옵션)
     * @param store      매장 (옵션)
     * @param authentication 인증 객체
     * @return ResponseEntity 머니 적립 내역 목록을 담은 응답
     */
    @GetMapping
    public ResponseEntity<List<MoneyLogResponseDTO>> getAllMoneyLog(@RequestParam(required = false) Long userId,
                                                                    @RequestParam(required = false) MoneyLogCategoryEnum category,
                                                                    @RequestParam(required = false) Long usedSportsBalance,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                    @RequestParam(required = false) String username,
                                                                    @RequestParam(required = false) String nickname,
                                                                    @RequestParam(required = false) String distributor,
                                                                    @RequestParam(required = false) String store,
                                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<MoneyLogResponseDTO> transactions = moneyLogService.getAllMoneyTrackingTransactions(principal, userId, category, usedSportsBalance, startDate, endDate, username, nickname, distributor, store);
        return ResponseEntity.ok(transactions);
    }
}

