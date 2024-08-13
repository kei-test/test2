package GInternational.server.api.controller;

import GInternational.server.api.dto.WhiteIpRequestDTO;
import GInternational.server.api.dto.WhiteIpResponseDTO;
import GInternational.server.api.service.WhiteIpService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class WhiteIpController {

    private final WhiteIpService whiteIpService;

    // 화이트 IP 추가
    @PostMapping("/admins/white-ip/add")
    public ResponseEntity<WhiteIpResponseDTO> addWhiteIp(@RequestBody WhiteIpRequestDTO whiteIpRequestDTO,
                                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        WhiteIpResponseDTO responseDTO = whiteIpService.addWhiteIp(whiteIpRequestDTO, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // 화이트 IP 수정 (memoStatus 및 memo 내용)
    @PutMapping("/admins/white-ip/update/{id}")
    public ResponseEntity<WhiteIpResponseDTO> updateWhiteIp(@PathVariable Long id,
                                                            @RequestBody WhiteIpRequestDTO whiteIpRequestDTO,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        WhiteIpResponseDTO responseDTO = whiteIpService.updateWhiteIp(id, whiteIpRequestDTO, principal);
        return ResponseEntity.ok(responseDTO);
    }

    // 화이트 IP 목록 조회
    @GetMapping("/admins/white-ip/get-all")
    public ResponseEntity<List<WhiteIpResponseDTO>> findAllWhiteIps(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<WhiteIpResponseDTO> responseDTOs = whiteIpService.findAllWhiteIps(principal);
        return ResponseEntity.ok(responseDTOs);
    }

    // 화이트 IP 삭제
    @DeleteMapping("/admins/white-ip/{id}")
    public ResponseEntity<Void> deleteWhiteIp(@PathVariable Long id,
                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        whiteIpService.deleteWhiteIp(id, principal);
        return ResponseEntity.noContent().build();
    }
}
