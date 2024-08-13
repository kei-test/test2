package GInternational.server.l_sport.info.controller.pre;


import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.l_sport.info.dto.count.PreMatchGameCountResponseDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetFixtureDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetFixtureResponseDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetSportResponseDTO;
import GInternational.server.l_sport.info.dto.results.GameResultListResponseDTO;
import GInternational.server.l_sport.info.dto.results.GameResultResponseDTO;
import GInternational.server.l_sport.info.entity.Match;
import GInternational.server.l_sport.info.repository.FixtureRepository;
import GInternational.server.l_sport.info.service.PreMatchFixtureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class PreMatchFixtureController {

    private final PreMatchFixtureService preMatchFixtureService;

    private final FixtureRepository fixtureRepository;


    //종목별 경기수(베팅 가능한 갯수)
    @GetMapping("/getGameCount/{type}")
    public ResponseEntity<Map<String, PreMatchGameCountResponseDTO>> getGameCount(@PathVariable("type") Long type) {
        Map<String, PreMatchGameCountResponseDTO> response = preMatchFixtureService.getGameCount(type);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    //종목별 경기(베팅) 전체목록
    @GetMapping("/getSport/{type}")
    public ResponseEntity getSport(@PathVariable("type") Long type) {
        List<PreMatchGetSportResponseDTO> response = preMatchFixtureService.getSport(type);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/get-preMatchFixture")
    public ResponseEntity getPreMatchFixture(@RequestParam(required = false) String matchId,
                                             @RequestParam(required = false) String type,
                                             @RequestParam(required = false) String sportName) {
        if (matchId != null) {
            List<PreMatchGetFixtureDTO> response = preMatchFixtureService.getPreMatchFixturesDetail(matchId,type);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            List<PreMatchGetFixtureResponseDTO> response = preMatchFixtureService.getPreMatchFixtures(sportName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("/getGameResultList/{type}")
    public ResponseEntity getGameResultList(@PathVariable("type") Long type,
                                            @RequestParam(required = false) int page,
                                            @RequestParam(required = false) int size) {

        if (type == 2 || type == 3) {
            // 인플레이, 프리매치
            Page<GameResultResponseDTO> response = preMatchFixtureService.getGameResult(type, page, size);
            return new ResponseEntity<>(new MultiResponseDto<>(response.getContent(), response), HttpStatus.OK);
        } else {
            Page<GameResultListResponseDTO> response = preMatchFixtureService.getGameResultList(type,page -1 ,size);
            return new ResponseEntity<>(new MultiResponseDto<>(response.getContent(), response), HttpStatus.OK);
        }
    }
}
