package GInternational.server.l_sport.info.controller.inplay;

import GInternational.server.l_sport.info.dto.pre.OddResponseDTO;
import GInternational.server.l_sport.info.service.OddLiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class OddLiveController {

    private final OddLiveService oddLiveService;


    @PostMapping("/odd-live/idx")
    public ResponseEntity validateOddLivePrice(@RequestBody List<String> oddLiveList) {
        List<OddResponseDTO> list = oddLiveService.validatePrice(oddLiveList);
        return ResponseEntity.ok(list);
    }
}
