package GInternational.server.api.controller;


import GInternational.server.api.dto.MessageOverviewDTO;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.api.dto.MessageListResponseDTO;
import GInternational.server.api.dto.MessageRequestDTO;
import GInternational.server.api.dto.MessageResponseDTO;
import GInternational.server.api.entity.Messages;
import GInternational.server.api.mapper.MessageResponseMapper;
import GInternational.server.api.service.MessageService;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageResponseMapper messageResponseMapper;

    /**
     * 쪽지 발송. [관리자 전용]
     *
     * @param messageRequestDTO 쪽지 발송에 필요한 정보를 담은 DTO
     * @param authentication 인증 객체
     * @return ResponseEntity 쪽지 발송 결과를 담은 DTO
     */
    @PostMapping("/managers/send")
    public ResponseEntity sendMessage(@RequestBody MessageRequestDTO messageRequestDTO,
                                      HttpServletRequest request,
                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        MessageResponseDTO response = messageService.sendMessage(messageRequestDTO, principal, request);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "쪽지 발송 완료.");
        return new ResponseEntity<>((response), HttpStatus.OK);
    }

    /**
     * 관리자가 특정 사용자로부터 보낸 모든 쪽지를 조회. [관리자 전용]
     *
     * @param userId 사용자 ID
     * @param deletedBySender 발신자에 의해 삭제된 쪽지 포함 여부
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param authentication 인증 객체
     * @return ResponseEntity 페이지네이션 처리된 쪽지 목록
     */
    @GetMapping("/managers/{userId}/sender/messages")
    public ResponseEntity findSenderMessages(@PathVariable("userId") @Positive Long userId,
                                             @RequestParam(required = false) boolean deletedBySender,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<MessageListResponseDTO> pages = messageService.findAllSenderMessage(userId, deletedBySender, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>((pages.getContent()), pages), HttpStatus.OK);
    }

    /**
     * 회원이 받은 모든 쪽지를 조회. [회원 전용]
     *
     * @param userId 사용자 ID
     * @param deletedByReceiver 수신자에 의해 삭제된 쪽지 포함 여부
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param type 쪽지의 종류
     * @param authentication 인증 객체
     * @return ResponseEntity 페이지네이션 처리된 쪽지 목록
     */
    @GetMapping("/users/{userId}/receiver/messages")
    public ResponseEntity findReceiverMessages(@PathVariable("userId") @Positive Long userId,
                                               @RequestParam(required = false) boolean deletedByReceiver,
                                               @RequestParam("page") int page,
                                               @RequestParam("size") int size,
                                               @RequestParam(required = false) String type,
                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<MessageListResponseDTO> pages = messageService.findAllReceiverMessage(userId, deletedByReceiver, page, size, type, principal);
        return new ResponseEntity<>(new MultiResponseDto<>((pages.getContent()), pages), HttpStatus.OK);
    }


    /**
     * 관리자가 보낸 쪽지를 조회합니다. [관리자 전용]
     *
     * @param userId 사용자 ID
     * @param isRead 읽음 상태
     * @param deletedBySender 발신자에 의해 삭제된 쪽지 포함 여부
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param authentication 인증 객체
     * @return ResponseEntity 페이지네이션 처리된 쪽지 목록
     */
    @GetMapping("/managers/{userId}/messages")
    public ResponseEntity getAdminMessages(@PathVariable("userId") @Positive Long userId,
                                           @RequestParam(required = false) boolean isRead,
                                           @RequestParam(required = false) boolean deletedBySender,
                                           @RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<MessageListResponseDTO> pages = messageService.getAdminMessages(userId, isRead, deletedBySender, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>((pages.getContent()), pages), HttpStatus.OK);
    }

    /**
     * 회원이 받은 쪽지를 조회. [회원 전용]
     *
     * @param userId 사용자 ID
     * @param isRead 읽음 상태
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param authentication 인증 객체
     * @return ResponseEntity 페이지네이션 처리된 쪽지 목록
     */
    @GetMapping("/users/{userId}/messages")
    public ResponseEntity getUserMessages(@PathVariable("userId") @Positive Long userId,
                                          @RequestParam(required = false) boolean isRead,
                                          @RequestParam("page") int page,
                                          @RequestParam("size") int size,
                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<MessageListResponseDTO> pages = messageService.getUserMessages(userId, isRead, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>((pages.getContent()), pages), HttpStatus.OK);
    }

    /**
     * 특정 쪽지의 상세 정보 조회.
     *
     * @param messageId 쪽지 ID
     * @param authentication 인증 객체
     * @return ResponseEntity 쪽지 상세 정보를 담은 DTO
     */
    @GetMapping("/users/message/{messageId}")
    public ResponseEntity<MessageResponseDTO> getMessage(@PathVariable("messageId") @Positive Long messageId,
                                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        MessageResponseDTO responseDTO = messageService.detailMessage(messageId, principal);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 관리자가 지정된 조건에 따라 모든 메시지를 조회.
     *
     * @param startDate        조회 시작 날짜 (옵션)
     * @param endDate          조회 종료 날짜 (옵션)
     * @param title            메시지 제목 (옵션)
     * @param content          메시지 내용 (옵션)
     * @param site             사이트 (옵션)
     * @param receiverUsername 수신자의 사용자 이름 (옵션)
     * @param receiverNickname 수신자의 닉네임 (옵션)
     * @param authentication   현재 사용자의 인증 정보를 담고 있는 {@link Authentication} 객체
     * @return 조회된 메시지 목록을 담고 있는 {@link ResponseEntity<List<MessageOverviewDTO>>} 객체
     */
    @GetMapping("/managers/all")
    public ResponseEntity<List<MessageOverviewDTO>> findAllMessages(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                    @RequestParam(required = false) String title,
                                                                    @RequestParam(required = false) String content,
                                                                    @RequestParam(required = false) String site,
                                                                    @RequestParam(required = false) String receiverUsername,
                                                                    @RequestParam(required = false) String receiverNickname,
                                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<MessageOverviewDTO> messages = messageService.findAllMessages(startDate, endDate, title, content, site, receiverUsername, receiverNickname, principal);
        return ResponseEntity.ok(messages);
    }


    /**
     * 관리자가 선택한 쪽지들을 삭제. [관리자 전용]
     *
     * @param messageIds 삭제할 쪽지의 ID 목록
     * @param authentication 인증 객체
     * @return ResponseEntity 삭제 결과 메시지
     */
    @DeleteMapping("/managers/messages")
    public ResponseEntity deleteAdminMessage(@RequestParam("messageIds") List<@Positive Long> messageIds,
                                             HttpServletRequest request,
                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if (messageIds.isEmpty()) {
            return ResponseEntity.badRequest().body("쪽지를 선택해주세요.");
        }
        messageService.deleteSelectedAdminMessages(messageIds, principal, request);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    /**
     * 회원이 선택한 쪽지들을 삭제. [회원 전용]
     *
     * @param messageIds 삭제할 쪽지의 ID 목록
     * @param authentication 인증 객체
     * @return ResponseEntity 삭제 결과 메시지
     */
    @PatchMapping("/users/messages")
    public ResponseEntity deleteMessage(@RequestParam("messageIds") List<@Positive Long> messageIds,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if (messageIds.isEmpty()) {
            return ResponseEntity.badRequest().body("쪽지를 선택해주세요.");
        }
        messageService.deleteSelectedMessages(messageIds, principal);
        return ResponseEntity.ok("삭제되었습니다.");
    }
}
