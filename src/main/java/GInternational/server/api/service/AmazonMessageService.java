package GInternational.server.api.service;


import GInternational.server.api.dto.AmazonMessageListResponseDTO;
import GInternational.server.api.dto.AmazonMessageRequestDTO;
import GInternational.server.api.dto.AmazonMessageResponseDTO;
import GInternational.server.api.entity.AmazonMessages;
import GInternational.server.api.entity.User;
import GInternational.server.api.mapper.AmazonMessageListMapper;
import GInternational.server.api.mapper.AmazonMessageRequestMapper;
import GInternational.server.api.mapper.AmazonMessageResponseMapper;
import GInternational.server.api.repository.AmazonMessageRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonMessageService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AmazonMessageRepository messageRepository;
    private final AmazonMessageResponseMapper messageResponseMapper;
    private final AmazonMessageRequestMapper messageRequestMapper;
    private final AmazonMessageListMapper messageListMapper;

    /**
     * 관리자가 사용자에게 쪽지를 보내는 기능.
     *
     * @param request 현재 HTTP 요청의 정보를 담고 있는 객체
     * @param messageRequestDTO 쪽지 전송에 필요한 정보를 담은 DTO
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     * @return 전송된 쪽지에 대한 응답 DTO
     */
    @AuditLogService.Audit("쪽지 보내기")
    public AmazonMessageResponseDTO sendAmazonMessage(HttpServletRequest request, AmazonMessageRequestDTO messageRequestDTO, PrincipalDetails principalDetails) {
        User sender = userService.validateUser(principalDetails.getUser().getId());
        User receiver = userRepository.findById(messageRequestDTO.getReceiverId()).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        AmazonMessages messages = messageRequestMapper.toEntity(messageRequestDTO);
        messages.setSender(sender);
        messages.setReceiver(receiver);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(receiver.getId()));
        context.setUsername(receiver.getUsername());
        context.setDetails(receiver.getUsername() + "에게 쪽지보냄" + " 제목: " + messageRequestDTO.getTitle() + " 내용: " + messageRequestDTO.getContent());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        AmazonMessages savedMessage = messageRepository.save(messages);
        return messageResponseMapper.toDto(savedMessage);
    }

    /**
     * 관리자가 특정 사용자가 보낸 모든 쪽지를 조회.
     *
     * @param senderId 발신자의 사용자 ID
     * @param deletedBySender 발신자에 의해 삭제된 쪽지를 포함할지 여부
     * @param isRead 쪽지의 읽음 상태
     * @param startDate 조회 시작 날짜
     * @param endTime 조회 종료 날짜
     * @param page 조회할 페이지 번호
     * @param size 페이지 당 표시할 쪽지 수
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     * @return 조회된 쪽지 목록을 페이지로 구성하여 반환
     */
    public Page<AmazonMessageListResponseDTO> getAdminMessages(Long senderId,
                                                               boolean deletedBySender,
                                                               boolean isRead,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endTime,
                                                               int page,
                                                               int size,
                                                               PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Page<AmazonMessages> pages = messageRepository.getAdminSenderMessages(sender, deletedBySender,startDate,endTime, isRead, pageable);

        List<AmazonMessageListResponseDTO> response = pages.stream()
                .map(messageListMapper::toDto)
                .collect(Collectors.toList());

        //long totalElements = messageRepository.countByAdminMessages(sender, deletedBySender, isRead);

        return new PageImpl<>(response, pageable, pages.getTotalElements());
    }

    /**
     * 회원이 받은 모든 쪽지를 조회.
     *
     * @param receiverId 수신자의 사용자 ID
     * @param isRead 쪽지의 읽음 상태
     * @param startDate 조회 시작 날짜
     * @param endTime 조회 종료 날짜
     * @param page 조회할 페이지 번호
     * @param size 페이지 당 표시할 쪽지 수
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     * @return 조회된 쪽지 목록을 페이지로 구성하여 반환
     */
    public Page<AmazonMessageListResponseDTO> getUserMessages(Long receiverId,
                                                              boolean isRead,
                                                              LocalDateTime startDate,
                                                              LocalDateTime endTime,
                                                              int page,
                                                              int size,
                                                              PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);

        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        User user = userService.validateUser(principalDetails.getUser().getId());

        if (receiver.getId().equals(user.getId())) {
            Page<AmazonMessages> pages = messageRepository.getUserReceivedMessages(receiver,isRead,startDate,endTime, pageable);

            List<AmazonMessageListResponseDTO> response = pages.stream()
                    .map(messageListMapper::toDto)
                    .collect(Collectors.toList());

            long totalElements = messageRepository.countByUserMessages(receiver, isRead);
            return new PageImpl<>(response, pageable, totalElements);
        }
        throw new AccessDeniedException("접근 불가");
    }


    /**
     * 회원이 받은 모든 쪽지를 조회.
     * 회원 본인이 받은 쪽지만 조회할 수 있으며, 다른 회원의 쪽지를 조회하려고 시도할 경우 접근이 거부됨.
     *
     * @param receiverId 수신자의 사용자 ID
     * @param startDate 조회할 쪽지의 시작 날짜와 시간.
     * @param endTime 조회할 쪽지의 종료 날짜와 시간.
     * @param deletedByReceiver 수신자에 의해 삭제된 쪽지를 포함할지 여부를 지정.
     * @param page 조회할 페이지 번호. 페이지 인덱스는 0부터 시작.
     * @param size 페이지 당 표시할 쪽지의 수
     * @param type 쪽지의 유형을 지정합니다. 특정 유형의 쪽지만 조회하려면 이 파라미터를 사용. "all"일 경우 모든 쪽지를 조회.
     * @param principalDetails 현재 인증된 사용자의 세부 정보를 포함하는 객체
     * @return 조회된 쪽지 목록과 페이지 정보를 포함한 {@link Page} 객체
     * @throws RestControllerException 사용자를 찾을 수 없거나, 인증된 사용자가 쪽지의 수신자가 아닌 경우 예외가 발생.
     */
    public Page<AmazonMessageListResponseDTO> findAllReceiverMessage(Long receiverId, LocalDateTime startDate, LocalDateTime endTime,
                                                                     boolean deletedByReceiver, int page, int size, String type,
                                                                     PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        User user = userService.validateUser(principalDetails.getUser().getId());

        if (receiver.getId().equals(user.getId())) {
            Page<AmazonMessages> pages;

            //  type이 all일 경우 전체조회 메소드 호출, all이 아닐 경우 true, false 분기처리
            if (!Objects.equals(type, null)) {
                // type이 지정된 경우 해당 type의 메시지 조회
                pages = messageRepository.findByReceiverMessagesAndType(receiver,startDate,endTime,type, deletedByReceiver, pageable);
            } else {
                // type이 null인 경우 모든 메시지 조회
                pages = messageRepository.findByReceiverMessages(receiver, deletedByReceiver, pageable);
            }

            List<AmazonMessageListResponseDTO> response = pages.stream()
                    .map(messageListMapper::toDto)
                    .collect(Collectors.toList());

            long total;
            if (!Objects.equals(type, null)) {
                // type이 지정된 경우 해당 type의 메시지 개수 조회
                total = messageRepository.countByReceiverMessagesAndType(receiver, type, deletedByReceiver);
            } else {
                // type이 null인 경우 모든 메시지 개수 조회
                total = messageRepository.countByReceiverMessages(receiver, deletedByReceiver);
            }

            return new PageImpl<>(response, pageable, total);
        }
        throw new RestControllerException(ExceptionCode.UNAUTHORIZED_ACCESS);
    }

    /**
     * 관리자가 특정 발신자로부터 온 모든 쪽지를 조회.
     *
     * @param senderId 조회할 쪽지의 발신자 ID
     * @param startDate 조회 시작 날짜
     * @param endTime 조회 종료 날짜
     * @param deletedBySender 발신자에 의해 삭제된 쪽지를 포함할지 여부
     * @param page 요청된 페이지 번호
     * @param size 페이지 당 표시할 쪽지의 수
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     * @return 조회된 쪽지 목록과 페이징 정보를 포함한 페이지 객체
     * @throws RestControllerException 발신자를 찾을 수 없는 경우 예외를 발생.
     */
    public Page<AmazonMessageListResponseDTO> findAllSenderMessage(Long senderId, LocalDateTime startDate, LocalDateTime endTime, boolean deletedBySender, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));

        Page<AmazonMessages> pages = messageRepository.findBySenderMessages(sender,startDate,endTime, deletedBySender, pageable);

        List<AmazonMessageListResponseDTO> response = pages.stream()
                .map(messageListMapper::toDto)
                .collect(Collectors.toList());

        long totalElements = messageRepository.countBySenderMessages(sender, deletedBySender);

        return new PageImpl<>(response, pageable, totalElements);
    }

    /**
     * 쪽지의 상세 정보를 조회. 이 기능은 관리자와 회원 모두에게 제공.
     *
     * @param messageId 조회할 쪽지의 ID
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     * @return 조회된 쪽지의 상세 정보
     * @throws RestControllerException 쪽지를 확인할 수 없는 경우 예외를 발생.
     */

    public AmazonMessages detailMessage(Long messageId, PrincipalDetails principalDetails) {
        AmazonMessages message = validateMessage(messageId);
        User receiver = userService.validateUser(principalDetails.getUser().getId());
        Long receiverId = message.getReceiver().getId();


        if (receiver.getId().equals(receiverId)) {
                message.setRead(true);
                return message;
        } else {
            throw new RestControllerException(ExceptionCode.MESSAGE_NOT_FOUND);
        }
    }

    /**
     * 관리자가 선택한 쪽지를 삭제.
     *
     * @param request 현재 HTTP 요청의 정보를 담고 있는 객체
     * @param messageIds 삭제할 쪽지의 ID 목록
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     */

    @AuditLogService.Audit("쪽지 삭제")
    public void deleteSelectedAmazonAdminMessages(HttpServletRequest request, List<Long> messageIds, PrincipalDetails principalDetails) {
        for (Long messageId : messageIds) {
            AmazonMessages messages = messageRepository.findById(messageId).orElseThrow(() -> new RestControllerException(ExceptionCode.MESSAGE_NOT_FOUND));
            User sender = userService.validateUser(principalDetails.getUser().getId());

            AuditContext context = AuditContextHolder.getContext();
            String clientIp = request.getRemoteAddr();
            context.setIp(clientIp);
            context.setTargetId(messages.getSender().getId().toString());
            context.setUsername(messages.getReceiver().getUsername());
            context.setDetails(messages.getReceiver().getUsername() + "의 쪽지 삭제" + " 제목: " + messages.getTitle());
            context.setAdminUsername(principalDetails.getUsername());
            context.setTimestamp(LocalDateTime.now());

            if (sender.getRole().equals("BIG_HEAD_OFFICE")) {
                if (messages.isDeletedByReceiver()) {
                    messageRepository.delete(messages);
                } else {
                    messages.setDeletedBySender(true);
                    messageRepository.save(messages);
                }
            }
        }
    }

    /**
     * 회원이 선택한 쪽지를 삭제합니다. Soft delete 방식을 사용하며, 양쪽 모두 삭제했을 경우 데이터베이스에서 쪽지를 삭제.
     *
     * @param messageIds 삭제할 쪽지의 ID 목록
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     */
    public void deleteSelectedMessages(List<Long> messageIds, PrincipalDetails principalDetails) {
        for (Long messageId : messageIds) {
            AmazonMessages messages = messageRepository.findById(messageId).orElseThrow(() -> new RestControllerException(ExceptionCode.MESSAGE_NOT_FOUND));
            User receiver = userService.validateUser(principalDetails.getUser().getId());
            Long receiverId = messages.getReceiver().getId();

            if (receiver.getId().equals(receiverId)) {
                if (messages.isDeletedBySender()) {
                    messageRepository.delete(messages);
                } else {
                    messages.setDeletedByReceiver(true);
                    messageRepository.save(messages);
                }
            }
        }
    }

    /**
     * 주어진 ID에 해당하는 쪽지의 유효성을 검증하고, 해당 쪽지 객체를 반환.
     *
     * @param id 검증할 쪽지의 ID
     * @return 유효한 쪽지 객체
     * @throws RestControllerException 쪽지를 찾을 수 없는 경우 예외를 발생시킴.
     */
    public AmazonMessages validateMessage(Long id) {
        Optional<AmazonMessages> messages = messageRepository.findById(id);
        AmazonMessages findMessage = messages.orElseThrow(() -> new RestControllerException(ExceptionCode.MESSAGE_NOT_FOUND));
        return findMessage;
    }
}
