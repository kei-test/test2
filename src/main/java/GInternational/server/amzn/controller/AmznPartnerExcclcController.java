package GInternational.server.amzn.controller;

import GInternational.server.amzn.dto.excclc.AdminExcclcCalculateDTO;
import GInternational.server.amzn.dto.excclc.AmznExcclcDTO;
import GInternational.server.amzn.dto.excclc.AmznPartnerCountDTO;
import GInternational.server.amzn.service.AmznPartnerExcclcService;
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
public class AmznPartnerExcclcController {


    private final AmznPartnerExcclcService amznPartnerExcclcService;


    //상단 어드민 전용 집계
    @GetMapping("/excclc-calculate")
    public ResponseEntity getExcclcCalculate(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AdminExcclcCalculateDTO response = amznPartnerExcclcService.adminExcclcCalculate(startDate,endDate,principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    //통합정산 리스트
    @GetMapping("/excclc")
    public ResponseEntity getPartnerExcclc(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                           @RequestParam(required = false) String username,
                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmznExcclcDTO> response = amznPartnerExcclcService.getPartnerExcclc(startDate,endDate,username,principal);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    //상단 파트너카운트
    @GetMapping("/partner-count")
    public ResponseEntity getPartnerCount(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmznPartnerCountDTO response = amznPartnerExcclcService.getPartnerCount(principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }
}
