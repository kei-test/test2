package GInternational.server.api.controller;

import GInternational.server.api.dto.WalletResponseDTO;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.mapper.WalletListResponseMapper;
import GInternational.server.api.dto.WalletRequestDTO;

import GInternational.server.api.service.WalletService;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.dto.SingleResponseDto;
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
public class WalletController {

    /**
     * 계좌 입금 시 입금받을 수 있는지 여부를 설정한다 ?
     * 계좌 활성화 여부 ?
     */


    private final WalletService walletService;
    private final WalletListResponseMapper walletListResponseMapper;

    /**
     * 지갑 업데이트.
     *
     * @param walletId          지갑 ID
     * @param walletRequestDTO  업데이트할 지갑 정보 DTO
     * @param authentication    인증 정보
     * @return                  업데이트된 지갑 정보의 응답 엔터티
     */
    @PatchMapping("/managers/wallet/{walletId}")
    public ResponseEntity updateWallet(@PathVariable("walletId") @Positive Long walletId,
                                       @RequestBody WalletRequestDTO walletRequestDTO,
                                       HttpServletRequest request,
                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        WalletResponseDTO response = walletService.updateWallet(walletId, walletRequestDTO, principal, request);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    /**
     * 사용자의 지갑 정보 조회.
     *
     * @param userId            사용자 ID
     * @param authentication    인증 정보
     * @return                  지갑 정보의 응답 엔터티
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity getWallet(@PathVariable("userId") @Positive Long userId,
                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Wallet response = walletService.detailWallet(userId,principal);
        return new ResponseEntity<>(new SingleResponseDto<>(response),HttpStatus.OK);
    }

    /**
     * 사용자의 총 정산 정보 조회.
     *
     * @param userId            사용자 ID
     * @param authentication    인증 정보
     * @return                  총 정산 정보의 응답 엔터티
     */
    @GetMapping("/managers/{userId}/total")
    public ResponseEntity getTotal(@PathVariable ("userId") @Positive Long userId,
                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Wallet wallet = walletService.totalSettlement(userId, principal);
        return ResponseEntity.ok(wallet);
    }

    /**
     * 모든 지갑 목록을 페이지로 반환.
     *
     * @param page              페이지 번호
     * @param size              페이지 크기
     * @param authentication    인증 정보
     * @return                  페이지로 구성된 지갑 목록의 응답 엔터티
     */
    @GetMapping("/managers/wallets")
    public ResponseEntity findAllWallet(@Positive @RequestParam int page,
                                        @Positive @RequestParam int size,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<Wallet> pages = walletService.findAllWallet(page,size,principal);
        List<Wallet> response = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(walletListResponseMapper.toDto(response),pages),HttpStatus.OK);
    }
}
