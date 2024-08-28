package GInternational.server.api.controller;

import GInternational.server.api.dto.DummyExchangeResDTO;
import GInternational.server.api.dto.DummyJackPotReqDTO;
import GInternational.server.api.dto.DummyJackPotResDTO;
import GInternational.server.api.mapper.DummyJackPotResMapper;
import GInternational.server.api.repository.DummyJackPotRepository;
import GInternational.server.api.service.DummyExchangeService;
import GInternational.server.api.service.DummyJackPotService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/managers/dummy-exchange")
@RequiredArgsConstructor
public class DummyExchangeController {

    private final DummyExchangeService dummyExchangeService;

    /**
     * 최근 생성된 5개의 더미 환전 기록 조회
     *
     * @return 최근 5개의 DummyExchangeResDTO 리스트
     */
    @GetMapping("/get")
    public ResponseEntity<List<DummyExchangeResDTO>> getLastFiveDummyExchanges(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<DummyExchangeResDTO> recentExchanges = dummyExchangeService.getLastFiveDummyExchanges(principal);
        return ResponseEntity.ok(recentExchanges);
    }
}
