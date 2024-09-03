package GInternational.server.amzn.controller;


import GInternational.server.amzn.dto.index.AmznDetailsByTypeDTO;
import GInternational.server.amzn.dto.index.AmznPartnerTreeDTO;
import GInternational.server.amzn.dto.index.AmznUserDetailDTO;

import GInternational.server.amzn.dto.index.IsAmazonUserListDTO;
import GInternational.server.amzn.dto.total.AmznPartnerResDTO;
import GInternational.server.amzn.service.AmznTotalService;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AmznTotalController {

    private final AmznTotalService amznTotalService;


    //하부통합 메인화면 리스트 반환
    @GetMapping("/total-partner")
    public ResponseEntity getTotalPartner(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<?> list = amznTotalService.getTotalList(principal);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    //유저 숫자 클릭 시 추천인으로인해 가입된 회원리스트
    @GetMapping("/total-is-amazon-user")
    public ResponseEntity getIsAmazonUsers(@RequestParam String referredBy,
                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<IsAmazonUserListDTO> response = amznTotalService.getAmazonUserList(referredBy,principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    //회원상세조회 회원의 상위 반환
    @GetMapping("/total-tree-partner")
    public ResponseEntity getTreePartner(@RequestParam Long userId,Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmznPartnerTreeDTO> response = amznTotalService.getPartnerTree(userId,principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    //아마존 회원 상세조회
    @GetMapping("/total-amzn-user-detail")
    public ResponseEntity getAmznUserDetail(@RequestParam Long userId,Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmznUserDetailDTO response = amznTotalService.getAmznUserDetail(userId,principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    //아마존 등급별 상세조회
    @GetMapping("/total-usertype-detail")
    public ResponseEntity getAmznDetailByType(@RequestParam Long userId,Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmznDetailsByTypeDTO response = amznTotalService.getAmznDetailsByType(userId,principal);
        return new ResponseEntity(response,HttpStatus.OK);
    }









    //검색쿼리는 프론트에서 구현하므로 미사용 컨트롤러
    @GetMapping("/get-partner")
    public ResponseEntity getPartner(@RequestParam(required = false) String username,
                                     @RequestParam(required = false) String nickname,
                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmznPartnerResDTO response = amznTotalService.getPartner(username, nickname, principal);
        return new ResponseEntity(new SingleResponseDto<>(response), HttpStatus.OK);
    }
}
