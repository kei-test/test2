package GInternational.server.l_sport.info.controller.inplay;

import GInternational.server.l_sport.info.entity.Match;
import GInternational.server.l_sport.info.service.InPlayFixtureService;
import GInternational.server.l_sport.info.dto.inplay.InPlayGetFixtureDTO;
import GInternational.server.l_sport.info.dto.inplay.InPlayGetFixtureResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class InPlayFixtureController {


    private final InPlayFixtureService inPlayFixtureService;


    //    처음 경기목록 조회시 사용 메서드
    @GetMapping("/get-inPlayFixture")
    public ResponseEntity getInPlayFixture(@RequestParam(required = false) String matchId,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) String sportsName) {
        if (matchId != null) {
            List<InPlayGetFixtureDTO> response = inPlayFixtureService.getInPlayFixturesDetail(matchId,type);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            List<InPlayGetFixtureResponseDTO> response = inPlayFixtureService.getInPlayFixtures(sportsName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    @GetMapping("/local")
    public String getLocal() {
        LocalDateTime currentDateTime = LocalDateTime.now().minusDays(2);
        DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedStartTime = currentDateTime.format(currentFormatter);
        return formattedStartTime;
    }
}
