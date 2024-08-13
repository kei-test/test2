package GInternational.server.api.controller;

import GInternational.server.api.dto.SuddenRechargeReqDTO;
import GInternational.server.api.dto.SuddenRechargeResDTO;
import GInternational.server.api.service.SuddenRechargeService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/managers/sudden-recharge")
@RequiredArgsConstructor
public class SuddenRechargeController {

    private final SuddenRechargeService suddenRechargeService;

    @PostMapping("/create")
    public ResponseEntity<SuddenRechargeResDTO> createSuddenRecharge(@RequestBody SuddenRechargeReqDTO suddenRechargeReqDTO,
                                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        SuddenRechargeResDTO createdSuddenRecharge = suddenRechargeService.createSuddenRecharge(suddenRechargeReqDTO, principal);
        return ResponseEntity.ok(createdSuddenRecharge);
    }

    @GetMapping("/get")
    public ResponseEntity<List<SuddenRechargeResDTO>> getAllSuddenRecharges(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<SuddenRechargeResDTO> suddenRecharges = suddenRechargeService.getAllSuddenRecharges(principal);
        return ResponseEntity.ok(suddenRecharges);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SuddenRechargeResDTO> updateSuddenRecharge(@PathVariable Long id,
                                                                     @RequestBody SuddenRechargeReqDTO suddenRechargeReqDTO,
                                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        SuddenRechargeResDTO updatedSuddenRecharge = suddenRechargeService.updateSuddenRecharge(id, suddenRechargeReqDTO, principal);
        return ResponseEntity.ok(updatedSuddenRecharge);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSuddenRecharge(@PathVariable Long id,
                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        suddenRechargeService.deleteSuddenRecharge(id, principal);
        return ResponseEntity.noContent().build();
    }
}
