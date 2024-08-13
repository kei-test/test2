package GInternational.server.api.controller;

import GInternational.server.api.dto.FlowNoticeRequestDTO;
import GInternational.server.api.dto.FlowNoticeResponseDTO;
import GInternational.server.api.service.FlowNoticeService;
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
public class FlowNoticeController {

    private final FlowNoticeService flowNoticeService;

    // 흐르는 공지 생성
    @PostMapping("/managers/flow/notices/create")
    public ResponseEntity<FlowNoticeResponseDTO> createFlowNotice(@RequestBody FlowNoticeRequestDTO requestDTO,
                                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        FlowNoticeResponseDTO responseDTO = flowNoticeService.createFlowNotice(requestDTO, principal);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 흐르는 공지 조회
    @GetMapping("/users/flow/notices/search/{id}")
    public ResponseEntity<FlowNoticeResponseDTO> getFlowNotice(@PathVariable Long id,
                                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        FlowNoticeResponseDTO responseDTO = flowNoticeService.getFlowNotice(id, principal);
        return ResponseEntity.ok(responseDTO);
    }
    
    // 모든 흐르는 공지 조회
    @GetMapping("/users/flow/notices/all")
    public ResponseEntity<List<FlowNoticeResponseDTO>> getAllFlowNotices(Authentication authentication) {
        List<FlowNoticeResponseDTO> responseDTOs = flowNoticeService.getAllFlowNotices();
        return ResponseEntity.ok(responseDTOs);
    }

    // 흐르는 공지 수정
    @PutMapping("/managers/flow/notices/update/{id}")
    public ResponseEntity<FlowNoticeResponseDTO> updateFlowNotice(@PathVariable Long id, @RequestBody FlowNoticeRequestDTO requestDTO,
                                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        FlowNoticeResponseDTO responseDTO = flowNoticeService.updateFlowNotice(id, requestDTO, principal);
        return ResponseEntity.ok(responseDTO);
    }

    // 흐르는 공지 삭제
    @DeleteMapping("/managers/flow/notices/delete/{id}")
    public ResponseEntity<Void> deleteFlowNotice(@PathVariable Long id,
                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        flowNoticeService.deleteFlowNotice(id, principal);
        return ResponseEntity.noContent().build();
    }
}