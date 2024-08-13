package GInternational.server.l_sport.info.controller.pre;

import GInternational.server.l_sport.info.dto.pre.OddResponseDTO;
import GInternational.server.l_sport.info.service.OddService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OddController {


    private final OddService oddService;


    @PostMapping("/odd/idx")
    public ResponseEntity validateOddPrice(@RequestBody List<String> oddList) {
        List<OddResponseDTO> list = oddService.validatePrice(oddList);
        return ResponseEntity.ok(list);
    }
}
