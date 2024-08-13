package GInternational.server.api.controller;

import GInternational.server.api.dto.EditPreMatchDataDTO;
import GInternational.server.api.dto.EditPreMatchDataList;
import GInternational.server.api.service.EditResultsService;
import GInternational.server.l_sport.batch.job.dto.edit.EditMatchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class EditController {

    private final EditResultsService editResultsService;




    //커스텀 게임별 조회 or type null 일 경우 모든 프리매치를 가져온다
    @GetMapping("/result")
    public ResponseEntity getResultData(@RequestParam(required = false) Long type) {
        List<EditMatchResponseDTO> content = editResultsService.searchByEditData(type);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }


    @PatchMapping("/edit/pre-match/update")
    public ResponseEntity updateMatchData(@RequestBody List<EditPreMatchDataList> editMatchDataRequestDTO) {
        editResultsService.updatePreMatchData(editMatchDataRequestDTO);
            return ResponseEntity.ok("ok");
    }
}
