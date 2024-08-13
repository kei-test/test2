package GInternational.server.api.controller;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.Articles;
import GInternational.server.api.mapper.ArticleResponseMapper;
import GInternational.server.api.service.ArticleService;
import GInternational.server.common.advice.ErrorResponse;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleResponseMapper articleResponseMapper;

    /**
     * 게시물 생성.
     * @param articlesRequestDTO 게시물 정보
     * @param request 작성자의 ip 정보
     * @param authentication 사용자 인증 정보
     * @return 생성된 게시물 정보와 메시지
     */
    @PostMapping("/users/category/{categoryId}/articles")
    public ResponseEntity<?> insertArticles(@PathVariable("categoryId") @Positive Long categoryId,
                                            @RequestBody ArticlesRequestDTO articlesRequestDTO,
                                            HttpServletRequest request,
                                            Authentication authentication) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            ArticlesResponseDTO response = articleService.insertArticle(categoryId, articlesRequestDTO, principal, request);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", new SingleResponseDto<>(response));
            responseBody.put("message", "게시물이 생성되었습니다");
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 게시물 수정.
     * @param articlesRequestDTO 수정할 게시물 정보
     * @param request 관리자의 ip 정보
     * @param authentication 사용자 인증 정보
     * @return 수정된 게시물 정보와 메시지
     */
    @PatchMapping("/users/category/{categoryId}/{articleId}")
    public ResponseEntity updateArticles(@PathVariable("categoryId") @Positive Long categoryId,
                                         @PathVariable("articleId") @Positive Long articleId,
                                         @RequestBody ArticlesRequestDTO articlesRequestDTO,
                                         HttpServletRequest request,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        ArticlesResponseDTO response = articleService.updateArticle(categoryId, articleId, articlesRequestDTO, principal, request);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "게시물이 수정되었습니다");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 카테고리별 게시물 조회.
     * @param categoryId 카테고리 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param authentication 사용자 인증 정보
     * @return 카테고리별 게시물 목록
     */
    @GetMapping("/users/category/{categoryId}/articles")
    public ResponseEntity getArticles(@PathVariable("categoryId") @Positive Long categoryId,
                                      @RequestParam int page,
                                      @RequestParam int size,
                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<ArticlesListDTO> response = articleService.getArticleByCategory(categoryId, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(response.getContent(), response), HttpStatus.OK);
    }

    /**
     * 특정 사용자가 작성한 특정 카테고리 게시물 조회.
     * @param categoryName 카테고리 이름
     * @param userId 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param authentication 사용자 인증 정보
     * @return 사용자가 작성한 카테고리별 게시물 목록
     */
    @GetMapping("/users/{categoryName}/{userId}/articles")
    public ResponseEntity getArticles(@PathVariable("categoryName") String categoryName,
                                      @PathVariable("userId") @Positive Long userId,
                                      @RequestParam int page,
                                      @RequestParam int size,
                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<ArticlesListDTO> response = articleService.myArticles(categoryName,userId, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(response.getContent(), response), HttpStatus.OK);
    }

    /**
     * 게시물 상세 조회.
     * @param articleId 게시물 ID
     * @param authentication 사용자 인증 정보
     * @return 게시물 상세 정보 DTO
     */
    @GetMapping("/users/articles/{articleId}")
    public ResponseEntity<SingleResponseDto<ArticlesResponseDTO>> getArticle(@PathVariable("articleId") @Positive Long articleId,
                                                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        ArticlesResponseDTO responseDTO = articleService.detailArticle(articleId, principal);
        return ResponseEntity.ok(new SingleResponseDto<>(responseDTO));
    }

    /**
     * 사용자의 게시물 상세 조회.
     * @param articleId 게시물 ID
     * @param authentication 사용자 인증 정보
     * @return 사용자 게시물 상세 정보
     */
    @GetMapping("/users/myArticles/{articleId}")
    public ResponseEntity getMyArticle(@PathVariable ("articleId") @Positive Long articleId,
                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Articles response = articleService.myDetailArticle(articleId,principal);
        return new ResponseEntity<>(new SingleResponseDto<>(response),HttpStatus.OK);
    }

    /**
     * 게시물 삭제.
     * @param articleId 삭제할 게시물 ID
     * @param authentication 사용자 인증 정보
     * @return 삭제 성공 응답
     */
    @DeleteMapping("/users/category/{categoryId}/{articleId}")
    public ResponseEntity deleteArticle(@PathVariable("articleId") @Positive Long articleId,
                                        HttpServletRequest request,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        articleService.deleteArticle(articleId, principal, request);
        return ResponseEntity.ok("게시물이 삭제되었습니다");
    }

    /**
     * 상단 고정 게시물 조회.
     * @param authentication 사용자 인증 정보
     * @return 상단 고정 게시물 목록
     */
    @GetMapping("/top-articles")
    public ResponseEntity<List<ArticlesListDTO>> getTopArticles(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<ArticlesListDTO> response = articleService.getTopArticles(principal);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/normal/articles/search")
    public ResponseEntity<List<ArticlesListDTO>> searchArticles(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            Authentication authentication) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

        List<ArticlesListDTO> response = articleService.searchArticles(title, content, nickname, startDateTime, endDateTime, username, principal);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * "고객센터" 카테고리에 해당하는 모든 게시글을 조회.
     *
     * @param authentication 인증된 사용자의 정보를 담고 있는 Authentication 객체
     * @return 고객센터 카테고리에 해당하는 모든 게시글의 리스트를 ResponseEntity로 감싸서 반환
     */
    @GetMapping("/users/customer-center/articles/search")
    public ResponseEntity<List<ArticlesListDTO>> findAllCustomerCenterArticles(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            Authentication authentication) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59, 999999) : null;

        List<ArticlesListDTO> response = articleService.findAllCustomerCenterArticles(title, content, nickname, startDateTime, endDateTime, username, principal);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 이전 게시물 상세 정보 조회.
     * @param categoryId 카테고리 ID
     * @param articleId 게시물 ID
     * @param authentication 사용자 인증 정보
     * @return 이전 게시물 상세 정보
     */
    @GetMapping("/users/articles/{categoryId}/{articleId}/previous")
    public ResponseEntity<?> getPreviousArticleDetails(@PathVariable Long categoryId,
                                                       @PathVariable Long articleId,
                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            ArticlesResponseDTO previousArticleDetails = articleService.getPreviousArticleDetails(categoryId, articleId, principal);
            return ResponseEntity.ok(new SingleResponseDto<>(previousArticleDetails));
        } catch (RestControllerException e) {
            int status = e.getExceptionCode().getStatus();
            return new ResponseEntity<>(new ErrorResponse(status, e.getExceptionCode().getMessage()), HttpStatus.valueOf(status));
        }
    }

    /**
     * 다음 게시물 상세 정보 조회.
     * @param categoryId 카테고리 ID
     * @param articleId 게시물 ID
     * @param authentication 사용자 인증 정보
     * @return 다음 게시물 상세 정보
     */
    @GetMapping("/users/articles/{categoryId}/{articleId}/next")
    public ResponseEntity<?> getNextArticleDetails(@PathVariable Long categoryId,
                                                   @PathVariable Long articleId,
                                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            ArticlesResponseDTO nextArticleDetails = articleService.getNextArticleDetails(categoryId, articleId, principal);
            return ResponseEntity.ok(new SingleResponseDto<>(nextArticleDetails));
        } catch (RestControllerException e) {
            int status = e.getExceptionCode().getStatus();
            return new ResponseEntity<>(new ErrorResponse(status, e.getExceptionCode().getMessage()), HttpStatus.valueOf(status));
        }
    }

    /**
     * 로그인 문의 게시물 생성.
     * @param articlesLoginInquiryDTO 로그인 문의 정보
     * @return 생성된 로그인 문의 게시물 정보
     */
    @PostMapping("/login-inquiry")
    public ResponseEntity<?> createLoginInquiryArticle(@RequestBody ArticlesLoginInquiryDTO articlesLoginInquiryDTO,
                                                       HttpServletRequest request) {
        ArticlesResponseDTO response = articleService.createLoginInquiryArticle(articlesLoginInquiryDTO, request);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "게시물이 생성되었습니다");
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    /**
     * 게시물의 메모를 업데이트
     * @param articlesLoginInquiryDTO 업데이트할 메모 데이터
     * @param authentication 인증 정보
     * @return 업데이트된 게시물의 상세 정보
     */
    @PutMapping("/managers/update-memo")
    public ResponseEntity<ArticlesResponseDTO> updateMemo(@RequestBody ArticlesLoginInquiryDTO articlesLoginInquiryDTO,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        ArticlesResponseDTO updatedArticle = articleService.updateMemo(articlesLoginInquiryDTO, principal);
        return ResponseEntity.ok(updatedArticle);
    }

    /**
     * 로그인 문의 게시물의 답변 상태를 조건에 따라 업데이트하는 엔드포인트.
     * @param articleId 게시물 ID
     * @return 업데이트된 게시물의 응답 데이터
     */
    @PatchMapping("/managers/customer-center/{articleId}/complete")
    public ResponseEntity<?> completeLoginInquiry(@PathVariable Long articleId) {
        ArticlesResponseDTO response = articleService.updateLoginInquiryStatus(articleId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "로그인 문의의 답변 상태가 업데이트되었습니다.");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    /**
     * 로그인 문의 목록 조회
     *
     * @param categoryId 카테고리 ID
     * @param writerName 작성자 이름 (옵션)
     * @param ownerName 소유자 이름 (옵션)
     * @param phone 전화번호 (옵션)
     * @param ip IP 주소 (옵션)
     * @param startDate 시작 날짜 (옵션)
     * @param endDate 종료 날짜 (옵션)
     * @param authentication 인증 정보
     * @return 로그인 문의 목록 포함 ResponseEntity
     */
    @GetMapping("/managers/login-inquiries")
    public ResponseEntity<List<LoginInquiryListDTO>> getLoginInquiries(@RequestParam Long categoryId,
                                                                       @RequestParam(required = false) String writerName,
                                                                       @RequestParam(required = false) String ownerName,
                                                                       @RequestParam(required = false) String phone,
                                                                       @RequestParam(required = false) String ip,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LoginInquiryListDTO> inquiries = articleService.searchLoginInquiries(categoryId, writerName, ownerName, phone, ip, startDate, endDate, principal);
        return ResponseEntity.ok(inquiries);
    }

    /**
     * 게시글의 노출 상태를 업데이트.
     *
     * @param articleId 업데이트할 게시글의 ID.
     * @param viewStatus 설정할 새로운 노출 상태.
     * @return 업데이트된 게시글 정보를 담은 ResponseEntity 객체를 반환.
     */
    @PatchMapping("/managers/{articleId}/change/view-status")
    public ResponseEntity<Articles> updateArticleViewStatus(@PathVariable Long articleId,
                                                            @RequestParam String viewStatus,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Articles updatedArticle = articleService.updateViewStatus(articleId, viewStatus, principal);
        return ResponseEntity.ok(updatedArticle);
    }

    /**
     * 게시물의 댓글 허용 여부를 업데이트
     *
     * @param articleId 게시물 ID
     * @param commentAllowed 댓글 허용 여부
     * @return ResponseEntity with status OK
     */
    @PatchMapping("/managers/{articleId}/comment-allowed")
    public ResponseEntity<Void> updateCommentAllowed(@PathVariable Long articleId,
                                                     @RequestParam boolean commentAllowed,
                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        articleService.updateCommentAllowed(articleId, commentAllowed, principal);
        return ResponseEntity.ok().build();
    }
}
