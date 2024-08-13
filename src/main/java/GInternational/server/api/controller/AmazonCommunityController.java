package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonCommunityReqDTO;
import GInternational.server.api.dto.AmazonCommunityResDTO;
import GInternational.server.api.entity.AmazonCategory;
import GInternational.server.api.entity.AmazonCommunity;
import GInternational.server.api.service.AmazonCommunityService;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class AmazonCommunityController {


    private final AmazonCommunityService amazonCommunityService;

    /**
     * 새 게시물을 생성.
     *
     * @param amazonCategoryId 카테고리 ID
     * @param amazonCommunityReqDTO 게시물 요청 데이터
     * @param authentication 인증 정보
     * @return 생성된 게시물의 응답 DTO
     */
    @PostMapping("/users/amazon-category/{amazonCategoryId}/community")
    public ResponseEntity insertCommunity(@PathVariable("amazonCategoryId") Long amazonCategoryId,
                                          @RequestBody AmazonCommunityReqDTO amazonCommunityReqDTO,
                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonCommunityResDTO response = amazonCommunityService.insertNotice(amazonCategoryId, amazonCommunityReqDTO,principal);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.CREATED);
    }

    /**
     * 게시물 수정.
     *
     * @param amazonCategoryId 카테고리 ID
     * @param communityId 게시물 ID
     * @param amazonCommunityReqDTO 수정할 게시물 데이터
     * @param authentication 인증 정보
     * @return 수정된 게시물의 응답 DTO
     */
    @PatchMapping("/users/amazon-category/{amazonCategoryId}/{communityId}")
    public ResponseEntity updateCommunity(@PathVariable("amazonCategoryId") Long amazonCategoryId,
                                         @PathVariable("communityId") Long communityId,
                                         @RequestBody AmazonCommunityReqDTO amazonCommunityReqDTO,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonCommunityResDTO response = amazonCommunityService.updateNotice(amazonCategoryId,communityId, amazonCommunityReqDTO, principal);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    /**
     * 특정 게시물 조회.
     *
     * @param communityId 게시물 ID
     * @param authentication 인증 정보
     * @return 조회된 게시물
     */
    @GetMapping("/users/{communityId}")
    public ResponseEntity getCommunity(@PathVariable("communityId") Long communityId,
                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonCommunity response = amazonCommunityService.detailCommunity(communityId,principal);
        return new ResponseEntity<>(new SingleResponseDto<>(response),HttpStatus.OK);
    }

    /**
     * 카테고리별 게시물 조회.
     *
     * @param amazonCategoryId 카테고리 ID
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @return 카테고리별 게시물 목록
     */
    @GetMapping("/users/amazon-category/{amazonCategoryId}/community")
    public ResponseEntity getCommunityByCategory(@PathVariable("amazonCategoryId") @Positive Long amazonCategoryId,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<AmazonCommunityResDTO> response = amazonCommunityService.getCommunityByCategory(amazonCategoryId, startDateTime, endDateTime, principal);
        return new ResponseEntity<>((response), HttpStatus.OK);
    }

    /**
     * 특정 게시물 삭제.
     *
     * @param communityId 게시물 ID
     * @param authentication 인증 정보
     * @return 삭제 성공시 HTTP 상태 NO_CONTENT
     */
    @DeleteMapping("/managers/{communityId}")
    public ResponseEntity deleteCommunity(@PathVariable("communityId") @Positive Long communityId,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonCommunityService.deleteCommunity(communityId,principal);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
