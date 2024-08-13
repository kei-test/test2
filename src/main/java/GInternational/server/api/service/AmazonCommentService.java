package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonCommentReqDTO;
import GInternational.server.api.dto.AmazonCommentResDTO;
import GInternational.server.api.entity.AmazonComment;
import GInternational.server.api.entity.AmazonCommunity;
import GInternational.server.api.mapper.AmazonCommentReqMapper;
import GInternational.server.api.repository.AmazonCommentRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonCommentService {

    private final AmazonCommunityService amazonCommunityService;
    private final UserRepository userRepository;
    private final AmazonCommentRepository amazonCommentRepository;
    private final UserService userService;
    private final AmazonCommentReqMapper amazonCommentReqMapper;


    /**
     * Amazon 커뮤니티 게시글에 댓글을 추가. 대댓글 추가도 지원.
     *
     * @param communityId 게시글 ID
     * @param amazonCommentReqDTO 댓글 데이터
     * @param principalDetails 인증된 사용자 정보
     * @return 생성된 댓글의 응답 DTO
     * @throws RuntimeException 유저가 존재하지 않거나, 상위 댓글(Parent)을 찾을 수 없는 경우 예외 발생
     */
    public AmazonCommentResDTO insertAmazonComment(Long communityId, AmazonCommentReqDTO amazonCommentReqDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow(() -> new RuntimeException("유저 없음"));
        AmazonCommunity amazonCommunity = amazonCommunityService.validateCommunity(communityId);
        AmazonComment comment = amazonCommentReqMapper.toEntity(amazonCommentReqDTO);
        AmazonComment parentComment;

        if (amazonCommentReqDTO.getParentId() != null) {
            parentComment = amazonCommentRepository.findByIdAndAmazonCommunity(amazonCommentReqDTO.getParentId(), amazonCommunity).orElseThrow(
                    () -> new RuntimeException("PARENT_NOT_FOUND"));
            comment.updateParent(parentComment);
        }
        comment.setStatus("답변완료");
        comment.setType("유저");
        comment.updateWriter(user);
        comment.setAmazonCommunity(amazonCommunity);
        comment.setCreatedAt(LocalDateTime.now());
        AmazonComment savedComment = amazonCommentRepository.save(comment);
        amazonCommentReqMapper.toDto(savedComment);
        return new AmazonCommentResDTO(savedComment);
    }

    /**
     * Amazon 커뮤니티 게시글의 댓글을 수정. 관리자와 매니저만 수정 가능.
     *
     * @param amazonCommentId 댓글 ID
     * @param updatedCommentReqDTO 수정할 댓글 데이터
     * @param principalDetails 인증된 사용자 정보
     * @return 수정된 댓글의 응답 DTO
     * @throws RuntimeException 유저 없음, 댓글을 찾을 수 없음, 또는 권한이 없는 경우 예외 발생
     */
    public AmazonCommentResDTO updateAmazonComment(Long amazonCommentId, AmazonCommentReqDTO updatedCommentReqDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow
                (() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));
        AmazonComment comment = amazonCommentRepository.findById(amazonCommentId).orElseThrow
                (() -> new RestControllerException(ExceptionCode.COMMENT_NOT_FOUND, "수정할 답변을 찾을 수 없습니다."));

        if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) {
            comment.setTitle(updatedCommentReqDTO.getTitle());
            comment.setContent(updatedCommentReqDTO.getContent());
            AmazonComment updatedComment = amazonCommentRepository.save(comment);
            return new AmazonCommentResDTO(updatedComment);
        }
        throw new RestControllerException(ExceptionCode.UNAUTHORIZED_ACCESS, "관리자만 댓글을 수정할 수 있습니다.");
    }

    /**
     * 특정 Amazon 커뮤니티 게시글의 모든 댓글(대댓글 포함)을 조회.
     *
     * @param communityId 게시글 ID
     * @return 조회된 댓글 목록
     */
    public List<AmazonCommentResDTO> getCommentsByArticlesId(Long communityId, PrincipalDetails principalDetails) {
        return amazonCommentRepository.findByAmazonCommunityParentAndChild(communityId);
    }

    /**
     * Amazon 커뮤니티 게시글의 댓글을 삭제. 댓글이 대댓글을 가지고 있을 경우 삭제 플래그만 변경.
     *
     * @param amazonCommentId 삭제할 댓글 ID
     * @param principalDetails 인증된 사용자 정보
     * @throws RestControllerException 댓글 삭제 권한이 없거나, 댓글 또는 유저 정보를 찾을 수 없는 경우 예외 발생
     */

    public void deleteComment(Long amazonCommentId,PrincipalDetails principalDetails) {
        AmazonComment comment = amazonCommentRepository.findAmazonCommentByIdWithParent(amazonCommentId).orElseThrow(() -> new RuntimeException("COMMENT_NOT_FOUND"));
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow(()-> new RuntimeException("유저 정보 없음"));
        if (comment.getWriter().equals(user) && user.getRole().equals("ROLE_MANAGER") && !user.getRole().equals("ROLE_ADMIN")) {
            throw new RestControllerException(ExceptionCode.UNAUTHORIZED_ACCESS, "댓글 삭제 권한이 없습니다.");
        }

        if (comment.getParent() != null) {
            // 대댓글인 경우 부모 댓글의 자식 목록에서 해당 대댓글을 제거.
            AmazonComment parent = comment.getParent();
            parent.getChildren().remove(comment);
            amazonCommentRepository.save(parent);
        } else {
            // 대댓글이 아닌 경우는 기존의 삭제 로직을 그대로 사용.
            if (comment.getChildren().size() != 0) {
                comment.changeIsDeleted(true);
            } else {
                amazonCommentRepository.delete(getDeletableAncestorComment(comment));
            }
        }
    }

    /**
     * 삭제 대상 댓글이 대댓글인 경우, 삭제해야 할 가장 상위의 댓글을 찾음. 대댓글의 부모가 삭제 상태이고, 자식이 없는 경우 상위 댓글을 삭제.
     *
     * @param comment 현재 검사 중인 댓글
     * @return 삭제해야 하는 가장 상위 댓글
     */
    public AmazonComment getDeletableAncestorComment(AmazonComment comment) {
        AmazonComment parent = comment.getParent(); // 현재 댓글의 부모를 구함
        if (parent != null && parent.getChildren().size() == 1 && parent.isDeleted())
            // 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
            return getDeletableAncestorComment(parent);
        return comment; // 삭제해야하는 댓글 반환
    }

//    @Transactional
//    public List<AmazonCommentResDTO> getMyComments(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다"));
//        List<AmazonComment> commentsList = amazonCommentRepository.findByWriter(user);
//
//        List<AmazonComment> dtoList = commentsList.stream()
//                .map(AmazonCommentResDTO::new)
//                .collect(Collectors.toList();
//        return dtoList;

//    }
}
