package GInternational.server.api.controller;

import GInternational.server.api.dto.AutoRechargeBankAccountDTO;
import GInternational.server.api.service.AutoRechargeBankAccountService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/managers/bank-accounts")
@RequiredArgsConstructor
public class AutoRechargeBankAccountController {

    private final AutoRechargeBankAccountService service;

    @PostMapping("/create")
    public ResponseEntity<AutoRechargeBankAccountDTO> createBankAccount(@RequestBody AutoRechargeBankAccountDTO dto,
                                                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(service.createBankAccount(dto, principal));
    }

    @GetMapping("/get")
    public ResponseEntity<List<AutoRechargeBankAccountDTO>> getAllBankAccounts(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(service.getAllBankAccounts(principal));
    }

    @PutMapping("/{id}/update/usage")
    public ResponseEntity<AutoRechargeBankAccountDTO> updateBankAccountUsage(@PathVariable Long id,
                                                                             @RequestParam Boolean isUse,
                                                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(service.updateBankAccountUsage(id, isUse, principal));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long id,
                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        service.deleteBankAccount(id, principal);
        return ResponseEntity.noContent().build();
    }
}
