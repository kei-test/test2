package GInternational.server.api.controller;


import GInternational.server.api.dto.IpReqDTO;
import GInternational.server.api.dto.IpResDTO;
import GInternational.server.api.entity.Ip;
import GInternational.server.api.mapper.IpResponseMapper;
import GInternational.server.api.service.IpService;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class IpController {


    private final IpService ipService;
    private final IpResponseMapper mapper;

    /**
     * IP 주소 차단.
     *
     * @param ipReqDTO 차단할 IP 정보를 담고 있는 DTO
     * @param authentication 인증 정보
     * @return ResponseEntity 차단된 IP 정보와 메시지를 담은 ResponseEntity 객체
     */
    @PostMapping("/managers/ip")
    public ResponseEntity<?> blockIp(@RequestBody IpReqDTO ipReqDTO,
                                     HttpServletRequest request,
                                     Authentication authentication) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            Ip response = ipService.blockIp(ipReqDTO, principal, request);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", new SingleResponseDto<>(response));
            responseBody.put("message", "IP가 차단되었습니다.");
            return ResponseEntity.ok(responseBody);
        } catch (RestControllerException e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", e.getMessage());
            errorBody.put("errorCode", e.getExceptionCode());
            return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Internal server error");
            errorBody.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * IP 주소 차단 해제.
     *
     * @param ipId 차단 해제할 IP의 식별자
     * @param authentication 인증 정보
     * @return ResponseEntity 해제 성공 메시지를 담은 ResponseEntity 객체
     */
    @DeleteMapping("/managers/ip/{ipId}")
    public ResponseEntity deleteIp(@PathVariable ("ipId") Long ipId,
                                   HttpServletRequest request,
                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        ipService.deleteIp(ipId, principal, request);
        return ResponseEntity.ok("ip 차단이 해제되었습니다.");
    }

    /**
     * 지정된 날짜 범위 내 모든 차단된 IP 주소를 조회.
     *
     * @param start 조회할 시작 날짜 (옵션)
     * @param end 조회할 종료 날짜 (옵션)
     * @param ipContent IP 내용 (옵션)
     * @param note 노트 (옵션)
     * @param authentication 인증 정보
     * @return ResponseEntity 날짜 범위 내의 IP 목록을 담은 ResponseEntity 객체
     */
    @GetMapping("/managers/ips/date-range")
    public ResponseEntity<List<IpResDTO>> getIpsByDateRange(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                                            @RequestParam(required = false) String ipContent,
                                                            @RequestParam(required = false) String note,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDate = (start != null) ? start.atStartOfDay() : null;
        LocalDateTime endDate = (end != null) ? end.atTime(23, 59, 59) : null;

        List<Ip> ipList = ipService.findIpsByFilters(startDate, endDate, principal, ipContent, note);
        List<IpResDTO> ipResDTOs = ipList.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ipResDTOs);
    }
}
