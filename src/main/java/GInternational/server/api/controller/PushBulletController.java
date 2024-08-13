package GInternational.server.api.controller;

import GInternational.server.api.service.PushBulletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class PushBulletController {

    private final PushBulletService pushBulletService;

    @GetMapping("/startWebSocket")
    public ResponseEntity<String> startWebSocket() {
        pushBulletService.startWebSocket();
        return ResponseEntity.ok("WebSocket 연결이 시작되었습니다.");
    }
}
