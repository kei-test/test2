package GInternational.server.api.repository;

import GInternational.server.api.entity.Articles;
import GInternational.server.security.auth.PrincipalDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepositoryCustom {

    Page<Articles> findByCategoryAndArticles(Long categoryId, Pageable pageable, PrincipalDetails principalDetails);
    Long countByCategoryArticles(Long categoryId);


    Page<Articles> searchByMyArticles(String categoryName, Long userId, Pageable pageable, PrincipalDetails principalDetails);

    Long findMaxId();

    List<Articles> searchByAdvancedCriteria(String title, String content, String nickname, String viewStatus, String categoryName, LocalDateTime startDateTime, LocalDateTime endDateTime, String username);
}
