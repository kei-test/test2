package GInternational.server.l_sport.info.controller.pre;

import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.l_sport.info.dto.admin.AdminPreMatchDTO;
import GInternational.server.l_sport.info.service.AdminMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AdminPreMatchController {


    private final AdminMatchService adminMatchService;

    //토탈 어마운트 빠져있음
    @GetMapping("/get-match")
    public ResponseEntity getMatches(@RequestParam (required = false) String type,
                                     @RequestParam (required = false) String status,
                                     @RequestParam (required = false) String sportsName,
                                     @RequestParam (required = false) String leagueName,
                                     @RequestParam int page,
                                     @RequestParam int size) {
        Page<AdminPreMatchDTO> content =  adminMatchService.searchAdminMatch(type,status,sportsName,leagueName,page,size);
        return new ResponseEntity(new MultiResponseDto<>(content.getContent(),content), HttpStatus.OK);
    }

}
