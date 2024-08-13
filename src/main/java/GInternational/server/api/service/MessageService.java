package GInternational.server.api.service;



import GInternational.server.api.dto.*;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.entity.Messages;
import GInternational.server.api.mapper.MessageListMapper;
import GInternational.server.api.mapper.MessageRequestMapper;
import GInternational.server.api.mapper.MessageResponseMapper;
import GInternational.server.api.repository.MessageRepository;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class MessageService {


    private final UserService userService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageResponseMapper messageResponseMapper;
    private final MessageRequestMapper messageRequestMapper;
    private final MessageListMapper messageListMapper;


    /**
     * 관리자가 사용자에게 쪽지를 보내는 기능.
     *
     * @param messageRequestDTO 쪽지 보내기 요청 데이터
     * @param principalDetails 보내는 사람의 인증 정보
     * @return MessageResponseDTO 보낸 쪽지의 응답 데이터
     */
    @AuditLogService.Audit("쪽지 보내기")
    public MessageResponseDTO sendMessage(MessageRequestDTO messageRequestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User sender = userService.validateUser(principalDetails.getUser().getId());
        User receiver = userRepository.findById(messageRequestDTO.getReceiverId()).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        Messages messages = messageRequestMapper.toEntity(messageRequestDTO);
        messages.setSender(sender);
        messages.setReceiver(receiver);
        messages.setPopup(messageRequestDTO.isPopup());
        Messages savedMessage = messageRepository.save(messages);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(receiver.getId()));
        context.setUsername(receiver.getUsername());
        context.setDetails(receiver.getUsername() + "에게 쪽지보냄" + "// 제목: " + messageRequestDTO.getTitle() + "// 내용: " + messageRequestDTO.getContent());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return messageResponseMapper.toDto(savedMessage);
    }

    /**
     * 관리자가 보낸 쪽지 목록을 조회.
     *
     * @param senderId 쪽지를 보낸 사용자 ID
     * @param deletedBySender 발신자에 의해 삭제된 쪽지 포함 여부
     * @param isRead 쪽지 읽음 여부
     * @param page 페이지 번호
     * @param size 페이지당 표시할 쪽지 수
     * @param principalDetails 인증된 사용자 정보
     * @return Page<MessageListResponseDTO> 조회된 쪽지 목록
     */
    public Page<MessageListResponseDTO> getAdminMessages(Long senderId,
                                                         boolean deletedBySender,
                                                         boolean isRead,
                                                         int page,
                                                         int size,
                                                         PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));

        Page<Messages> pages = messageRepository.getAdminSenderMessages(sender, deletedBySender, isRead, pageable);

        List<MessageListResponseDTO> response = pages.stream()
                .map(messageListMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(response, pageable, pages.getTotalElements());
    }

    /**
     * 회원이 받은 쪽지 목록을 조회.
     *
     * @param receiverId 쪽지를 받은 사용자 ID
     * @param isRead 쪽지 읽음 여부
     * @param page 페이지 번호
     * @param size 페이지당 표시할 쪽지 수
     * @param principalDetails 인증된 사용자 정보
     * @return Page<MessageListResponseDTO> 조회된 쪽지 목록
     */
    public Page<MessageListResponseDTO> getUserMessages(Long receiverId,
                                                        boolean isRead,
                                                        int page,
                                                        int size,
                                                        PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);

        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        User user = userService.detailUser(principalDetails.getUser().getId(), principalDetails);

        if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER") || receiver.getId().equals(user.getId())) {
            Page<Messages> pages = messageRepository.getUserReceivedMessages(receiver, isRead, pageable);

            List<MessageListResponseDTO> response = pages.stream()
                    .map(messageListMapper::toDto)
                    .collect(Collectors.toList());

            long totalElements = messageRepository.countByUserMessages(receiver, isRead);
            return new PageImpl<>(response, pageable, totalElements);
        }
        throw new AccessDeniedException("접근 불가");
    }

    /**
     * 회원이 받은 모든 쪽지를 조회.
     *
     * @param receiverId 쪽지를 받은 사용자 ID
     * @param deletedByReceiver 수신자에 의해 삭제된 쪽지 포함 여부
     * @param page 페이지 번호
     * @param size 페이지당 표시할 쪽지 수
     * @param type 쪽지의 유형
     * @param principalDetails 인증된 사용자 정보
     * @return Page<MessageListResponseDTO> 조회된 쪽지 목록
     */
    public Page<MessageListResponseDTO> findAllReceiverMessage(Long receiverId, boolean deletedByReceiver, int page, int size, String type, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        User user = userService.detailUser(principalDetails.getUser().getId(), principalDetails);

        if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER") || receiver.getId().equals(user.getId())) {
            Page<Messages> pages;

            // type이 all일 경우 전체조회 메소드 호출, all이 아닐 경우 true, false 분기처리
            if (!Objects.equals(type, null)) {
                // type이 지정된 경우 해당 type의 메시지 조회
                pages = messageRepository.findByReceiverMessagesAndType(receiver, type, deletedByReceiver, pageable);
            } else {
                // type이 null인 경우 모든 메시지 조회
                pages = messageRepository.findByReceiverMessages(receiver, deletedByReceiver, pageable);
            }

            List<MessageListResponseDTO> response = pages.stream()
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
        throw new AccessDeniedException("접근 불가");
    }

    /**
     * 관리자가 보낸 모든 쪽지를 조회.
     *
     * @param senderId 쪽지를 보낸 사용자 ID
     * @param deletedBySender 발신자에 의해 삭제된 쪽지 포함 여부
     * @param page 페이지 번호
     * @param size 페이지당 표시할 쪽지 수
     * @param principalDetails 인증된 사용자 정보
     * @return Page<MessageListResponseDTO> 조회된 쪽지 목록
     */
    public Page<MessageListResponseDTO> findAllSenderMessage(Long senderId, boolean deletedBySender, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));

        Page<Messages> pages = messageRepository.findBySenderMessages(sender, deletedBySender, pageable);

        List<MessageListResponseDTO> response = pages.stream()
                .map(messageListMapper::toDto)
                .collect(Collectors.toList());

        long totalElements = messageRepository.countBySenderMessages(sender, deletedBySender);

        return new PageImpl<>(response, pageable, totalElements);
    }

    /**
     * 쪽지의 상세 정보를 조회. 관리자와 회원 모두 사용 가능.
     *
     * @param messageId        조회할 쪽지 ID
     * @param principalDetails 인증된 사용자 정보
     * @return Messages 조회된 쪽지의 상세 정보
     */
    public MessageResponseDTO detailMessage(Long messageId, PrincipalDetails principalDetails) {
        Messages message = validateMessage(messageId);
        User receiver = userService.detailUser(principalDetails.getUser().getId(), principalDetails);
        Long receiverId = message.getReceiver().getId();

        if (receiver.getRole().equals("ROLE_ADMIN") || receiver.getRole().equals("ROLE_MANAGER") || receiver.getId().equals(receiverId)) {
            message.setRead(true);
            message.setReadDate(LocalDateTime.now());
            messageRepository.save(message);

            // receiver 정보를 MessageResponseDTO로 변환하여 설정
            UserProfileDTO receiverDTO = new UserProfileDTO(message.getReceiver());
            MessageResponseDTO responseDTO = new MessageResponseDTO(message);
            responseDTO.setId(messageId);
            responseDTO.setContent(message.getContent());
            responseDTO.setTitle(message.getTitle());
            responseDTO.setReadDate(message.getReadDate());
            responseDTO.setRead(message.isRead());
            responseDTO.setReceiver(receiverDTO);
            responseDTO.setCreatedAt(message.getCreatedAt());

            return responseDTO;
        } else {
            throw new AccessDeniedException("쪽지를 확인할 수 없습니다.");
        }
    }

    /**
     * 관리자가 선택한 쪽지들을 삭제.
     *
     * @param messageIds 삭제할 쪽지 ID 목록
     * @param principalDetails 인증된 사용자 정보
     */
    @AuditLogService.Audit("쪽지 삭제")
    public void deleteSelectedAdminMessages(List<Long> messageIds, PrincipalDetails principalDetails, HttpServletRequest request) {
        for (Long messageId : messageIds) {
            Messages messages = messageRepository.findById(messageId).orElseThrow(() -> new RestControllerException(ExceptionCode.MESSAGE_NOT_FOUND));
            User sender = userService.detailUser(principalDetails.getUser().getId(), principalDetails);

            AuditContext context = AuditContextHolder.getContext();
            String clientIp = request.getRemoteAddr();
            context.setIp(clientIp);
            context.setTargetId(messages.getSender().getId().toString());
            context.setUsername(messages.getReceiver().getUsername());
            context.setDetails(messages.getReceiver().getUsername() + "의 쪽지 삭제" + " 제목: " + messages.getTitle());
            context.setAdminUsername(principalDetails.getUsername());
            context.setTimestamp(LocalDateTime.now());

            if (sender.getRole().equals("ROLE_ADMIN") || sender.getRole().equals("ROLE_MANAGER")) {
                messageRepository.delete(messages); // 관리자가 삭제하면 완전 삭제
            }
        }
    }

    /**
     * 회원이 선택한 쪽지들을 삭제. 관리자, 회원 양쪽 모두 삭제할 경우 데이터베이스에서 삭제됨.
     *
     * @param messageIds 삭제할 쪽지 ID 목록
     * @param principalDetails 인증된 사용자 정보
     */
    public void deleteSelectedMessages(List<Long> messageIds, PrincipalDetails principalDetails) {
        for (Long messageId : messageIds) {
            Messages messages = messageRepository.findById(messageId).orElseThrow(() -> new RestControllerException(ExceptionCode.MESSAGE_NOT_FOUND));
            User receiver = userService.detailUser(principalDetails.getUser().getId(), principalDetails);
            Long receiverId = messages.getReceiver().getId();

            if (receiver.getId().equals(receiverId)) {
                if (messages.isDeletedBySender()) {
                    messageRepository.delete(messages); // 발신자가 이미 삭제한 경우 완전 삭제
                } else {
                    messages.setDeletedByReceiver(true); // 수신자가 삭제한 경우 소프트 삭제
                    messageRepository.save(messages);
                }
            }
        }
    }

    /**
     * 지정된 조건에 따라 모든 메시지를 조회.
     *
     * @param startDate        조회 시작 날짜 (옵션)
     * @param endDate          조회 종료 날짜 (옵션)
     * @param title            메시지 제목 (옵션)
     * @param content          메시지 내용 (옵션)
     * @param site             사이트 (옵션)
     * @param receiverUsername 수신자의 사용자 이름 (옵션)
     * @param receiverNickname 수신자의 닉네임 (옵션)
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 조회된 메시지 목록을 {@link MessageOverviewDTO} 리스트로 반환
     */
    public List<MessageOverviewDTO> findAllMessages(LocalDate startDate, LocalDate endDate, String title, String content,
                                                    String site, String receiverUsername, String receiverNickname,
                                                    PrincipalDetails principalDetails) {
        Specification<Messages> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null || endDate != null) {
                if (startDate != null) {
                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    if (endDate != null) {
                        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                        predicates.add(cb.between(root.get("createdAt"), startDateTime, endDateTime));
                    } else {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime));
                    }
                } else {
                    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDateTime));
                }
            }

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + title + "%"));
            }

            if (content != null && !content.isEmpty()) {
                predicates.add(cb.like(root.get("content"), "%" + content + "%"));
            }

            if (site != null && !site.isEmpty()) {
                predicates.add(cb.equal(root.get("site"), site));
            }

            if (receiverUsername != null && !receiverUsername.isEmpty()) {
                predicates.add(cb.equal(root.get("receiver").get("username"), receiverUsername));
            }

            if (receiverNickname != null && !receiverNickname.isEmpty()) {
                predicates.add(cb.equal(root.get("receiver").get("nickname"), receiverNickname));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Messages> messagesList = messageRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 메시지 엔티티를 DTO로 변환
        return messagesList.stream().map(message -> new MessageOverviewDTO(
                message.getId(),
                message.getTitle(),
                message.getSite(),
                message.getSender() != null ? message.getSender().getUsername() : "N/A", // Sender가 null일 수 있으므로 처리
                message.getCreatedAt(),
                message.getReadDate(),
                message.isRead(),
                message.isDeletedBySender(),
                message.isDeletedByReceiver(),
                message.getReceiver() != null ? message.getReceiver().getUsername() : "N/A", // receiver의 username
                message.getReceiver() != null ? message.getReceiver().getNickname() : "N/A"  // receiver의 nickname
        )).collect(Collectors.toList());
    }

    /**
     * 주어진 ID에 해당하는 쪽지를 유효성 검사하고 반환.
     *
     * @param id 검증할 쪽지 ID
     * @return Messages 유효한 쪽지
     */
    public Messages validateMessage(Long id) {
        Optional<Messages> messages = messageRepository.findById(id);
        Messages findMessage = messages.orElseThrow(() -> new RestControllerException(ExceptionCode.MESSAGE_NOT_FOUND));
        return findMessage;
    }
}
