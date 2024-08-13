package GInternational.server.api.controller;

import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.api.dto.EventsBoardListDTO;
import GInternational.server.api.dto.EventsBoardRequestDTO;
import GInternational.server.api.dto.EventsBoardResponseDTO;
import GInternational.server.api.service.EventsBoardService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class EventsBoardController {

    private final EventsBoardService eventsBoardService;

    /**
     * 이벤트 생성.
     * @param eventsBoardRequestDTO 이벤트 생성 요청 데이터
     * @param authentication 사용자 인증 정보
     * @return 생성된 이벤트 정보
     */
    @PostMapping("/managers/{userId}/events")
    public ResponseEntity<SingleResponseDto<EventsBoardResponseDTO>> insertEvents(@RequestBody EventsBoardRequestDTO eventsBoardRequestDTO,
                                                                                  HttpServletRequest request,
                                                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        EventsBoardResponseDTO response = eventsBoardService.insertEvent(eventsBoardRequestDTO, principal, request);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "이벤트가 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResponseDto<>(response));
    }

    /**
     * 이벤트 수정.
     * @param eventId 수정할 이벤트 ID
     * @param eventsBoardRequestDTO 이벤트 수정 요청 데이터
     * @param authentication 사용자 인증 정보
     * @param request 관리자 ip 정보
     * @return 수정된 이벤트 정보
     */
    @PatchMapping("/managers/{eventId}")
    public ResponseEntity<SingleResponseDto<EventsBoardResponseDTO>> updateEvents(@PathVariable("eventId") @Positive Long eventId,
                                                                                  @RequestBody EventsBoardRequestDTO eventsBoardRequestDTO,
                                                                                  HttpServletRequest request,
                                                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        EventsBoardResponseDTO response = eventsBoardService.updateEvent(eventId, eventsBoardRequestDTO, principal, request);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "이벤트가 수정되었습니다.");
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    /**
     * 모든 이벤트 조회.
     * @param authentication 사용자 인증 정보
     * @return 이벤트 목록
     */
    @GetMapping("/users/get/events")
    public ResponseEntity<SingleResponseDto<List<EventsBoardListDTO>>> getAllEvents(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String site,
            @RequestParam(required = false) String writerNickname,
            @RequestParam(required = false) String writerUsername,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<EventsBoardListDTO> allEvents = eventsBoardService.getAllEvents(principal, startDate, endDate, title, site, writerNickname, writerUsername);
        return ResponseEntity.ok(new SingleResponseDto<>(allEvents));
    }

    /**
     * 특정 이벤트 조회.
     * @param eventId 조회할 이벤트 ID
     * @param authentication 사용자 인증 정보
     * @return 조회된 이벤트 정보
     */
    @GetMapping("/users/{eventId}/event")
    public ResponseEntity<SingleResponseDto<EventsBoardResponseDTO>> getEvent(@PathVariable("eventId") @Positive Long eventId,
                                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        EventsBoardResponseDTO response = eventsBoardService.detailEvent(eventId, principal);
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    /**
     * 이벤트 삭제.
     * @param eventId 삭제할 이벤트 ID
     * @param authentication 사용자 인증 정보
     * @param request 관리자 ip 정보
     * @return 응답 상태 코드
     */
    @DeleteMapping("/managers/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("eventId") @Positive Long eventId,
                                            HttpServletRequest request,
                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        eventsBoardService.deleteEvent(eventId, principal, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 이벤트 상태 변경.
     * @param eventId 상태를 변경할 이벤트 ID
     * @param enabled 변경할 활성화 상태
     * @param authentication 사용자 인증 정보
     * @return 변경된 이벤트 정보
     */
    @PatchMapping("/managers/{eventId}/enabled")
    public ResponseEntity<EventsBoardResponseDTO> changeEventStatus(@PathVariable("eventId") Long eventId,
                                                                    @RequestParam boolean enabled,
                                                                    HttpServletRequest request,
                                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        EventsBoardResponseDTO updatedEvent = eventsBoardService.updateEventStatus(eventId, principal, enabled, request);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(updatedEvent));
        responseBody.put("message", "상태가 변경되었습니다.");
        return ResponseEntity.ok(updatedEvent);
    }
}
