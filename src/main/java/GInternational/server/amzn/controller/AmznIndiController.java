package GInternational.server.amzn.controller;

import GInternational.server.amzn.dto.indi.business.AmznPartnerRollingInfo;
import GInternational.server.amzn.dto.indi.business.AmznPartnerWalletDTO;
import GInternational.server.amzn.dto.indi.business.AmznRollingTransactionResDTO;
import GInternational.server.amzn.dto.indi.calculate.AmznIndiTotalCalculateDTO;
import GInternational.server.amzn.dto.indi.calculate.UserBetweenCalculateDTO;
import GInternational.server.amzn.dto.indi.indi_response.AmznIndiPartnerResDTO;
import GInternational.server.amzn.service.AmznIndiService;
import GInternational.server.amzn.service.AmznRollingTransactionService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AmznIndiController {


    private final AmznRollingTransactionService amznRollingTransactionService;
    private final AmznIndiService amznIndiService;


    //토큰을 통해 비즈니스 로직에서 케이스별 처리


    //<개별> 하부총판 리스트 조회
    @GetMapping("/indi-list")
    public ResponseEntity getIndiRollingInfo(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmznIndiPartnerResDTO> response = amznIndiService.getIndiResults(startDate,endDate,principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping("/indi-total-calculate")
    public ResponseEntity getCalculate(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmznIndiTotalCalculateDTO response = amznIndiService.getCalculate(startDate,endDate,principal);
        return new ResponseEntity(response,HttpStatus.OK);

    }


    //상위 추천인에게 지급된 롤링마일리지 지급 내역 조회
    @GetMapping("/indi-rolling-transaction")
    public ResponseEntity getIndiRollingTransaction(@RequestParam Long userId,
                                                    @RequestParam(required = false) String category,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmznRollingTransactionResDTO> response = amznRollingTransactionService.getIndiRollingTransaction(userId,category,startDate,endDate,principal);
        return new ResponseEntity(response, HttpStatus.OK);
    }


    //특정 파트너의 현재 지갑 금액정보
    @GetMapping("/indi-partner-wallet")
    public ResponseEntity getPartnerWallet(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmznPartnerWalletDTO response = amznIndiService.getPartnerWallet(principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }




    //상위 파트너의 카지노,슬롯 롤링요율 조회
    @GetMapping("/indi-rolling-info")
    public ResponseEntity getPartnerRollingInfo(@RequestParam Long userId,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmznPartnerRollingInfo response = amznRollingTransactionService.getPartnerRollingInfo(userId,principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }



    @GetMapping("/indi-detail-wallet")
    public ResponseEntity getDetailWallet(@RequestParam Long userId,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        UserBetweenCalculateDTO response = amznIndiService.getUserCalculate(userId,startDate,endDate,principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }
}
