package GInternational.server.api.service;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.*;
import GInternational.server.api.mapper.ArticleListResponseMapper;
import GInternational.server.api.mapper.ArticleRequestMapper;
import GInternational.server.api.mapper.ArticleResponseMapper;
import GInternational.server.api.repository.*;
import GInternational.server.api.vo.ExpRecordEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRequestMapper articleRequestMapper;
    private final ArticleResponseMapper articleResponseMapper;
    private final ArticleListResponseMapper articleListResponseMapper;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ExpRecordService expRecordService;
    private final DailyLimitRepository dailyLimitRepository;
    private final WalletRepository walletRepository;

    /**
     * 게시물 생성. [유저]
     * @param articlesRequestDTO 게시물 생성 요청 데이터
     * @param principalDetails 사용자 인증 정보
     * @return 생성된 게시물의 응답 데이터
     */
    @AuditLogService.Audit("게시물 작성")
    public ArticlesResponseDTO insertArticle(Long categoryId, ArticlesRequestDTO articlesRequestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow
                (()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));
        if (!user.isCanPost()) {
            throw new RestControllerException(ExceptionCode.PERMISSION_DENIED, "게시글 작성이 불가능합니다. 관리자에게 문의하세요.");
        }
        Category category = categoryRepository.findById(categoryId).orElseThrow
                (()-> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND, "카테로리를 찾을 수 없습니다."));

        DailyLimit dailyLimit = dailyLimitRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "DailyLimit 설정을 찾을 수 없습니다."));

        String clientIp = request.getRemoteAddr();

        // "게시판" 카테고리에서 유저의 일일 게시글 등록 제한 체크
        if ("게시판".equals(category.getName())) {
            LocalDate today = LocalDate.now();
            int userArticleCount = articleRepository.countByWriterAndCategoryAndCreatedAtBetween(
                    user, category, today.atStartOfDay(), today.plusDays(1).atStartOfDay());

            if (userArticleCount >= dailyLimit.getDailyArticleLimit()) {
                throw new RestControllerException(ExceptionCode.LIMIT_EXCEEDED, "일일 게시글 등록가능 갯수를 초과했습니다.");
            }
        }

        int point = dailyLimit.getDailyArticlePoint();
        Wallet userWallet = user.getWallet();
        userWallet.setPoint(userWallet.getPoint() + point);
        walletRepository.save(userWallet);

        if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) {
            AuditContext context = AuditContextHolder.getContext();
            context.setIp(clientIp);
            context.setTargetId(null);
            context.setUsername(null);
            context.setDetails("게시물 작성, 제목: " + articlesRequestDTO.getTitle() + ", " + "게시물 작성자: " + user.getUsername());
            context.setAdminUsername(user.getUsername());
            context.setTimestamp(LocalDateTime.now());
        } else if (!category.getCategoryRole().equals("ROLE_USER")) {
            throw new RuntimeException("권한이 없습니다.");
        }

        Articles articles = articleRequestMapper.toEntity(articlesRequestDTO);
        // 카테고리가 "고객센터"인 경우 답변 상태를 "답변대기"로 설정
        if ("고객센터".equals(category.getName())) {
            articles.setAnswerStatus("답변요청");
        } else {
            articles.setAnswerStatus("해당없음");
        }
        articles.setIsTop(articlesRequestDTO.getIsTop());
        articles.setViewStatus(articlesRequestDTO.getViewStatus());
        articles.setReadCount(articlesRequestDTO.getReadCount());
        articles.setCommentCount(0);
        articles.setWriter(user);
        articles.setWriterName(user.getUsername());
        String ownerName = Optional.ofNullable(user.getWallet()).map(Wallet::getOwnerName).orElse("지값 없음");
        articles.setOwnerName(ownerName);
        articles.setNickname(user.getNickname());
        articles.setPhone(user.getPhone());
        articles.setIp(clientIp);
        articles.setCategory(category);
        articles.setCommentAllowed(articlesRequestDTO.isCommentAllowed());
        articles.setCreatedAt(LocalDateTime.now());
        Articles savedArticle = articleRepository.save(articles);

        expRecordService.recordDailyExpUpToFiveTime(user.getId(), user.getUsername(), user.getNickname(), 1, clientIp, ExpRecordEnum.게시글작성경험치);

        return articleResponseMapper.toDto(savedArticle);
    }

    /**
     * 게시물 수정. [유저]
     * @param articlesRequestDTO 게시물 수정 요청 데이터
     * @param principalDetails 사용자 인증 정보
     * @return 수정된 게시물의 응답 데이터
     */
    @AuditLogService.Audit("게시물 수정")
    public ArticlesResponseDTO updateArticle(Long categoryId, Long articleId, ArticlesRequestDTO articlesRequestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND));
        Articles articles = articleRepository.findById(articleId).orElseThrow(()-> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND));
        User user = userService.validateUser(principalDetails.getUser().getId());

        Long articleWriter = articles.getWriter().getId();
        if (!user.getRole().equals("ROLE_ADMIN") && !user.getRole().equals("ROLE_MANAGER") && !user.getId().equals(articleWriter)) {
            throw new RestControllerException(ExceptionCode.PERMISSION_DENIED, "글의 작성자만 수정할 수 있습니다.");
        }

        Optional.ofNullable(articlesRequestDTO.getTitle()).ifPresent(articles::setTitle);
        Optional.ofNullable(articlesRequestDTO.getContent()).ifPresent(articles::setContent);
        // isTop과 readCount는 관리자 또는 매니저만 수정 가능
        if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) {
            Optional.ofNullable(articlesRequestDTO.getIsTop()).ifPresent(articles::setIsTop);
            Optional.ofNullable(articlesRequestDTO.getReadCount()).ifPresent(articles::setReadCount);
        }
        articles.setUpdatedAt(LocalDateTime.now());
        Articles savedArticle = articleRepository.save(articles);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("게시물 수정, 제목: " + articlesRequestDTO.getTitle() + ", " + "게시물 작성자: " + articles.getWriter().getUsername());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return articleResponseMapper.toDto(savedArticle);
    }

    /**
     * 게시물 상세 정보 조회. [유저]
     * @param articleId 게시물 ID
     * @param principalDetails 사용자 인증 정보
     * @return 게시물 상세 정보 DTO
     */
    public ArticlesResponseDTO detailArticle(Long articleId, PrincipalDetails principalDetails) {
        Articles article;
        String role = principalDetails.getUser().getRole();

        if (role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")) {
            article = validateArticle(articleId);
            if (article.getAnswerStatus().equals("답변요청")) {
                article.setAnswerStatus("답변대기");
                articleRepository.save(article);
            } else if (article.getAnswerStatus().equals("로그인문의 답변요청")) {
                article.setAnswerStatus("로그인문의 답변대기");
                articleRepository.save(article);
            }
        } else {
            article = articleRepository.findByIdAndViewStatus(articleId, "노출")
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND, "게시글을 찾을 수 없습니다."));
        }

        Long previousArticleId = getPreviousArticleId(articleId); // 이전 게시물 ID 조회 로직
        Long nextArticleId = getNextArticleId(articleId); // 다음 게시물 ID 조회 로직

        ArticlesResponseDTO responseDTO = new ArticlesResponseDTO();
        responseDTO.setWriter(article.getWriter() != null ? new UserProfileDTO(article.getWriter()) : null);
        responseDTO.setCategory(new CategoryResponseDTO(article.getCategory()));
        responseDTO.setId(article.getId());
        responseDTO.setTitle(article.getTitle());
        responseDTO.setContent(article.getContent());
        responseDTO.setIsTop(article.getIsTop());
        responseDTO.setOwnerName(article.getOwnerName());
        responseDTO.setReadCount(article.getReadCount());
        responseDTO.setCommentCount(article.getCommentCount());
        responseDTO.setPreviousArticleId(previousArticleId);
        responseDTO.setNextArticleId(nextArticleId);
        responseDTO.setIp(article.getIp());
        responseDTO.setAnswerStatus(article.getAnswerStatus());
        responseDTO.setViewStatus(article.getViewStatus());
        responseDTO.setCreatedAt(article.getCreatedAt());
        responseDTO.setUpdatedAt(article.getUpdatedAt());
        responseDTO.setCommentAllowed(article.isCommentAllowed());

        // 조회 수 증가
        article.setReadCount(article.getReadCount() + 1);
        articleRepository.save(article);

        return responseDTO;
    }

    /**
     * 사용자의 상세 게시물 정보를 조회. [유저]
     * 특정 조건(고객센터 게시물, 관리자 또는 작성자 본인 확인, viewStatus)에 따라 접근 제한을 확인.
     * @param articleId 게시물 ID
     * @param principalDetails 사용자 인증 정보
     * @return 게시물 상세 정보
     */
    public Articles myDetailArticle(Long articleId, PrincipalDetails principalDetails) {
        Articles article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND, "게시글을 찾을 수 없습니다."));
        String role = principalDetails.getUser().getRole();
        boolean isOwner = principalDetails.getUser().getId().equals(article.getWriter().getId());

        // 관리자 또는 매니저의 경우, 모든 게시글 조회 가능
        if (role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")) {
            if (article.getAnswerStatus().equals("답변요청")) {
                article.setAnswerStatus("답변대기");
                articleRepository.save(article);
            } else if (article.getAnswerStatus().equals("로그인문의 답변요청")) {
                article.setAnswerStatus("로그인문의 답변대기");
                articleRepository.save(article);
            }
            article.setReadCount(article.getReadCount() + 1);
            articleRepository.save(article);
            return article;
        } else if (isOwner && article.getViewStatus().equals("노출")) {
            // 일반 사용자는 자신이 작성한 "노출" 상태인 게시글만 조회 가능
            article.setReadCount(article.getReadCount() + 1);
            articleRepository.save(article);
            return article;
        } else {
            throw new RestControllerException(ExceptionCode.UNAUTHORIZED_ACCESS, "이 게시글을 볼 수 있는 권한이 없습니다.");
        }
    }

    /**
     * 사용자가 작성한 특정 카테고리의 모든 게시물 조회.
     * 관리자(ROLE_ADMIN)와 매니저(ROLE_MANAGER)는 viewStatus 값에 상관없이 모든 게시물 조회.
     * 일반 사용자(ROLE_USER)는 viewStatus가 "노출"인 게시물만 조회.
     *
     * @param categoryName 조회할 카테고리의 이름.
     * @param userId 조회를 요청한 사용자의 ID.
     * @param page 요청한 페이지 번호.
     * @param size 페이지 당 표시할 게시물 수.
     * @param principalDetails 현재 사용자의 인증 정보를 담은 PrincipalDetails 객체.
     * @return 해당 조건에 맞는 게시물 목록이 담긴 Page 객체 반환.
     */
    @Transactional(value = "clientServerTransactionManager", readOnly = true)
    public Page<ArticlesListDTO> myArticles(String categoryName, Long userId, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page -1, size,Sort.by("id").descending());

        Page<Articles> pages = articleRepository.searchByMyArticles(categoryName,userId,pageable,principalDetails);

        List<ArticlesListDTO> list = pages.getContent().stream()
                .map(articleListResponseMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(list,pageable,pages.getTotalElements());
    }

    /**
     * 특정 카테고리에 속하는 게시물들 조회.
     * 관리자(ROLE_ADMIN)와 매니저(ROLE_MANAGER)는 viewStatus 값에 상관없이 모든 게시물 조회.
     * 일반 사용자(ROLE_USER)는 viewStatus가 "노출"로 설정된 게시물만 조회.
     *
     * @param categoryId 조회할 카테고리의 ID.
     * @param page 조회할 페이지 번호.
     * @param size 한 페이지에 표시할 게시물의 수.
     * @param principalDetails 현재 인증된 사용자의 상세 정보를 포함.
     * @return 해당 카테고리에 속하는 게시물 목록을 페이지네이션 처리하여 반환.
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public Page<ArticlesListDTO> getArticleByCategory(Long categoryId, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1,size, Sort.by("id").descending());

        Page<Articles> Articlespage = articleRepository.findByCategoryAndArticles(categoryId,pageable,principalDetails);

        List<ArticlesListDTO> list = Articlespage.getContent().stream()
                .map(articleListResponseMapper::toDto)
                .collect(Collectors.toList());

        long totalElements = articleRepository.countByCategoryArticles(categoryId);
        return new PageImpl<>(list,pageable, totalElements);
    }

    /**
     * 게시물 삭제. 관리자, 매니저, 또는 게시물 작성자만 삭제할 수 있음.
     * @param articleId 삭제할 게시물 ID
     * @param principalDetails 사용자 인증 정보
     */
    @AuditLogService.Audit("게시물 삭제")
    public void deleteArticle(Long articleId, PrincipalDetails principalDetails, HttpServletRequest request) {
        Articles articles = articleRepository.findById(articleId).orElseThrow(() -> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND));
        User user = userService.validateUser(principalDetails.getUser().getId());

        boolean isAdminOrManager = user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER");
        boolean isArticleWriter = articles.getWriter() != null && user.getId().equals(articles.getWriter().getId());

        if (isAdminOrManager || isArticleWriter) {
            AuditContext context = AuditContextHolder.getContext();
            String clientIp = request.getRemoteAddr();
            context.setIp(clientIp);
            context.setTargetId(null);
            context.setUsername(null);
            context.setDetails("게시물 삭제, 제목: " + articles.getTitle() + ", " + (articles.getWriter() != null ? "게시물 작성자: " + articles.getWriter().getUsername() : "게시물 작성자 없음"));
            context.setAdminUsername(principalDetails.getUsername());
            context.setTimestamp(LocalDateTime.now());

            articleRepository.delete(articles);
        } else {
            throw new RuntimeException("글을 삭제할 권한이 없습니다.");
        }
    }

    /**
     * 게시물 ID를 통해 게시물을 검증하고 반환.
     * @param id 게시물 ID
     * @return 검증된 게시물
     */
    public Articles validateArticle(Long id) {
        Optional<Articles> articles = articleRepository.findById(id);
        Articles findArticle = articles.orElseThrow(()-> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND));
        return findArticle;
    }

    /**
     * 주어진 게시물 ID 이전의 게시물 ID를 찾음.
     * 현재 게시물보다 ID가 작은 가장 가까운 게시물의 ID를 반환.
     * @param id 현재 게시물 ID
     * @return 이전 게시물의 ID, 없으면 null
     */
    private Long getPreviousArticleId(Long id) {
        Long previousArticleId = null;
        Long currentId = id;

        while (previousArticleId == null && currentId > 1) {
            currentId--;
            if (articleRepository.existsById(currentId)) {
                previousArticleId = currentId;
            }
        }
        return previousArticleId;
    }

    /**
     * 주어진 게시물 ID 다음의 게시물 ID를 찾음.
     * 현재 게시물보다 ID가 큰 가장 가까운 게시물의 ID를 반환.
     * @param id 현재 게시물 ID
     * @return 다음 게시물의 ID, 없으면 null
     */
    private Long getNextArticleId(Long id) {
        Long nextArticleId = null;
        Long currentId = id;
        Long maxId = articleRepository.findMaxId();

        while (nextArticleId == null && currentId < maxId) {
            currentId++;
            if (articleRepository.existsById(currentId)) {
                nextArticleId = currentId;
            }
        }
        return nextArticleId;
    }

    /**
     * 상단 고정된 게시물 목록을 조회합니다.
     * 관리자와 매니저는 viewStatus에 상관없이 모든 상단 고정 게시물을 조회할 수 있습니다.
     * 일반 사용자는 viewStatus가 "노출"인 상단 고정 게시물만 조회할 수 있습니다.
     * 상단 고정 게시물은 생성 날짜 내림차순으로 정렬됩니다.
     *
     * @param principalDetails 사용자 인증 정보
     * @return 조회된 상단 고정 게시물 목록
     */
    public List<ArticlesListDTO> getTopArticles(PrincipalDetails principalDetails) {
        List<Articles> topArticles;

        if (principalDetails.getUser().getRole().equals("ROLE_ADMIN") || principalDetails.getUser().getRole().equals("ROLE_MANAGER")) {
            topArticles = articleRepository.findByIsTopTrueOrderByCreatedAtDesc();
        } else {
            topArticles = articleRepository.findByIsTopTrueAndViewStatusOrderByCreatedAtDesc("노출");
        }

        return topArticles.stream()
                .map(articleListResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    // 게시판 게시글 조회
    public List<ArticlesListDTO> searchArticles(String title, String content, String nickname, LocalDateTime startDateTime, LocalDateTime endDateTime, String username, PrincipalDetails principalDetails) {
        String viewStatus = isAdminOrManager(principalDetails) ? null : "노출";
        String categoryName = "게시판";

        List<Articles> articlesList = searchArticlesByCriteria(title, content, nickname, startDateTime, endDateTime, viewStatus, categoryName, username, principalDetails);

        return articlesList.stream()
                .map(articleListResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    // 고객센터 게시글 조회
    public List<ArticlesListDTO> findAllCustomerCenterArticles(String title, String content, String nickname, LocalDateTime startDateTime, LocalDateTime endDateTime, String username, PrincipalDetails principalDetails) {
        String viewStatus = isAdminOrManager(principalDetails) ? null : "노출";
        String categoryName = "고객센터";

        List<Articles> articlesList = searchArticlesByCriteria(title, content, nickname, startDateTime, endDateTime, viewStatus, categoryName, username, principalDetails);

        return articlesList.stream()
                .map(articleListResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean isAdminOrManager(PrincipalDetails principalDetails) {
        return "ROLE_ADMIN".equals(principalDetails.getUser().getRole()) || "ROLE_MANAGER".equals(principalDetails.getUser().getRole());
    }

    private List<Articles> searchArticlesByCriteria(String title, String content, String nickname, LocalDateTime startDateTime, LocalDateTime endDateTime, String viewStatus, String categoryName, String username, PrincipalDetails principalDetails) {
        return articleRepository.searchByAdvancedCriteria(title, content, nickname, viewStatus, categoryName, startDateTime, endDateTime, username);
    }

    /**
     * 특정 카테고리 내에서 현재 게시글의 이전 게시글을 조회.
     * 관리자와 매니저는 모든 게시글이 조회됨.
     * 일반 사용자는 '노출' 상태인 게시글만 조회됨.
     *
     * @param categoryId 조회할 게시글이 속한 카테고리의 ID.
     * @param articleId 현재 게시글의 ID.
     * @param principalDetails 현재 인증된 사용자의 정보.
     * @return 조회된 이전 게시글의 정보 반환합. 이전 게시글이 없는 경우 예외 발생.
     * @throws RestControllerException 이전 게시글이 없을 때 발생하는 예외.
     */
    public Articles getPreviousArticleInCategory(Long categoryId, Long articleId, PrincipalDetails principalDetails) {
        String userRole = principalDetails.getUser().getRole();
        List<Articles> articlesInCategory;

        // 관리자 또는 매니저인 경우 "노출" 상태와 상관없이 모든 게시물 조회
        if ("ROLE_ADMIN".equals(userRole) || "ROLE_MANAGER".equals(userRole)) {
            articlesInCategory = articleRepository.findByCategoryAndIdLessThanOrderByIdDesc(categoryId, articleId);
        } else {
            // 그 외 사용자는 "노출" 상태인 게시물만 조회
            articlesInCategory = articleRepository.findByCategoryAndIdLessThanAndViewStatusOrderByIdDesc(categoryId, articleId, "노출");
        }

        if (articlesInCategory.isEmpty()) {
            throw new RestControllerException(ExceptionCode.NO_PREVIOUS_ARTICLE);
        }
        return articlesInCategory.get(0);
    }


    public Articles getNextArticleInCategory(Long categoryId, Long articleId, PrincipalDetails principalDetails) {
        String userRole = principalDetails.getUser().getRole();
        List<Articles> articlesInCategory;

        // 관리자 또는 매니저인 경우 "노출" 상태와 상관없이 모든 게시물 조회
        if ("ROLE_ADMIN".equals(userRole) || "ROLE_MANAGER".equals(userRole)) {
            articlesInCategory = articleRepository.findByCategoryAndIdGreaterThanOrderByIdAsc(categoryId, articleId);
        } else {
            // 그 외 사용자는 "노출" 상태인 게시물만 조회
            articlesInCategory = articleRepository.findByCategoryAndIdGreaterThanAndViewStatusOrderByIdAsc(categoryId, articleId, "노출");
        }

        if (articlesInCategory.isEmpty()) {
            throw new RestControllerException(ExceptionCode.NO_NEXT_ARTICLE);
        }
        return articlesInCategory.get(0);
    }

    /**
     * 특정 카테고리 내 이전 게시물의 상세 정보 가져오기.
     * @param categoryId 카테고리 ID
     * @param articleId 현재 게시물 ID
     * @param principalDetails 사용자 인증 정보
     * @return 이전 게시물의 상세 정보
     */
    public ArticlesResponseDTO getPreviousArticleDetails(Long categoryId, Long articleId, PrincipalDetails principalDetails) {
        Articles previousArticle = getPreviousArticleInCategory(categoryId, articleId, principalDetails);
        previousArticle.setReadCount(previousArticle.getReadCount() + 1);
        articleRepository.save(previousArticle);
        return articleResponseMapper.toDto(previousArticle);
    }

    /**
     * 특정 카테고리 내 다음 게시물의 상세 정보 가져오기.
     * @param categoryId 카테고리 ID
     * @param articleId 현재 게시물 ID
     * @param principalDetails 사용자 인증 정보
     * @return 다음 게시물의 상세 정보
     */
    public ArticlesResponseDTO getNextArticleDetails(Long categoryId, Long articleId, PrincipalDetails principalDetails) {
        Articles nextArticle = getNextArticleInCategory(categoryId, articleId, principalDetails);
        nextArticle.setReadCount(nextArticle.getReadCount() + 1);
        articleRepository.save(nextArticle);
        return articleResponseMapper.toDto(nextArticle);
    }

    /**
     * 로그인 문의 게시물을 생성. 로그인 문의는 특정 카테고리에만 생성.
     * @param articlesLoginInquiryDTO 로그인 문의 데이터
     * @return 생성된 게시물의 상세 정보
     */
    public ArticlesResponseDTO createLoginInquiryArticle(ArticlesLoginInquiryDTO articlesLoginInquiryDTO, HttpServletRequest request) {
        Category category = categoryRepository.findByName("고객센터")
                .orElseThrow(() -> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND));

        String clientIp = request.getRemoteAddr();

        Articles article = new Articles();
        article.setTitle("로그인 문의 - " + articlesLoginInquiryDTO.getUsername());
        article.setContent("비밀번호 초기화 요청. 예금주: " + articlesLoginInquiryDTO.getOwnerName() + ", 전화번호: " + articlesLoginInquiryDTO.getPhone());
        article.setWriterName(articlesLoginInquiryDTO.getUsername());
        article.setOwnerName(articlesLoginInquiryDTO.getOwnerName());
        article.setPhone(articlesLoginInquiryDTO.getPhone());
        article.setAnswerStatus("로그인문의 답변요청");
        article.setViewStatus("노출");
        article.setMemo("");
        article.setIsTop(false);
        article.setCategory(category);
        article.setCreatedAt(LocalDateTime.now());
        article.setIp(clientIp);
        Articles savedArticle = articleRepository.save(article);

        return articleResponseMapper.toDto(savedArticle);
    }

    /**
     * 게시물의 메모를 업데이트
     * @param articlesLoginInquiryDTO 업데이트할 메모 데이터
     * @param principalDetails 인증된 사용자 정보
     * @return 업데이트된 게시물의 상세 정보
     */
    public ArticlesResponseDTO updateMemo(ArticlesLoginInquiryDTO articlesLoginInquiryDTO, PrincipalDetails principalDetails) {
        Articles article = articleRepository.findById(articlesLoginInquiryDTO.getId())
                .orElseThrow(() -> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND));

        article.setMemo(articlesLoginInquiryDTO.getMemo());
        Articles updatedArticle = articleRepository.save(article);

        return articleResponseMapper.toDto(updatedArticle);
    }

    /**
     * 로그인 문의 게시물의 답변 상태를 조건에 따라 업데이트.
     * @param articleId 게시물 ID
     * @return 업데이트된 게시물의 상세 정보
     */
    public ArticlesResponseDTO updateLoginInquiryStatus(Long articleId) {
        Articles article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND, "게시물을 찾을 수 없습니다."));

        // 현재 상태에 따른 새 상태 설정
        switch (article.getAnswerStatus()) {
            case "답변대기":
            case "답변요청":
                article.setAnswerStatus("답변완료");
                break;
            case "로그인문의 답변대기":
            case "로그인문의 답변요청":
                article.setAnswerStatus("로그인문의 답변완료");
                break;
        }

        Articles updatedArticle = articleRepository.save(article);
        return articleResponseMapper.toDto(updatedArticle);
    }

    /**
     * 특정 카테고리 ID에 속하는 로그인 문의 목록을 조회.
     *
     * @param categoryId 로그인 문의를 조회하고자 하는 카테고리의 ID
     * @param writerName 작성자 이름 (옵션)
     * @param ownerName 소유자 이름 (옵션)
     * @param phone 전화번호 (옵션)
     * @param ip IP 주소 (옵션)
     * @param startDate 시작 날짜 (옵션)
     * @param endDate 종료 날짜 (옵션)
     * @param principalDetails 인증된 사용자 정보
     * @return 조회된 로그인 문의 목록
     */
    public List<LoginInquiryListDTO> searchLoginInquiries(Long categoryId, String writerName, String ownerName, String phone, String ip,
                                                          LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        return articleRepository.findAll((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
                    predicates.add(criteriaBuilder.like(root.get("title"), "로그인 문의%"));

                    if (writerName != null && !writerName.isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("writerName"), writerName));
                    }
                    if (ownerName != null && !ownerName.isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("ownerName"), ownerName));
                    }
                    if (phone != null && !phone.isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("phone"), phone));
                    }
                    if (ip != null && !ip.isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("ip"), ip));
                    }
                    if (startDate != null && endDate != null) {
                        predicates.add(criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
                    }

                    query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }).stream()
                .map(article -> new LoginInquiryListDTO(article.getId(), article.getWriterName(), article.getOwnerName(), article.getPhone(),
                        article.getIp(), article.getMemo(), article.getAnswerStatus(), article.getSite(), article.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * 지정된 ID의 게시글의 노출 상태를 업데이트.
     *
     * @param articleId 노출 상태를 업데이트할 게시글의 ID.
     * @param viewStatus 게시글에 설정할 새로운 노출 상태.
     * @return 업데이트된 게시글 객체를 반환.
     */
    public Articles updateViewStatus(Long articleId, String viewStatus, PrincipalDetails principalDetails) {
        Articles article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        article.setViewStatus(viewStatus);
        articleRepository.save(article);

        return articleRepository.save(article);
    }

    /**
     * 특정 게시물의 댓글 허용 여부를 업데이트.
     *
     * @param articleId 게시물 ID
     * @param commentAllowed 새로운 댓글 허용 여부
     */
    public void updateCommentAllowed(Long articleId, boolean commentAllowed, PrincipalDetails principalDetails) {
        Articles article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "게시물을 찾을 수 없습니다."));
        article.setCommentAllowed(commentAllowed);
        articleRepository.save(article);
    }

    /**
     * "<>" 사이의 내용을 제거하는 메서드.
     * @param content 처리할 내용
     * @return 처리된 내용
     */
    private String removeContentInBrackets(String content) {
        return content.replaceAll("<[^>]*>", ""); // "<>" 사이의 내용을 제거
    }
}
