package GInternational.server.api.controller;

import GInternational.server.api.vo.AppStatus;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.dto.SingleResponseDto;

import GInternational.server.api.entity.Account;
import GInternational.server.api.mapper.AccountAdminPageResponseMapper;
import GInternational.server.api.dto.AccountReqDTO;
import GInternational.server.api.mapper.AccountAdminResponseMapper;
import GInternational.server.api.service.AccountService;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountAdminResponseMapper accountAdminResponseMapper;
    private final AccountAdminPageResponseMapper accountAdminPageResponseMapper;


    /**
     * 사용자에게 새로운 계좌를 생성하고 지급.
     *
     * @param userId 대상 사용자의 ID
     * @param accountReqDTO 계좌 생성 요청 데이터
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 생성된 계좌 정보와 HTTP 상태 코드 CREATED
     */
    @PostMapping("/users/{userId}/account")
    public ResponseEntity insertAccount(@PathVariable("userId") Long userId,
                                        @RequestBody AccountReqDTO accountReqDTO,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            Account response = accountService.insertAccount(userId, accountReqDTO, principal);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RestControllerException e) {
            if (e.getExceptionCode() == ExceptionCode.ACCOUNT_ALREADY_EXISTS) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("서버 에러", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 관리자가 사용자의 계좌 승인 상태를 업데이트.
     *
     * @param userId 대상 사용자의 ID
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 업데이트된 계좌 정보와 HTTP 상태 코드 OK
     */
    @PatchMapping("/managers/{userId}/account/approval")
    public ResponseEntity statusUpdate(@PathVariable("userId") Long userId,
                                       HttpServletRequest request,
                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            Account response = accountService.statusUpdate(userId, principal, request);
            return new ResponseEntity<>(new SingleResponseDto<>(accountAdminResponseMapper.toDto(response)), HttpStatus.OK);
        } catch (RestControllerException e) {
            if (e.getExceptionCode() == ExceptionCode.ONLY_WAITING_TRANSACTIONS_CAN_BE_APPROVED) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("서버 에러", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 관리자가 특정 상태의 계좌를 조회.
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param status 조회하려는 계좌의 상태
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 조회된 계좌 목록과 페이징 정보, HTTP 상태 코드 OK
     */
    @GetMapping("/managers/accounts")
    public ResponseEntity getAccount(@RequestParam int page,
                                     @RequestParam int size,
                                     @RequestParam AppStatus status,
                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<Account> pages = accountService.getAccounts(status, page, size, principal);
        List<Account> list = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(accountAdminPageResponseMapper.toDto(list),pages),HttpStatus.OK);
    }

    /**
     * 관리자가 하나 이상의 계좌를 삭제.
     *
     * @param accountIds 삭제하려는 계좌의 ID 목록
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return HTTP 상태 코드 NO_CONTENT
     */
    @DeleteMapping("/managers/delete")
    public ResponseEntity deleteAccount(@RequestParam("accountIds") List<@Positive Long> accountIds,
                                        HttpServletRequest request,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        accountService.deleteAccount(accountIds, principal, request);
        return ResponseEntity.noContent().build();
    }
}
