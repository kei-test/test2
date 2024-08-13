package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonCommentReqDTO;
import GInternational.server.api.dto.AmazonCommentResDTO;
import GInternational.server.api.service.AmazonCommentService;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class AmazonCommentController {

    private final AmazonCommentService commentService;

    /**
     * Amazon 커뮤니티 게시글에 댓글을 추가.
     *
     * @param amazonCategoryId Amazon 카테고리 ID
     * @param communityId 커뮤니티 게시글 ID
     * @param commentReqDTO 댓글 추가 요청 데이터
     * @param authentication 현재 인증된 사용자 정보
     * @return 생성된 댓글에 대한 응답 DTO와 함께 HTTP 상태 CREATED 반환
     */
    @PostMapping("/managers/{amazonCategoryId}/{communityId}/comment")
    public ResponseEntity insertComment(@PathVariable("amazonCategoryId") Long amazonCategoryId,
                                        @PathVariable("communityId") Long communityId,
                                        @RequestBody AmazonCommentReqDTO commentReqDTO,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonCommentResDTO response = commentService.insertAmazonComment(communityId, commentReqDTO, principal);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.CREATED);
    }

    /**
     * 특정 Amazon 커뮤니티 게시글의 모든 댓글을 조회.
     *
     * @param amazonCommunityId 조회할 커뮤니티 게시글 ID
     * @return 조회된 댓글 목록과 함께 HTTP 상태 OK 반환
     */
    @GetMapping("/users/{amazonCategoryId}/{amazonCommunityId}/comments")
    public ResponseEntity getCommentsByBoardId(@PathVariable("amazonCommunityId") @Positive Long amazonCommunityId,
                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmazonCommentResDTO> commentPage = commentService.getCommentsByArticlesId(amazonCommunityId, principal);
        return new ResponseEntity<>(commentPage, HttpStatus.OK);
    }

    /**
     * 특정 Amazon 커뮤니티 게시글의 댓글을 수정. 관리자와 매니저만 수정 가능.
     *
     * @param amazonCategoryId Amazon 카테고리 ID
     * @param communityId 커뮤니티 게시글 ID
     * @param commentId 수정할 댓글의 ID
     * @param updatedCommentReqDTO 수정할 댓글의 새로운 내용을 담은 DTO
     * @param authentication 현재 인증된 사용자 정보
     * @return 수정된 댓글 정보를 담은 DTO와 함께 HTTP 상태 200 OK 반환.
     * @throws RuntimeException 유저 없음, 댓글 찾을 수 없음, 또는 권한 없는 경우 예외 발생.
     */
    @PutMapping("/managers/{amazonCategoryId}/{communityId}/comments/{commentId}")
    public ResponseEntity<AmazonCommentResDTO> updateComment(@PathVariable Long amazonCategoryId,
                                                             @PathVariable Long communityId,
                                                             @PathVariable Long commentId,
                                                             @RequestBody AmazonCommentReqDTO updatedCommentReqDTO,
                                                             Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        AmazonCommentResDTO updatedComment = commentService.updateAmazonComment(commentId, updatedCommentReqDTO, principalDetails);
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * Amazon 커뮤니티 게시글의 특정 댓글을 삭제.
     *
     * @param amazonCategoryId Amazon 카테고리 ID
     * @param communityId 커뮤니티 게시글 ID
     * @param commentId 삭제할 댓글 ID
     * @param authentication 현재 인증된 사용자 정보
     * @return HTTP 상태 NO_CONTENT 반환
     */
    @DeleteMapping("/managers/{amazonCategoryId}/{communityId}/{commentId}")
    public ResponseEntity deleteComment(@PathVariable("amazonCategoryId") Long amazonCategoryId,
                                        @PathVariable("communityId") @Positive Long communityId,
                                        @PathVariable("commentId") @Positive Long commentId,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        commentService.deleteComment(commentId, principal);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
