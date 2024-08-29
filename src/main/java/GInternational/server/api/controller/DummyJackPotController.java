package GInternational.server.api.controller;

import GInternational.server.api.dto.DummyJackPotReqDTO;
import GInternational.server.api.dto.DummyJackPotResDTO;
import GInternational.server.api.entity.DummyJackPot;
import GInternational.server.api.mapper.DummyJackPotResMapper;
import GInternational.server.api.repository.DummyJackPotRepository;
import GInternational.server.api.service.DummyJackPotService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class DummyJackPotController {

    private final DummyJackPotService dummyJackPotService;
    private final DummyJackPotRepository dummyJackPotRepository;
    private final DummyJackPotResMapper dummyJackPotResMapper;

    // 수동 데이터 생성
    @PostMapping("/managers/jackpot/create")
    public ResponseEntity<DummyJackPotResDTO> createManualJackpotData(@RequestBody DummyJackPotReqDTO dummyJackPotReqDTO,
                                                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        DummyJackPotResDTO response = dummyJackPotService.createManualJackpotData(dummyJackPotReqDTO, principal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/jackpot/all")
    public ResponseEntity<List<DummyJackPotResDTO>> getAllJackpotData() {
        List<DummyJackPotResDTO> jackpotData = dummyJackPotService.getAllJackpotData();
        return new ResponseEntity<>(jackpotData, HttpStatus.OK);
    }

    // 업데이트
    @PutMapping("/managers/jackpot/update/{id}")
    public ResponseEntity<DummyJackPotResDTO> updateJackpotData(@PathVariable Long id,
                                                                @RequestBody DummyJackPotReqDTO dummyJackPotReqDTO,
                                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        DummyJackPotResDTO response = dummyJackPotService.updateJackpotData(id, dummyJackPotReqDTO, principal);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 데이터 삭제
    @DeleteMapping("/managers/jackpot/delete/{id}")
    public ResponseEntity<Void> deleteJackpotData(@PathVariable Long id,
                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        dummyJackPotService.deleteJackpotData(id, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
