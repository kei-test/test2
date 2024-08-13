package GInternational.server.api.controller;

import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.api.entity.PointTransaction;
import GInternational.server.api.mapper.PointTransactionResponseMapper;
import GInternational.server.api.service.PointTransactionService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class PointTransactionController {

    private final PointTransactionService pointService;
    private final PointTransactionResponseMapper mapper;

    /**
     * 특정 사용자의 포인트 거래 내역을 조회.
     *
     * @param userId           사용자 ID
     * @param page             페이지 번호
     * @param size             페이지 크기
     * @param authentication   인증 정보
     * @return 포인트 거래 내역과 페이징 정보
     */
    @GetMapping("/users/{userId}/point/transaction")
    public ResponseEntity getPointTransaction(@PathVariable("userId") @Positive Long userId,
                                              @RequestParam int page,
                                              @RequestParam int size,
                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<PointTransaction> transactions = pointService.getPointTransactionsByUserId(userId,page,size,principal);
        List<PointTransaction> list = transactions.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(list, transactions), HttpStatus.OK);
    }
}
