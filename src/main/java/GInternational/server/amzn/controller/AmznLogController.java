package GInternational.server.amzn.controller;

import GInternational.server.amzn.dto.log.AmznLogDTO;
import GInternational.server.amzn.repo.AmazonRollingTransactionRepository;
import GInternational.server.amzn.service.AmznLogService;
import GInternational.server.api.entity.AmazonRollingTransaction;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.common.dto.MultiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AmznLogController {


    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AmazonRollingTransactionRepository am;

    private final AmznLogService amznLogService;



    @GetMapping("/system")
    public ResponseEntity getAmazonSystemLog(@RequestParam int page,
                                             @RequestParam int size,
                                             @RequestParam (required = false) String username,
                                             @RequestParam (required = false) String nickname,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Page<AmznLogDTO> pages = amznLogService.amazonSystemLog(username,nickname,startDate,endDate,page,size);
        return new ResponseEntity<>(new MultiResponseDto<>(pages.getContent(),pages), HttpStatus.OK);
    }
}
