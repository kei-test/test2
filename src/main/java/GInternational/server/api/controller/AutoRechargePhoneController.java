package GInternational.server.api.controller;

import GInternational.server.api.dto.AutoRechargePhoneDTO;
import GInternational.server.api.service.AutoRechargePhoneService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/managers/phones")
@RequiredArgsConstructor
public class AutoRechargePhoneController {

    private final AutoRechargePhoneService autoRechargePhoneService;

    @PostMapping("/create")
    public ResponseEntity<AutoRechargePhoneDTO> createPhone(@RequestBody AutoRechargePhoneDTO phoneDTO,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AutoRechargePhoneDTO createdPhone = autoRechargePhoneService.createPhone(phoneDTO, principal);
        return ResponseEntity.ok(createdPhone);
    }

    @GetMapping("/get")
    public ResponseEntity<List<AutoRechargePhoneDTO>> getAllPhones(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AutoRechargePhoneDTO> phones = autoRechargePhoneService.getAllPhones(principal);
        return ResponseEntity.ok(phones);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<AutoRechargePhoneDTO> updatePhone(@PathVariable Long id,
                                                            @RequestBody AutoRechargePhoneDTO phoneDTO,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AutoRechargePhoneDTO updatedPhone = autoRechargePhoneService.updatePhone(id, phoneDTO, principal);
        return ResponseEntity.ok(updatedPhone);
    }


    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deletePhone(@PathVariable Long id,
                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        autoRechargePhoneService.deletePhone(id, principal);
        return ResponseEntity.noContent().build();
    }
}
