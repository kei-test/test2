package GInternational.server.api.controller;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.service.BetHistoryService;
import GInternational.server.api.vo.BetFoldCountEnum;
import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.l_sport.batch.job.dto.order.ResponseDTO;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class BetHistoryController {


    private final BetHistoryService betHistoryService;

    /**
     * 새로운 베팅 추가.
     *
     * @param betRequestDTOs 베팅 요청 데이터 전송 객체 리스트
     * @param betGroupId 베팅 그룹 ID
     * @param authentication 사용자 인증 정보
     * @param request HTTP 요청 정보
     * @return 생성된 베팅 내역에 대한 응답 데이터 전송 객체와 HTTP 상태 코드
     */
    @PostMapping("/users/bet")
    public ResponseEntity<MultiResponseDto<BetHistoryResDTO>> newBets(@RequestBody List<BetHistoryReqDTO> betRequestDTOs,
                                                                      @RequestParam Long betGroupId,
                                                                      Authentication authentication,
                                                                      HttpServletRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        List<BetHistory> betHistories = betHistoryService.insertUserBets(betRequestDTOs, betGroupId, principal, request);

        List<BetHistoryResDTO> responseDTOs = betHistories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new MultiResponseDto<>(responseDTOs), HttpStatus.OK);
    }


    @GetMapping("/validate")
    public ResponseEntity validate(@RequestParam Long betGroupId) {
        return ResponseEntity.ok(betHistoryService.validateBetGroupId(betGroupId));
    }




    //계층 구조의 베팅 내역 조회 /동적 쿼리 & 토큰을 통해 회원 여부만 확인하며 페이로드에 해당하는 베팅내역 조회
    @GetMapping("/users/orderHistory/get")
    public ResponseEntity getOrderResponse(@RequestParam int page,
                                           @RequestParam int size,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) BetTypeEnum custom,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String nickname,
                                           @RequestParam(required = false) BetFoldCountEnum foldCount,
                                           @RequestParam(required = false) String ip,
                                           @RequestParam(required = false) Long id,
                                           @RequestParam(required = false) List<Long> betGroupId,
                                           @RequestParam(required = false) OrderStatusEnum orderStatus,
                                           @RequestParam(required = false) Boolean deleted,
                                           @RequestParam(required = false) String orderBy,
                                           Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Page<ResponseDTO> response = betHistoryService.orderResponse(page,size,userId,custom,username,nickname,foldCount,ip,id,betGroupId,orderStatus,deleted,orderBy,startDate,endDate,principalDetails);
        return new ResponseEntity(new MultiResponseDto<>(response.getContent(),response),HttpStatus.OK);
    }


    /**
     * 경기전, 경기중인 모든 베팅 내역을 조회.
     *
     * @param authentication 사용자 인증 정보
     * @return 조회된 베팅 내역 리스트와 HTTP 상태 코드
     */
    @GetMapping("/managers/bets/before-end")
    public ResponseEntity<List<BetHistoryResDTO>> findAllByMatchStatusOneOrTwoBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<BetHistoryResDTO> responseDTOs = betHistoryService.findAllByMatchStatusOneOrTwoAndDateBetween(startDate, endDate, principal);
        return ResponseEntity.ok(responseDTOs);
    }


    /**
     * 경기 종료된 모든 베팅 내역을 조회.
     *
     * @param authentication 사용자 인증 정보
     * @return 조회된 베팅 내역 리스트와 HTTP 상태 코드
     */
    @GetMapping("/managers/bets/end")
    public ResponseEntity<List<BetHistoryResDTO>> findAllByMatchStatusThreeBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<BetHistoryResDTO> responseDTOs = betHistoryService.findAllByMatchStatusThreeAndDateBetween(startDate, endDate, principal);
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * 특정 사용자의 모든 베팅 내역을 조회합니다.
     * [사용중]
     * @param userId 사용자 ID
     * @param authentication 사용자 인증 정보
     * @return 조회된 베팅 내역 리스트와 HTTP 상태 코드
     */
    @GetMapping("/users/bets/{userId}")
    public ResponseEntity<List<BetHistoryResDTO>> findAllByUserId(@PathVariable("userId") @Positive Long userId,
                                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<BetHistory> betHistories = betHistoryService.findAllByUserId(userId, principal);
        List<BetHistoryResDTO> responseDTOs = betHistories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    //1

    /**
     * 특정 그룹ID의 베팅 내역 소프트 딜리트.
     *
     * @param betGroupId 베팅 그룹 ID
     * @param authentication 사용자 인증 정보
     * @return HTTP 상태 코드
     */
    @DeleteMapping("/users/bet/{betGroupId}")
    public ResponseEntity<Void> softDeleteBetHistoryByGroupId(@PathVariable("betGroupId") @Positive Long betGroupId,
                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        betHistoryService.softDeleteBetHistoryByGroupId(betGroupId, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 특정 사용자의 모든 베팅 내역 소프트 딜리트.
     *
     * @param userId 사용자 ID
     * @param authentication 사용자 인증 정보
     * @return 삭제 성공 메시지와 HTTP 상태 코드
     */
    @DeleteMapping("/users/bets/delete-all/{userId}")
    public ResponseEntity<String> softDeleteAllBetHistoryByUser(@PathVariable("userId") @Positive Long userId,
                                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        betHistoryService.softDeleteAllBetHistoryByUser(userId, principal);
        return ResponseEntity.ok("모든 베팅 내역이 삭제되었습니다.");
    }

    /**
     * 베팅 내역 객체를 데이터 전송 객체로 변환.
     *
     * @param betHistory 베팅 내역 객체
     * @return 베팅 내역 데이터 전송 객체
     */
    private BetHistoryResDTO convertToDto(BetHistory betHistory) {
        BetHistoryResDTO dto = new BetHistoryResDTO();
        BeanUtils.copyProperties(betHistory, dto);

        return dto;
    }

    @GetMapping("/managers/match/{matchId}")
    public ResponseEntity<BetHistoryCalculationResult> getBetHistoriesGroupedByBetGroupId(
            @PathVariable String matchId,
            Authentication authentication) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        BetHistoryCalculationResult calculationResult = betHistoryService.findBetHistoriesGroupedByBetGroupId(matchId, principal);
        return ResponseEntity.ok(calculationResult);
    }

    /**
     * 특정 그룹 ID에 대한 베팅 취소.
     *
     * @param betGroupId 베팅 그룹 ID
     * @param authentication 사용자 인증 정보
     * @return 취소 성공 메시지 또는 오류 메시지와 HTTP 상태 코드
     */
    @PostMapping("/users/cancelBets/{betGroupId}")
    public ResponseEntity<?> cancelBetsByGroupId(@PathVariable Long betGroupId,
                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            betHistoryService.cancelBetsByGroupId(betGroupId, principal);
            return ResponseEntity.ok().body("그룹 ID " + betGroupId + "에 대한 베팅이 성공적으로 취소되었습니다.");
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * 특정 그룹 ID에 속하는 취소된 베팅 복구. [유저가 삭제한 배팅내역을 관리자가 복구]
     *
     * @param groupId 복구하려는 베팅의 그룹 ID
     * @param authentication 사용자 인증 정보
     * @return 복구 성공 메시지와 HTTP 상태 코드. 인증 실패 시 오류 메시지와 HTTP 상태 코드 반환.
     */
    @PostMapping("/managers/restoreBets/{groupId}")
    public ResponseEntity<?> restoreCancelledBetsByGroupId(@PathVariable Long groupId,
                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            betHistoryService.restoreCancelledBetsByGroupId(groupId, principal);
            return ResponseEntity.ok().body("그룹 ID " + groupId + "에 대한 취소된 베팅이 성공적으로 복구되었습니다.");
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * 지정된 기간 동안 취소된 모든 베팅 조회.
     *
     * @param authentication 사용자 인증 정보
     * @param startDate 조회 시작 날짜와 시간
     * @param endDate 조회 종료 날짜와 시간
     * @return 조회된 취소된 베팅의 리스트와 HTTP 상태 코드
     */
    @GetMapping("/managers/cancelledBets")
    public ResponseEntity<List<BetResultDTO>> findAllCancelledBets(
            Authentication authentication,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<BetResultDTO> cancelledBets = betHistoryService.findAllCancelledBets(principal, startDate, endDate);
        return ResponseEntity.ok(cancelledBets);
    }

    /**
     * 관리자에 의해 취소된 베팅만을 지정된 기간 동안 조회.
     *
     * @param authentication 사용자 인증 정보
     * @param startDate 조회 시작 날짜와 시간
     * @param endDate 조회 종료 날짜와 시간
     * @return 조회된 취소된 베팅의 리스트와 HTTP 상태 코드
     */
    @GetMapping("/managers/cancelledBets/admin")
    public ResponseEntity<List<BetResultDTO>> findCancelledBetsByAdmin(
            Authentication authentication,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<BetResultDTO> cancelledBets = betHistoryService.findCancelledBetsByAdmin(principal, startDate, endDate);
        return ResponseEntity.ok(cancelledBets);
    }

    /**
     * 유저에 의해 취소된 베팅만을 지정된 기간 동안 조회.
     *
     * @param authentication 사용자 인증 정보
     * @param startDate 조회 시작 날짜와 시간
     * @param endDate 조회 종료 날짜와 시간
     * @return 조회된 취소된 베팅의 리스트와 HTTP 상태 코드
     */
    @GetMapping("/managers/cancelledBets/user")
    public ResponseEntity<List<BetResultDTO>> findCancelledBetsByUser(
            Authentication authentication,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<BetResultDTO> cancelledBets = betHistoryService.findCancelledBetsByUser(principal, startDate, endDate);
        return ResponseEntity.ok(cancelledBets);
    }
}
