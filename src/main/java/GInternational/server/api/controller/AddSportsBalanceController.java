package GInternational.server.api.controller;

import GInternational.server.api.dto.AddPointRequestDTO;
import GInternational.server.api.dto.AddSportsBalanceRequestDTO;
import GInternational.server.api.service.AddSportsBalanceService;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AddSportsBalanceController {

    private final AddSportsBalanceService addSportsBalanceService;

    @PatchMapping("/managers/add-sportsBalance/{userId}/{walletId}")
    public ResponseEntity<?> modifyUserPoints(@PathVariable("userId") @Positive Long userId,
                                              @PathVariable("walletId") @Positive Long walletId,
                                              @RequestBody AddSportsBalanceRequestDTO requestDTO,
                                              HttpServletRequest request,
                                              Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        try {
            addSportsBalanceService.modifySportsBalance(userId, walletId, requestDTO, request, principalDetails);
            String operationMessage = requestDTO.getOperation().equals("지급") ? "지급" : "차감";
            return ResponseEntity.ok("머니가 성공적으로 " + operationMessage + "되었습니다.");
        } catch (RestControllerException e) {
            if (e.getExceptionCode() == ExceptionCode.USER_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getExceptionCode() == ExceptionCode.WALLET_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생");
        }
    }
}
