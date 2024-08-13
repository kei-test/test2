package GInternational.server.api.controller;

import GInternational.server.api.dto.CategoryRequestDTO;
import GInternational.server.api.dto.CategoryResponseDTO;
import GInternational.server.api.entity.Category;
import GInternational.server.api.mapper.CategoryResponseMapper;
import GInternational.server.api.service.CategoryService;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryResponseMapper categoryResponseMapper;

    /**
     * 카테고리 생성.
     * @param categoryRequestDTO 카테고리 정보
     * @param authentication 사용자 인증 정보
     * @return 생성 메시지 포함된 응답
     */
    @PostMapping("/managers/category")
    public ResponseEntity insertCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        CategoryResponseDTO response = categoryService.insertCategory(categoryRequestDTO, principal);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "카테고리가 생성되었습니다.");
        return new ResponseEntity(responseBody, HttpStatus.CREATED);
    }

    /**
     * 카테고리 수정.
     * @param categoryId 수정할 카테고리 ID
     * @param categoryRequestDTO 수정 정보
     * @param authentication 사용자 인증 정보
     * @return 수정 메시지 포함된 응답
     */
    @PatchMapping("/managers/category/{categoryId}")
    public ResponseEntity updateCategory(@PathVariable("categoryId") @Positive Long categoryId,
                                         @RequestBody CategoryRequestDTO categoryRequestDTO,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        CategoryResponseDTO response = categoryService.updateCategory(categoryId, categoryRequestDTO, principal);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "카테고리가 수정되었습니다.");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 카테고리 목록 조회.
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param authentication 사용자 인증 정보
     * @return 카테고리 목록
     */
    @GetMapping("/managers/categories")
    public ResponseEntity getCategories(@Positive @RequestParam  int page,
                                        @Positive @RequestParam  int size,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<Category> pages = categoryService.allCategory(page -1, size, principal);
        List<Category> response = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(categoryResponseMapper.toDto(response),pages),HttpStatus.OK);
    }

    /**
     * 카테고리 삭제.
     * @param categoryId 삭제할 카테고리 ID
     * @param authentication 사용자 인증 정보
     * @return 삭제 메시지 포함된 응답
     */
    @DeleteMapping("/managers/category/{categoryId}")
    public ResponseEntity deleteCategory(@PathVariable("categoryId") @Positive Long categoryId,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        categoryService.deleteCategory(categoryId, principal);
        return ResponseEntity.ok("카테고리가 삭제되었습니다.");
    }
}
