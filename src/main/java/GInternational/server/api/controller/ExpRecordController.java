package GInternational.server.api.controller;

import GInternational.server.api.dto.ExpRecordResponseDTO;
import GInternational.server.api.service.ExpRecordService;
import GInternational.server.api.vo.ExpRecordEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class ExpRecordController {

    private final ExpRecordService expRecordService;

    /**
     * EXP 기록을 조회.
     *
     * @param username 사용자 이름 (옵션)
     * @param nickname 닉네임 (옵션)
     * @param content 내용 (옵션)
     * @param authentication 인증 정보
     * @return 조회된 ExpRecordResponseDTO 리스트를 담은 ResponseEntity 객체
     */
    @GetMapping("/exp/record/all")
    public ResponseEntity<List<ExpRecordResponseDTO>> getExpRecords(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) ExpRecordEnum content,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<ExpRecordResponseDTO> records = expRecordService.findExpRecords(username, nickname, content, principal);
        return ResponseEntity.ok(records);
    }
}
