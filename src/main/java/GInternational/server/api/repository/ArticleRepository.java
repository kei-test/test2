package GInternational.server.api.repository;

import GInternational.server.api.dto.LoginInquiryListDTO;
import GInternational.server.api.entity.Articles;
import GInternational.server.api.entity.Category;
import GInternational.server.api.entity.LoginHistory;
import GInternational.server.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Articles, Long>, JpaSpecificationExecutor<Articles>, ArticleRepositoryCustom{

    Optional<Articles> findById(Long ArticleId);

    Optional<Articles> findByIdAndViewStatus(Long articleId, String viewStatus);

    // 관리자와 매니저용: viewStatus 상관없이 모든 게시물 조회
    List<Articles> findByIsTopTrueOrderByCreatedAtDesc();

    // 일반 사용자용: viewStatus가 "노출"인 게시물만 조회
    @Query("SELECT a FROM articles a WHERE a.isTop = true AND a.viewStatus = :viewStatus ORDER BY a.createdAt DESC")
    List<Articles> findByIsTopTrueAndViewStatusOrderByCreatedAtDesc(@Param("viewStatus") String viewStatus);

    @Query("SELECT a FROM articles a WHERE a.category.id = :categoryId AND a.id > :articleId ORDER BY a.id ASC")
    List<Articles> findByCategoryAndIdGreaterThanOrderByIdAsc(@Param("categoryId") Long categoryId, @Param("articleId") Long articleId);

    @Query("SELECT a FROM articles a WHERE a.category.id = :categoryId AND a.id < :articleId ORDER BY a.id DESC")
    List<Articles> findByCategoryAndIdLessThanOrderByIdDesc(@Param("categoryId") Long categoryId, @Param("articleId") Long articleId);

    @Query("SELECT a FROM articles a WHERE a.category.id = :categoryId AND a.id > :articleId AND a.viewStatus = :viewStatus ORDER BY a.id ASC")
    List<Articles> findByCategoryAndIdGreaterThanAndViewStatusOrderByIdAsc(@Param("categoryId") Long categoryId, @Param("articleId") Long articleId, String viewStatus);

    @Query("SELECT a FROM articles a WHERE a.category.id = :categoryId AND a.id < :articleId AND a.viewStatus = :viewStatus ORDER BY a.id DESC")
    List<Articles> findByCategoryAndIdLessThanAndViewStatusOrderByIdDesc(@Param("categoryId") Long categoryId, @Param("articleId") Long articleId, String viewStatus);

    @Query("SELECT a FROM articles a JOIN a.writer u WHERE a.category.id = :categoryId AND FUNCTION('MONTH', a.createdAt) = :month AND FUNCTION('YEAR', a.createdAt) = :year AND u.role = 'ROLE_USER'")
    List<Articles> findByCategoryIdAndMonthAndYearAndUserRole(@Param("categoryId") Long categoryId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(a) FROM articles a WHERE a.answerStatus IN (?1)")
    Long countByAnswerStatuses(List<String> statuses);

    int countByAnswerStatus(String answerStatus);

    int countByWriterAndCategoryAndCreatedAtBetween(User writer, Category category, LocalDateTime startDate, LocalDateTime endDate);
}
