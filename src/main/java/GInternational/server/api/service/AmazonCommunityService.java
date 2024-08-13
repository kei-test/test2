package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonCommunityReqDTO;
import GInternational.server.api.dto.AmazonCommunityResDTO;
import GInternational.server.api.entity.AmazonCategory;
import GInternational.server.api.entity.AmazonCommunity;
import GInternational.server.api.mapper.AmazonCommunityReqMapper;
import GInternational.server.api.mapper.AmazonCommunityResMapper;
import GInternational.server.api.repository.AmazonCategoryRepository;
import GInternational.server.api.repository.AmazonCommunityRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonCommunityService {


    private final AmazonCommunityRepository amazonCommunityRepository;
    private final UserService userService;
    private final AmazonCategoryRepository amazonCategoryRepository;
    private final AmazonCommunityReqMapper amazonCommunityReqMapper;




    /**
     * 공지 글쓰기 v
     * 공지 글 수정 v
     * 공지 글 조회 v
     * 공지 글 삭제 v\
     * 문의 글 작성
     * 문의 글 조회
     * 문의 글 수정
     * 문의 글의 답글
     * 문의 글 삭제 시 답변 글 같이 삭제
     */


    /**
     * 공지사항 또는 질문 게시글을 생성.
     *
     * @param amazonCategoryId 카테고리 ID
     * @param amazonCommunityReqDTO 게시글 정보
     * @param principalDetails 인증된 사용자 정보
     * @return 생성된 게시글의 응답 DTO
     */
    public AmazonCommunityResDTO insertNotice(Long amazonCategoryId, AmazonCommunityReqDTO amazonCommunityReqDTO, PrincipalDetails principalDetails) {
        User user = userService.validateUser(principalDetails.getUser().getId());
        AmazonCategory amazonCategory = amazonCategoryRepository.findById(amazonCategoryId).orElseThrow(() -> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND));

        if (amazonCategory.getCategoryRole().equals("ROLE_ADMIN") || amazonCategory.getCategoryRole().equals("ROLE_MANAGER")) {
            if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) {
                AmazonCommunity amazonCommunity = amazonCommunityReqMapper.toEntity(amazonCommunityReqDTO);
                amazonCommunity.setIsTop(amazonCommunityReqDTO.getIsTop());
                amazonCommunity.setNickname(user.getNickname());
                amazonCommunity.setWriter(user);
                amazonCommunity.setAmazonCategory(amazonCategory);
                amazonCommunity.setType("유저");
                amazonCommunity.setStatus("미확인");
                amazonCommunity.setDescription("질문");
                amazonCommunity.setCreatedAt(LocalDateTime.now());
                AmazonCommunity savedAmazonCommunity = amazonCommunityRepository.save(amazonCommunity);
                return new AmazonCommunityResDTO(savedAmazonCommunity);
            }
        } else if (amazonCategory.getCategoryRole().equals("ROLE_USER")) {
            if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER") || user.getRole().equals("ROLE_USER")) {
                AmazonCommunity amazonCommunity = amazonCommunityReqMapper.toEntity(amazonCommunityReqDTO);
                amazonCommunity.setIsTop(amazonCommunityReqDTO.getIsTop());
                amazonCommunity.setNickname(user.getNickname());
                amazonCommunity.setWriter(user);
                amazonCommunity.setAmazonCategory(amazonCategory);
                amazonCommunity.setType("유저");
                amazonCommunity.setStatus("미확인");
                amazonCommunity.setDescription("질문");
                amazonCommunity.setCreatedAt(LocalDateTime.now());
                AmazonCommunity savedAmazonCommunity = amazonCommunityRepository.save(amazonCommunity);
                return new AmazonCommunityResDTO(savedAmazonCommunity);
            }
        }
        return null;
    }

    /**
     * 공지사항 또는 질문 게시글을 수정.
     *
     * @param amazonCategoryId 카테고리 ID
     * @param communityId 게시글 ID
     * @param amazonCommunityReqDTO 수정할 게시글 정보
     * @param principalDetails 인증된 사용자 정보
     * @return 수정된 게시글의 응답 DTO
     */
    public AmazonCommunityResDTO updateNotice(Long amazonCategoryId, Long communityId, AmazonCommunityReqDTO amazonCommunityReqDTO, PrincipalDetails principalDetails) {
        AmazonCategory category = amazonCategoryRepository.findById(amazonCategoryId).orElseThrow(() -> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND));
        AmazonCommunity amazonCommunity = amazonCommunityRepository.findById(communityId).orElseThrow(() -> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND));
        User user = userService.validateUser(principalDetails.getUser().getId());

        if (category.getCategoryRole().equals("ROLE_ADMIN") || category.getCategoryRole().equals("ROLE_MANAGER")) {
            if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) {
                amazonCommunityReqMapper.toEntity(amazonCommunityReqDTO);
                Optional.ofNullable(amazonCommunityReqDTO.getTitle()).ifPresent(amazonCommunity::setTitle);
                Optional.ofNullable(amazonCommunityReqDTO.getContent()).ifPresent(amazonCommunity::setContent);
                amazonCommunity.setIsTop(amazonCommunityReqDTO.getIsTop());
                amazonCommunity.setUpdatedAt(LocalDateTime.now());
                AmazonCommunity savedAmazonCommunity = amazonCommunityRepository.save(amazonCommunity);
                return new AmazonCommunityResDTO(savedAmazonCommunity);
            }
        } else if (category.getCategoryRole().equals("ROLE_USER")) {
            if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER") || user.getRole().equals("ROLE_USER")) {
                amazonCommunityReqMapper.toEntity(amazonCommunityReqDTO);
                Optional.ofNullable(amazonCommunityReqDTO.getTitle()).ifPresent(amazonCommunity::setTitle);
                Optional.ofNullable(amazonCommunityReqDTO.getContent()).ifPresent(amazonCommunity::setContent);
                amazonCommunity.setIsTop(amazonCommunityReqDTO.getIsTop());
                amazonCommunity.setUpdatedAt(LocalDateTime.now());
                AmazonCommunity savedAmazonCommunity = amazonCommunityRepository.save(amazonCommunity);
                return new AmazonCommunityResDTO(savedAmazonCommunity);
            }
        }
        return null;
    }

    /**
     * 특정 공지사항 또는 질문 게시글을 조회.
     *
     * @param communityId 게시글 ID
     * @param principalDetails 인증된 사용자 정보
     * @return 조회된 게시글 정보
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public AmazonCommunity detailCommunity(Long communityId, PrincipalDetails principalDetails) {
        AmazonCommunity amazonCommunity = validateCommunity(communityId);
        return amazonCommunity;
    }

    /**
     * 특정 카테고리에 속하는 게시글을 기간별로 조회.
     *
     * @param amazonCategoryId 카테고리 ID
     * @param startDateTime 조회 시작 날짜
     * @param endDateTime 조회 종료 날짜
     * @param principalDetails 인증된 사용자 정보
     * @return 조회된 게시글 목록의 응답 DTO 리스트
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public List<AmazonCommunityResDTO> getCommunityByCategory(Long amazonCategoryId, LocalDateTime startDateTime, LocalDateTime endDateTime, PrincipalDetails principalDetails) {
        List<AmazonCommunity> communities = amazonCommunityRepository.findByAmazonCategoryIdAndCreatedAtBetween(amazonCategoryId, startDateTime, endDateTime);
        return communities.stream()
                .map(AmazonCommunityResDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 공지사항 또는 질문 게시글을 삭제. 관리자 또는 매니저만 삭제 가능.
     *
     * @param amazonCategoryId 카테고리 ID
     * @param principalDetails 인증된 사용자 정보
     */
    public void deleteCommunity(Long amazonCategoryId,PrincipalDetails principalDetails) {
        User user = userService.validateUser(principalDetails.getUser().getId());
        if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) {
            AmazonCommunity amazonCommunity = amazonCommunityRepository.findById(amazonCategoryId).orElseThrow
                    (() -> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND));
            amazonCommunityRepository.delete(amazonCommunity);
        }
    }

    /**
     * 게시글 ID에 해당하는 게시글의 유효성을 검증.
     *
     * @param communityId 게시글 ID
     * @return 유효한 게시글 정보
     * @throws RestControllerException 게시글을 찾을 수 없는 경우 예외 발생
     */
    public AmazonCommunity validateCommunity(Long communityId) {
        Optional<AmazonCommunity> optionalCommunity = amazonCommunityRepository.findById(communityId);
        AmazonCommunity findAmazonCommunity = optionalCommunity.orElseThrow(() -> new RestControllerException(ExceptionCode.ARTICLE_NOT_FOUND));
        return findAmazonCommunity;
    }
}
