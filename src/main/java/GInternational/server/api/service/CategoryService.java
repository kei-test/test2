package GInternational.server.api.service;

import GInternational.server.api.dto.CategoryRequestDTO;
import GInternational.server.api.dto.CategoryResponseDTO;
import GInternational.server.api.entity.Category;
import GInternational.server.api.mapper.CategoryRequestMapper;
import GInternational.server.api.mapper.CategoryResponseMapper;
import GInternational.server.api.repository.CategoryRepository;
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
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryRequestMapper categoryRequestMapper;
    private final CategoryResponseMapper categoryResponseMapper;

    /**
     * 카테고리 생성.
     * @param categoryRequestDTO 카테고리 정보
     * @param principalDetails 사용자 인증 정보
     * @return 생성된 카테고리 정보
     */
    public CategoryResponseDTO insertCategory(CategoryRequestDTO categoryRequestDTO, PrincipalDetails principalDetails) {
        //관리자 조회 후 등록한 매니저 조회
        Category category = categoryRequestMapper.toEntity(categoryRequestDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryResponseMapper.toDto(savedCategory);
    }

    /**
     * 카테고리 수정.
     * @param categoryId 수정할 카테고리 ID
     * @param categoryRequestDTO 수정 정보
     * @param principalDetails 사용자 인증 정보
     * @return 수정된 카테고리 정보
     */
    public CategoryResponseDTO updateCategory(Long categoryId,CategoryRequestDTO categoryRequestDTO, PrincipalDetails principalDetails) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND));
        categoryRequestMapper.toEntity(categoryRequestDTO);
        Optional.ofNullable(categoryRequestDTO.getName()).ifPresent(category::setName);
        Optional.ofNullable(categoryRequestDTO.getCategoryRole()).ifPresent(category::setCategoryRole);
        category.setUpdatedAt(LocalDateTime.now());
        Category savedCategory = categoryRepository.save(category);
        return categoryResponseMapper.toDto(savedCategory);
    }

    /**
     * 모든 카테고리 조회.
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param principalDetails 사용자 인증 정보
     * @return 페이지 처리된 카테고리 목록
     */
    public Page<Category> allCategory(int page, int size, PrincipalDetails principalDetails) {
        return categoryRepository.findAll(PageRequest.of(page,size,Sort.by("id").descending()));
    }

    /**
     * 카테고리 삭제.
     * @param categoryId 삭제할 카테고리 ID
     * @param principalDetails 사용자 인증 정보
     */
    public void deleteCategory(Long categoryId, PrincipalDetails principalDetails) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }
}
