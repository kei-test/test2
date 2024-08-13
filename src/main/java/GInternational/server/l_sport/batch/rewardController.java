package GInternational.server.l_sport.batch;

import GInternational.server.l_sport.batch.job.service.JdbcBatchService;
import GInternational.server.l_sport.info.entity.Match;
import GInternational.server.l_sport.info.repository.FixtureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class rewardController {

    private final JdbcBatchService jdbcBatchService;

    @PostMapping("/reward")
    public void reward() {
        jdbcBatchService.rewardProcess();
    }


//    @PostMapping("/remove")
//    public void remove() {
//        jdbcBatchService.removeLs();
//    }
}
