package GInternational.server.kplay.credit.controller;

import GInternational.server.kplay.credit.dto.CreditRequestDTO;
import GInternational.server.kplay.credit.dto.CreditResponseDTO;
import GInternational.server.kplay.credit.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    /**
     * 사용자에게 크레딧(베팅)을 추가.
     *
     * @param creditRequestDTO 크레딧 추가 요청 정보를 담은 DTO
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return ResponseEntity 처리 결과를 담은 CreditResponseDTO를 ResponseEntity로 반환
     */
    @PostMapping("/credit")
    public ResponseEntity calledCredit(@RequestBody CreditRequestDTO creditRequestDTO,
                                       @RequestHeader("secret-key") String secretHeader) {
        CreditResponseDTO response = creditService.calledCredit(creditRequestDTO,secretHeader);
        return ResponseEntity.ok(response);
    }
}

