package GInternational.server.api.repository;

import GInternational.server.api.dto.CommentResDTO;
import GInternational.server.api.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

import static GInternational.server.api.entity.QArticles.articles;
import static GInternational.server.api.entity.QComment.comment;
import static GInternational.server.api.entity.QEventsBoard.eventsBoard;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentResDTO> findByArticleParentAndChild(Long id, Pageable pageable) {
        List<Comment> comments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.articles.id.eq(id).or(comment.parent.id.eq(id)))
                .orderBy(comment.parent.id.desc().nullsFirst(), comment.createdAt.desc())
                .fetch();

        return paginateComments(comments, pageable);
    }

    @Override
    public Page<CommentResDTO> findByEventsBoardParentAndChild(Long id, Pageable pageable) {
        List<Comment> comments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.eventsBoard.id.eq(id).or(comment.parent.id.eq(id)))
                .orderBy(comment.parent.id.desc().nullsFirst(), comment.createdAt.desc())
                .fetch();

        return paginateComments(comments, pageable);
    }

    @Override
    public Optional<Comment> findArticleCommentByIdWithParent(Long id) {
        Comment selectedComment = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .leftJoin(comment.articles, articles).fetchJoin()
                .leftJoin(comment.eventsBoard, eventsBoard).fetchJoin()
                .where(comment.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(selectedComment);
    }

    @Override
    public Optional<Comment> findEventsBoardCommentByIdWithParent(Long id) {
        Comment selectedComment = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .join(comment.eventsBoard, eventsBoard).fetchJoin()
                .where(comment.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(selectedComment);
    }

    @Override
    public Long getTotalCommentCountByArticleId(Long articlesId) {
        return queryFactory.select(comment.id.count())
                .from(comment)
                .where(comment.articles.id.eq(articlesId))
                .fetchOne();
    }

    @Override
    public Long getTotalCommentCountByEventsBoardId(Long eventsBoardId) {
        return queryFactory.select(comment.id.count())
                .from(comment)
                .where(comment.eventsBoard.id.eq(eventsBoardId))
                .fetchOne();
    }

    private Page<CommentResDTO> paginateComments(List<Comment> comments, Pageable pageable) {
        int totalComments = comments.size();

        // 페이징 처리
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalComments);

        // 유효한 범위 내에 있는지 확인
        if (startIndex > endIndex || startIndex > totalComments) {
            // 유효한 범위 내에 댓글이 없음
            return new PageImpl<>(Collections.emptyList(), pageable, totalComments);
        }

        List<Comment> paginatedComments = comments.subList(startIndex, endIndex);

        // Comment 객체를 CommentResponseDto 객체로 변환
        List<CommentResDTO> commentResponse = new ArrayList<>();
        Map<Long, CommentResDTO> commentDtoHashMap = new HashMap<>();

        for (Comment c : paginatedComments) {
            CommentResDTO commentResponseDto = CommentResDTO.convertCommentToDto(c);
            commentDtoHashMap.put(commentResponseDto.getId(), commentResponseDto);

            Comment parent = c.getParent();
            if (parent != null) {
                CommentResDTO parentDto = commentDtoHashMap.get(parent.getId());
                if (parentDto != null) {
                    parentDto.getChildren().add(commentResponseDto);
                }
            } else {
                commentResponse.add(commentResponseDto);
            }
        }

        return new PageImpl<>(commentResponse, pageable, totalComments);
    }
}
