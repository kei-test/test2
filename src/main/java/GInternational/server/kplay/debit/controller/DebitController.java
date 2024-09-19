package GInternational.server.kplay.debit.controller;

import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.kplay.debit.dto.DebitAmazonResponseDTO;
import GInternational.server.kplay.debit.dto.DebitRequestDTO;
import GInternational.server.kplay.debit.dto.DebitResponseDTO;
import GInternational.server.kplay.debit.dto.DebitUserResponseDTO;
import GInternational.server.kplay.debit.service.DebitService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@RestController
@RequiredArgsConstructor
public class DebitController {

    private final DebitService debitService;

    /**
     * 사용자로부터 베팅 요청을 받아 처리.
     *
     * @param debitRequestDTO 베팅 요청 정보를 담은 DTO
     * @param secretHeader 요청에 포함된 시크릿 키 헤더
     * @return ResponseEntity<DebitResponseDTO> 처리 결과를 담은 베팅 응답 DTO
     */
    @PostMapping("/debit")
    public ResponseEntity<DebitResponseDTO> processDebit(@RequestBody DebitRequestDTO debitRequestDTO,
                                                         @RequestHeader("secret-key") String secretHeader,
                                                         HttpServletRequest request) {
        DebitResponseDTO response = debitService.calledDebit(debitRequestDTO, secretHeader, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자의 베팅 내역 조회.
     *
     * @param userId 조회할 사용자의 ID
     * @param type 베팅의 타입
     * @param size 페이지당 항목 수
     * @param page 페이지 번호
     * @return ResponseEntity 사용자의 베팅 내역을 담은 페이지 응답
     */
    @GetMapping("/debits/{userId}")
    public ResponseEntity searchMyDebit(@PathVariable("userId") int userId,
                                        @RequestParam String type,
                                        @RequestParam int size,
                                        @RequestParam int page) {
        Page<DebitUserResponseDTO> pages = debitService.searchMyDebit(userId, size, type, page);
        return new ResponseEntity<>(new MultiResponseDto<>(pages.getContent(), pages), HttpStatus.OK);
    }

    /**
     * 모든 사용자의 베팅 내역을 조회. (특정 타입에 따라 필터링)
     *
     * @param type 베팅의 타입
     * @param size 페이지당 항목 수
     * @param page 페이지 번호
     * @return ResponseEntity 베팅 내역을 담은 페이지 응답
     */
    @GetMapping("/debits")
    public ResponseEntity searchMyDebit(@RequestParam String type,
                                        @RequestParam int size,
                                        @RequestParam int page,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        try {
            Page<DebitAmazonResponseDTO> pages = debitService.searchMyDebits(size, type, page, startDateTime, endDateTime, principal);
            return ResponseEntity.ok(new MultiResponseDto<>(pages.getContent(), pages));
        } catch (RestControllerException e) {
            if (e.getExceptionCode() == ExceptionCode.PERMISSION_DENIED) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else if (e.getExceptionCode() == ExceptionCode.USER_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생");
        }
    }
}