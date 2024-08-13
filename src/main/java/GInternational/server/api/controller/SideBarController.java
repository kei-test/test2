package GInternational.server.api.controller;

import GInternational.server.api.dto.SideBar1DTO;
import GInternational.server.api.dto.SideBar2DTO;
import GInternational.server.api.dto.SideBar3DTO;
import GInternational.server.api.service.SideBarService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class SideBarController {

    private final SideBarService sideBarService;

    @GetMapping("/managers/sidebar1")
    public ResponseEntity<SideBar1DTO> getSidebarInfo(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        SideBar1DTO sideBarInfo = sideBarService.calculate1(principal);
        return ResponseEntity.ok(sideBarInfo);
    }

    @GetMapping("/managers/sidebar2")
    public ResponseEntity<SideBar2DTO> getSidebarDetailedInfo(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        SideBar2DTO sideBarDetailedInfo = sideBarService.calculate2(principal);
        return ResponseEntity.ok(sideBarDetailedInfo);
    }

    @GetMapping("/managers/sidebar3")
    public ResponseEntity<SideBar3DTO> getSidebarExtraInfo(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        SideBar3DTO sideBarExtraInfo = sideBarService.calculate3(principal);
        return ResponseEntity.ok(sideBarExtraInfo);
    }
}
