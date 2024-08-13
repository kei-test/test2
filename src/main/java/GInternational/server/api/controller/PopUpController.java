package GInternational.server.api.controller;

import GInternational.server.api.dto.PopUpListDTO;
import GInternational.server.api.dto.PopUpRequestDTO;
import GInternational.server.api.dto.PopUpResponseDTO;
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
public class PopUpController {

    private final PopUpService popUpService;

    @PostMapping("/managers/pop-up/add")
    public ResponseEntity<PopUpResponseDTO> addPopUp(@RequestBody PopUpRequestDTO requestDTO,
                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PopUpResponseDTO responseDTO = popUpService.addPopUp(requestDTO, principal);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/managers/pop-up/update/{id}")
    public ResponseEntity<PopUpResponseDTO> updatePopUp(@PathVariable Long id,
                                                        @RequestBody PopUpRequestDTO requestDTO,
                                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PopUpResponseDTO responseDTO = popUpService.updatePopUp(id, requestDTO, principal);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/managers/pop-up/delete/{id}")
    public ResponseEntity<Void> deletePopUp(@PathVariable Long id,
                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        popUpService.deletePopUp(id, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/managers/pop-up/details/{id}")
    public ResponseEntity<PopUpResponseDTO> getPopUpDetails(@PathVariable Long id,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PopUpResponseDTO responseDTO = popUpService.getPopUpDetails(id, principal);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/users/pop-up/list")
    public ResponseEntity<List<PopUpListDTO>> getAllPopUps(@RequestParam Optional<PopUpStatusEnum> status,
                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<PopUpListDTO> popUps = popUpService.getAllPopUps(status, principal);
        return ResponseEntity.ok(popUps);
    }

    @PutMapping("/managers/pop-up/status/{id}")
    public ResponseEntity<PopUpResponseDTO> updatePopUpStatus(@PathVariable Long id,
                                                              @RequestParam PopUpStatusEnum status,
                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PopUpResponseDTO responseDTO = popUpService.updatePopUpStatus(id, status, principal);
        return ResponseEntity.ok(responseDTO);
    }
}
