package GInternational.server.amzn.controller;

import GInternational.server.amzn.dto.log.AmznLogDTO;
import GInternational.server.amzn.service.AmznLogService;
import GInternational.server.common.dto.MultiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AmznLogController {


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
