package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.EventsBoardListDTO;
import GInternational.server.api.dto.EventsBoardRequestDTO;
import GInternational.server.api.dto.EventsBoardResponseDTO;
import GInternational.server.api.entity.EventsBoard;
import GInternational.server.api.mapper.EventsBoardListResponseMapper;
import GInternational.server.api.mapper.EventsBoardRequestMapper;
import GInternational.server.api.mapper.EventsBoardResponseMapper;
import GInternational.server.api.repository.EventsBoardRepository;

import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class EventsBoardService {

    private final EventsBoardRequestMapper eventsBoardRequestMapper;
    private final EventsBoardResponseMapper eventsBoardResponseMapper;
    private final EventsBoardRepository eventsBoardRepository;
    private final UserService userService;

    /**
     * 이벤트 생성
     *
     * @param eventsBoardRequestDTO  이벤트 상세 정보가 담긴 DTO
     * @param principalDetails  현재 로그인한 사용자의 보안 상세 정보
     * @return                  저장된 이벤트의 DTO
     */
    @AuditLogService.Audit("이벤트 게시글 생성")
    public EventsBoardResponseDTO insertEvent(@Valid EventsBoardRequestDTO eventsBoardRequestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userService.validateUser(principalDetails.getUser().getId());
        EventsBoard eventsBoard = eventsBoardRequestMapper.toEntity(eventsBoardRequestDTO);
        eventsBoard.setWriter(user);
        eventsBoard.setWriterUsername(user.getUsername());
        eventsBoard.setViewStatus(eventsBoardRequestDTO.getViewStatus());
        eventsBoard.setReadCount(eventsBoard.getReadCount());
        eventsBoard.setStartDate(eventsBoardRequestDTO.getStartDate());
        eventsBoard.setEndDate(eventsBoardRequestDTO.getEndDate());
        eventsBoard.setDescription(eventsBoard.getDescription());
        eventsBoard.setCreatedAt(LocalDateTime.now());
        eventsBoard.setEnabled(eventsBoardRequestDTO.isEnabled());
        EventsBoard savedEvent = eventsBoardRepository.save(eventsBoard);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("이벤트 게시글 생성, 게시글 제목: " + eventsBoardRequestDTO.getTitle());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return eventsBoardResponseMapper.toDto(savedEvent);
    }

    /**
     * 이벤트 수정
     *
     * @param eventId           업데이트할 이벤트 ID
     * @param eventsBoardRequestDTO  업데이트할 이벤트 정보가 담긴 DTO
     * @param principalDetails  현재 로그인한 사용자의 보안 상세 정보
     * @return                  업데이트된 이벤트의 DTO
     */
    @AuditLogService.Audit("이벤트 게시글 수정")
    public EventsBoardResponseDTO updateEvent(Long eventId, EventsBoardRequestDTO eventsBoardRequestDTO,
                                              PrincipalDetails principalDetails, HttpServletRequest request) {
        EventsBoard eventsBoard = eventsBoardRepository.findById(eventId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.EVENT_NOT_FOUND, "이벤트를 찾을 수 없습니다."));

        // Null 또는 빈 값이 아닌 경우에만 업데이트
        if (eventsBoardRequestDTO.getTitle() != null) {
            eventsBoard.setTitle(eventsBoardRequestDTO.getTitle());
        }
        if (eventsBoardRequestDTO.getDescription() != null) {
            eventsBoard.setDescription(eventsBoardRequestDTO.getDescription());
        }
        if (eventsBoardRequestDTO.getStartDate() != null) {
            eventsBoard.setStartDate(eventsBoardRequestDTO.getStartDate());
        }
        if (eventsBoardRequestDTO.getEndDate() != null) {
            eventsBoard.setEndDate(eventsBoardRequestDTO.getEndDate());
        }
        if (eventsBoardRequestDTO.getViewStatus() != null) {
            eventsBoard.setViewStatus(eventsBoardRequestDTO.getViewStatus());
        }
        if (eventsBoardRequestDTO.getReadCount() != 0) {
            eventsBoard.setReadCount(eventsBoardRequestDTO.getReadCount());
        }
        if (eventsBoard.isEnabled() != eventsBoardRequestDTO.isEnabled()) {
            eventsBoard.setEnabled(eventsBoardRequestDTO.isEnabled());
        }
        eventsBoard.setUpdatedAt(LocalDateTime.now());

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("이벤트 게시글 수정, 게시글 제목: " + eventsBoardRequestDTO.getTitle());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return eventsBoardResponseMapper.toDto(eventsBoardRepository.save(eventsBoard));
    }

    /**
     * 이벤트 전체 조회.
     * 게스트가 아닌 모든 사용자가 조회할 수 있음.
     *
     * @param principalDetails 현재 로그인한 사용자의 보안 상세 정보
     * @return 조회된 모든 이벤트의 DTO 목록
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public List<EventsBoardListDTO> getAllEvents(PrincipalDetails principalDetails, String startDate, String endDate, String title, String site, String writerNickname, String writerUsername) {
        Specification<EventsBoard> spec = Specification.where(null);

        if (startDate != null && !startDate.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), LocalDate.parse(startDate).atStartOfDay()));
        }

        if (endDate != null && !endDate.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), LocalDate.parse(endDate).atTime(LocalTime.MAX)));
        }

        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + title + "%"));
        }

        if (principalDetails.getUser().getRole().equals("ROLE_USER") || principalDetails.getUser().getRole().equals("ROLE_TEST")) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("viewStatus"), "노출"));
        }

        if (site != null && !site.isEmpty()) {
            spec = spec.and (((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("site"), "%" + site + "%")));
        }

        if (writerNickname != null && !writerNickname.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("writer").get("nickname"), "%" + writerNickname + "%"));
        }

        if (writerUsername != null && !writerUsername.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("writerUsername"), "%" + writerUsername + "%"));
        }

        List<EventsBoard> eventsBoardList = eventsBoardRepository.findAll(spec);
        return eventsBoardList.stream()
                .map(EventsBoardListResponseMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 이벤트 ID에 해당하는 이벤트를 조회함.
     * 조회는 GUEST 역할이 아닌 사용자에게만 허용됨.
     *
     * @param eventId          조회할 이벤트 ID
     * @param principalDetails 현재 로그인한 사용자의 보안 상세 정보
     * @return                 조회된 특정 이벤트의 DTO
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public EventsBoardResponseDTO detailEvent(Long eventId, PrincipalDetails principalDetails) {
        EventsBoard event = eventsBoardRepository.findById(eventId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.EVENT_NOT_FOUND, "이벤트를 찾을 수 없습니다."));
        event.setReadCount(event.getReadCount() + 1);
        return eventsBoardResponseMapper.toDto(event);
    }

    /**
     * 주어진 이벤트 ID에 해당하는 이벤트를 삭제함.
     * 삭제 권한은 이벤트의 작성자 또는 관리자에게 있음.
     *
     * @param eventId          삭제할 이벤트 ID
     * @param principalDetails 현재 로그인한 사용자의 보안 상세 정보
     */
    @AuditLogService.Audit("이벤트 게시글 삭제")
    public void deleteEvent(Long eventId, PrincipalDetails principalDetails, HttpServletRequest request) {
        EventsBoard event = eventsBoardRepository.findById(eventId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.EVENT_NOT_FOUND, "이벤트를 찾을 수 없습니다."));
        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("이벤트 게시글 삭제, 게시글 제목: " + event.getTitle());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        eventsBoardRepository.delete(event);
    }

    /**
     * 이벤트 활성화/비활성화
     *
     * @param eventId           활성화/비활성화할 이벤트 ID
     * @param principalDetails  현재 로그인한 사용자의 보안 상세 정보
     * @param enable            활성화 상태(true for 활성화, false for 비활성화)
     * @return                  업데이트된 이벤트의 DTO
     */
    @AuditLogService.Audit("이벤트 게시글 활성화 상태 변경")
    public EventsBoardResponseDTO updateEventStatus(Long eventId, PrincipalDetails principalDetails, boolean enable, HttpServletRequest request) {
        EventsBoard event = eventsBoardRepository.findById(eventId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.EVENT_NOT_FOUND, "이벤트를 찾을 수 없습니다."));

        if (event.isEnabled() == enable) {
            throw new RestControllerException(ExceptionCode.INVALID_STATUS, "유효하지 않은 상태입니다.");
        }

        event.setEnabled(enable);
        event.setUpdatedAt(LocalDateTime.now());

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("이벤트 게시글 활성화 상태 변경, 게시글 제목: " + event.getTitle() + ", 활성화 상태 " + (enable ? "활성화" : "비활성화") + "로 변경");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return eventsBoardResponseMapper.toDto(eventsBoardRepository.save(event));
    }
}
