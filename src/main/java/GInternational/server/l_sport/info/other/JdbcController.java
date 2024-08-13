//package GInternational.server.l_sport.lsports.info;
//
//import GInternational.server.l_sport.lsports.pack.sport.Sport;
//import lombok.RequiredArgsConstructor;
//import org.hibernate.engine.jdbc.spi.JdbcServices;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//public class JdbcController {
//
//
//    private final JdbcTemplates jdbcTemplates;
//
//
//
//
////    @GetMapping("/{name}")
////    public String getSportByName(@PathVariable String name) {
////        return jdbcTemplates.findSportNameById(name);
////    }
//
//
//    @GetMapping("/find/sports")
//    public List<String> findAll() {
//        return jdbcTemplates.findAllSportNames();
//    }
//
//
//    @GetMapping("/all")
//    public List<Map<String, Long>> getAllSports() {
//        return jdbcTemplates.findAllSettlement();
//    }
//}
