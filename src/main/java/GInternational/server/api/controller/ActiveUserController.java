package GInternational.server.api.controller;

import GInternational.server.api.dto.ActiveUserResponseDTO;
import GInternational.server.api.service.ActiveUserService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class ActiveUserController {

    private final ActiveUserService activeUserService;

    /**
     * 최근 로그인한 사용자 목록 조회
     *
     * @param authentication 사용자 인증 정보
     * @return 최근 로그인한 사용자 목록
     */
    @GetMapping("/managers/active/find/all")
    public ResponseEntity<List<ActiveUserResponseDTO>> getTop30RecentlyLoggedInUsers(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<ActiveUserResponseDTO> activeUsers = activeUserService.findTop30RecentlyLoggedInUsers(principal);
        return new ResponseEntity<>(activeUsers, HttpStatus.OK);
    }
}
