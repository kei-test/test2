package GInternational.server.api.controller;


import GInternational.server.api.dto.AmazonMessageListResponseDTO;
import GInternational.server.api.dto.AmazonMessageRequestDTO;
import GInternational.server.api.dto.AmazonMessageResponseDTO;
import GInternational.server.api.entity.AmazonMessages;
import GInternational.server.api.mapper.AmazonMessageResponseMapper;
import GInternational.server.api.service.AmazonMessageService;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class AmazonMessageController {

    private final AmazonMessageService messageService;
    private final AmazonMessageResponseMapper messageResponseMapper;

    /**
     * 쪽지 보내기.
     *
     * @param messageRequestDTO 쪽지 보내기 요청에 필요한 데이터를 담은 DTO
     * @param request HTTP 요청 정보
     * @param authentication 현재 인증된 사용자의 정보를 담고 있는 객체
     * @return 전송된 쪽지의 응답 DTO와 HTTP 상태 OK를 반환합니다.
     */
    @PostMapping("/managers/send")
    public ResponseEntity sendMessage(@RequestBody AmazonMessageRequestDTO messageRequestDTO,
                                      HttpServletRequest request,
                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonMessageResponseDTO response = messageService.sendAmazonMessage(request, messageRequestDTO, principal);
        return new ResponseEntity<>((response), HttpStatus.OK);
    }

    /**
     * 관리자가 특정 사용자가 보낸 모든 쪽지를 조회.
     *
     * @param userId 조회할 사용자의 ID
     * @param deletedBySender 발신자에 의해 삭제된 쪽지를 포함할지 여부
     * @param startDate 조회할 시작 날짜와 시간
     * @param endDate 조회할 종료 날짜와 시간
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param authentication 현재 인증된 사용자의 정보를 담고 있는 객체
     * @return 조회된 쪽지 목록과 페이징 정보를 포함한 HTTP 상태 OK를 반환.
     */
    @GetMapping("/managers/{userId}/sender/messages")
    public ResponseEntity findSenderMessages(@PathVariable("userId") @Positive Long userId,
                                             @RequestParam(required = false) boolean deletedBySender,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AmazonMessageListResponseDTO> pages = messageService.findAllSenderMessage(userId,startDate,endDate, deletedBySender, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>((pages.getContent()), pages), HttpStatus.OK);
    }

    /**
     * 회원이 받은 모든 쪽지를 조회.
     *
     * @param userId 조회할 사용자의 ID
     * @param deletedByReceiver 수신자에 의해 삭제된 쪽지를 포함할지 여부
     * @param startDate 조회할 시작 날짜와 시간
     * @param endDate 조회할 종료 날짜와 시간
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param type 쪽지의 유형을 지정 (선택 사항)
     * @param authentication 현재 인증된 사용자의 정보를 담고 있는 객체
     * @return 조회된 쪽지 목록과 페이징 정보를 포함한 HTTP 상태 OK를 반환.
     */
    @GetMapping("/users/{userId}/receiver/messages")
    public ResponseEntity findReceiverMessages(@PathVariable("userId") @Positive Long userId,
                                               @RequestParam(required = false) boolean deletedByReceiver,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                               @RequestParam("page") int page,
                                               @RequestParam("size") int size,
                                               @RequestParam(required = false) String type,
                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AmazonMessageListResponseDTO> pages = messageService.findAllReceiverMessage(userId,startDate,endDate, deletedByReceiver, page, size, type, principal);
        return new ResponseEntity<>(new MultiResponseDto<>((pages.getContent()), pages), HttpStatus.OK);
    }

    /**
     * 관리자가 특정 사용자가 보낸 쪽지 목록을 조회.
     *
     * @param userId 조회할 사용자의 ID
     * @param isRead 읽음 상태의 쪽지만 조회할지 여부
     * @param deletedBySender 발신자에 의해 삭제된 쪽지를 포함할지 여부
     * @param startDate 조회 시작 날짜와 시간
     * @param endDate 조회 종료 날짜와 시간
     * @param page 페이지 번호
     * @param size 한 페이지당 표시할 쪽지 수
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return 조회된 쪽지 목록과 페이지 정보를 포함한 응답 엔티티
     */
    @GetMapping("/managers/{userId}/messages")
    public ResponseEntity getAdminMessages(@PathVariable("userId") @Positive Long userId,
                                           @RequestParam(required = false) boolean isRead,
                                           @RequestParam(required = false) boolean deletedBySender,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                           @RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AmazonMessageListResponseDTO> pages = messageService.getAdminMessages(userId, isRead,deletedBySender,startDate,endDate, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>((pages.getContent()), pages), HttpStatus.OK);
    }

    /**
     * 유저가 받은 쪽지 목록을 조회.
     *
     * @param userId 조회할 사용자의 ID
     * @param isRead 읽음 상태의 쪽지만 조회할지 여부
     * @param startDate 조회 시작 날짜와 시간
     * @param endDate 조회 종료 날짜와 시간
     * @param page 페이지 번호
     * @param size 한 페이지당 표시할 쪽지 수
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return 조회된 쪽지 목록과 페이지 정보를 포함한 응답 엔티티
     */
    @GetMapping("/users/{userId}/messages")
    public ResponseEntity getUserMessages(@PathVariable("userId") @Positive Long userId,
                                          @RequestParam(required = false) boolean isRead,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                          @RequestParam("page") int page,
                                          @RequestParam("size") int size,
                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AmazonMessageListResponseDTO> pages = messageService.getUserMessages(userId, isRead,startDate,endDate, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>((pages.getContent()), pages), HttpStatus.OK);
    }

    /**
     * 사용자가 특정 쪽지의 상세 정보를 조회.
     *
     * @param messageId 조회할 쪽지의 ID
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return 조회된 쪽지의 상세 정보를 포함한 응답 엔티티
     */
    @GetMapping("/users/message/{messageId}")
    public ResponseEntity getMessage(@PathVariable("messageId") @Positive Long messageId,
                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonMessages response = messageService.detailMessage(messageId, principal);
        return new ResponseEntity<>(new SingleResponseDto<>(messageResponseMapper.toDto(response)), HttpStatus.OK);
    }

    /**
     * 쪽지 선택 삭제(관리자 전용)
     *
     * @param messageIds 삭제할 쪽지의 ID 목록
     * @param request HTTP 서블릿 요청 정보
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return 성공적으로 삭제된 경우 "삭제 되었습니다." 메시지와 함께 HTTP 상태 OK를 반환하고,
     *         삭제할 쪽지가 선택되지 않은 경우 "쪽지를 선택해주세요." 메시지와 함께 HTTP 상태 BAD REQUEST를 반환.
     */
    @PatchMapping("/managers/messages")
    public ResponseEntity deleteAdminMessage(@RequestParam("messageIds") List<@Positive Long> messageIds, HttpServletRequest request,
                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if (messageIds.isEmpty()) {
            return ResponseEntity.badRequest().body("쪽지를 선택해주세요.");
        }
        messageService.deleteSelectedAmazonAdminMessages(request, messageIds, principal);
        return ResponseEntity.ok("삭제 되었습니다.");
    }

    /**
     * 쪽지 선택 삭제(회원 전용)
     *
     * @param messageIds 삭제할 쪽지의 ID 목록
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return 성공적으로 삭제된 경우 "삭제 되었습니다." 메시지와 함께 HTTP 상태 OK를 반환하고,
     *         삭제할 쪽지가 선택되지 않은 경우 "쪽지를 선택해주세요." 메시지와 함께 HTTP 상태 BAD REQUEST를 반환.
     */
    @PatchMapping("/users/messages")
    public ResponseEntity deleteMessage(@RequestParam("messageIds") List<@Positive Long> messageIds,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if (messageIds.isEmpty()) {
            return ResponseEntity.badRequest().body("쪽지를 선택해주세요.");
        }
        messageService.deleteSelectedMessages(messageIds, principal);
        return ResponseEntity.ok("삭제 되었습니다.");
    }
}
