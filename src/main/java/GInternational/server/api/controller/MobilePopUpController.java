package GInternational.server.api.controller;

import GInternational.server.api.dto.PopUpListDTO;
import GInternational.server.api.dto.PopUpRequestDTO;
import GInternational.server.api.dto.PopUpResponseDTO;
import GInternational.server.api.service.MobilePopUpService;
import GInternational.server.api.service.PopUpService;
import GInternational.server.api.vo.PopUpStatusEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class MobilePopUpController {

    private final MobilePopUpService mobilePopUpService;

    @PostMapping("/managers/mobile-pop-up/add")
    public ResponseEntity<PopUpResponseDTO> addMobilePopUp(@RequestBody PopUpRequestDTO requestDTO,
                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PopUpResponseDTO responseDTO = mobilePopUpService.addMobilePopUp(requestDTO, principal);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/managers/mobile-pop-up/update/{id}")
    public ResponseEntity<PopUpResponseDTO> updateMobilePopUp(@PathVariable Long id,
                                                              @RequestBody PopUpRequestDTO requestDTO,
                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PopUpResponseDTO responseDTO = mobilePopUpService.updateMobilePopUp(id, requestDTO, principal);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/managers/mobile-pop-up/delete/{id}")
    public ResponseEntity<Void> deleteMobilePopUp(@PathVariable Long id,
                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        mobilePopUpService.deleteMobilePopUp(id, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/managers/mobile-pop-up/details/{id}")
    public ResponseEntity<PopUpResponseDTO> getMobilePopUpDetails(@PathVariable Long id,
                                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PopUpResponseDTO responseDTO = mobilePopUpService.getMobilePopUpDetails(id, principal);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/managers/mobile-pop-up/list")
    public ResponseEntity<List<PopUpListDTO>> getAllMobilePopUps(@RequestParam Optional<PopUpStatusEnum> status,
                                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<PopUpListDTO> popUps = mobilePopUpService.getAllMobilePopUps(status, principal);
        return ResponseEntity.ok(popUps);
    }

    @PutMapping("/managers/mobile-pop-up/status/{id}")
    public ResponseEntity<PopUpResponseDTO> updateMobilePopUpStatus(@PathVariable Long id,
                                                                    @RequestParam PopUpStatusEnum status,
                                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PopUpResponseDTO responseDTO = mobilePopUpService.updateMobilePopUpStatus(id, status, principal);
        return ResponseEntity.ok(responseDTO);
    }
}
