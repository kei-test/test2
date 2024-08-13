package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonCategoryReqDTO;
import GInternational.server.api.dto.AmazonCategoryResDTO;
import GInternational.server.api.entity.AmazonCategory;
import GInternational.server.api.mapper.AmazonCategoryReqMapper;
import GInternational.server.api.mapper.AmazonCategoryResMapper;
import GInternational.server.api.repository.AmazonCategoryRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonCategoryService {

    private final AmazonCategoryRepository amazonCategoryRepository;
    private final AmazonCategoryReqMapper amazonCategoryReqMapper;
    private final AmazonCategoryResMapper amazonCategoryResMapper;

    /**
     * 카테고리 생성.
     *
     * @param amazonCategoryReqDTO 추가할 카테고리 정보를 담은 DTO
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     * @return 생성된 카테고리에 대한 응답 DTO
     */
    public AmazonCategoryResDTO insertCategory(AmazonCategoryReqDTO amazonCategoryReqDTO, PrincipalDetails principalDetails) {
        AmazonCategory amazonCategory = amazonCategoryReqMapper.toEntity(amazonCategoryReqDTO);
        AmazonCategory savedCategory = amazonCategoryRepository.save(amazonCategory);
        return amazonCategoryResMapper.toDto(savedCategory);
    }

    /**
     * 카테고리 업데이트.
     *
     * @param id 업데이트할 카테고리의 ID
     * @param amazonCategoryReqDTO 업데이트할 카테고리 정보를 담은 DTO
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     * @return 업데이트된 카테고리에 대한 응답 DTO
     * @throws RestControllerException 카테고리를 찾을 수 없는 경우 예외 발생
     */
    public AmazonCategoryResDTO updateCategory(Long id,AmazonCategoryReqDTO amazonCategoryReqDTO, PrincipalDetails principalDetails) {
        AmazonCategory amazonCategory = amazonCategoryRepository.findById(id).orElseThrow
                (()-> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND, "카테고리를 찾을 수 없습니다."));
        amazonCategoryReqMapper.toEntity(amazonCategoryReqDTO);
        Optional.ofNullable(amazonCategoryReqDTO.getName()).ifPresent(amazonCategory::setName);
        Optional.ofNullable(amazonCategoryReqDTO.getCategoryRole()).ifPresent(amazonCategory::setCategoryRole);
        amazonCategory.setUpdatedAt(LocalDateTime.now());
        AmazonCategory savedCategory = amazonCategoryRepository.save(amazonCategory);
        return amazonCategoryResMapper.toDto(savedCategory);
    }

    /**
     * 카테고리 조회.
     *
     * @param page 요청된 페이지 번호
     * @param size 페이지 당 표시할 아이템 수
     * @return 조회된 카테고리의 페이지 객체
     */
    public Page<AmazonCategory> allCategory(int page, int size) {
        return amazonCategoryRepository.findAll(PageRequest.of(page,size, Sort.by("id").descending()));
    }

    /**
     * 카테고리 삭제.
     *
     * @param id 삭제할 카테고리의 ID
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     * @throws RestControllerException 카테고리를 찾을 수 없는 경우 예외 발생
     */
    public void deleteCategory(Long id,PrincipalDetails principalDetails) {
        AmazonCategory category = amazonCategoryRepository.findById(id).orElseThrow(()-> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND));
        amazonCategoryRepository.delete(category);
    }
}
