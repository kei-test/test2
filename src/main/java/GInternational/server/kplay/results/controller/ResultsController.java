package GInternational.server.kplay.results.controller;

import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.kplay.results.dto.SavedCreditDTO;
import GInternational.server.kplay.results.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResultsController {

    private final ResultService resultService;

    /**
     * Debit에는 존재하지만 Credit에는 없는 데이터(미처리 베팅 결과)를 조회.
     *
     * @return ResponseEntity<List<Debit>> 미처리된 베팅 데이터 목록
     */
    @GetMapping("/debitResults")
    public ResponseEntity<List<Debit>> getUnmatchedData() {
        List<Debit> unmatchedData = resultService.getPrdAndTxnDTO();
        return new ResponseEntity<>(unmatchedData, HttpStatus.OK);
    }

    /**
     * 저장된 Credit 결과를 반환합니다. 클라이언트로부터 Credit 정보를 받아 처리 결과를 반환.
     *
     * @param savedCreditDTO 처리할 Credit 데이터
     * @return ResponseEntity 저장 처리된 Credit 결과
     */
    @GetMapping("/results")
    public ResponseEntity savedCreditResult(@RequestBody SavedCreditDTO savedCreditDTO) {  // 담겨있어야 서비스로직으로 그 매개변수가 넘어간다 -> 뭘 넘겨야할지 알 수 없다.
        SavedCreditDTO response = resultService.debitResults(savedCreditDTO);
        return ResponseEntity.ok(response);
    }
}
