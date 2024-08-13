package GInternational.server.kplay.buyin.controller;

import GInternational.server.kplay.buyin.dto.BuyinRequestDTO;
import GInternational.server.kplay.buyin.dto.BuyinResponseDTO;
import GInternational.server.kplay.buyin.service.BuyinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BuyInController {

    private final BuyinService buyinService;

    /**
     * 게임 내 구매 요청을 처리하고 응답을 반환.
     *
     * @param buyinRequestDTO 구매 요청 정보를 담은 DTO
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return ResponseEntity 구매 응답 DTO를 담은 ResponseEntity 객체
     */
    @PostMapping("/buyin")
    public ResponseEntity<BuyinResponseDTO> info(@RequestBody BuyinRequestDTO buyinRequestDTO,
                                                 @RequestHeader("secret-key") String secretHeader) {
        BuyinResponseDTO response = buyinService.getInfo(buyinRequestDTO, secretHeader);
        return ResponseEntity.ok(response);
    }
}
