package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonCategoryReqDTO;
import GInternational.server.api.dto.AmazonCategoryResDTO;
import GInternational.server.api.entity.AmazonCategory;
import GInternational.server.api.mapper.AmazonCategoryResMapper;
import GInternational.server.api.service.AmazonCategoryService;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class AmazonCategoryController {

    private final AmazonCategoryService amazonCategoryService;
    private final AmazonCategoryResMapper amazonCategoryResMapper;

    /**
     * Amazon 카테고리 추가.
     *
     * @param amazonCategoryReqDTO 추가할 카테고리 정보를 담은 요청 DTO
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return 생성된 카테고리에 대한 응답 DTO와 함께 HTTP 상태 CREATED 반환
     */
    @PostMapping("/managers/amazon-category")
    public ResponseEntity insertCategory(@Valid @RequestBody AmazonCategoryReqDTO amazonCategoryReqDTO,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonCategoryResDTO response = amazonCategoryService.insertCategory(amazonCategoryReqDTO,principal);
        return new ResponseEntity(new SingleResponseDto<>(response), HttpStatus.CREATED);
    }

    /**
     * 특정 Amazon 카테고리의 정보 업데이트.
     *
     * @param amazonCategoryId 수정할 카테고리의 ID
     * @param amazonCategoryReqDTO 수정될 카테고리 정보를 담은 요청 DTO
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return 수정된 카테고리에 대한 응답 DTO와 함께 HTTP 상태 OK 반환
     */
    @PatchMapping("/managers/amazon-category/{amazonCategoryId}")
    public ResponseEntity updateCategory(@PathVariable("amazonCategoryId") @Positive Long amazonCategoryId,
                                         @RequestBody AmazonCategoryReqDTO amazonCategoryReqDTO,
                                         Authentication authentication) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            AmazonCategoryResDTO response = amazonCategoryService.updateCategory(amazonCategoryId, amazonCategoryReqDTO, principal);
            return ResponseEntity.ok(new SingleResponseDto<>(response));
        } catch (RestControllerException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "카테고리 업데이트 중 오류가 발생했습니다.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Amazon 카테고리 목록을 페이지네이션으로 조회.
     *
     * @param page 요청된 페이지 번호
     * @param size 페이지 당 표시할 아이템 수
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return 조회된 카테고리 목록과 페이징 정보를 포함한 응답과 함께 HTTP 상태 OK 반환
     */
    @GetMapping("/managers/amazon-categories")
    public ResponseEntity getCategories(@Positive @RequestParam  int page,
                                        @Positive @RequestParam  int size,
                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AmazonCategory> pages = amazonCategoryService.allCategory(page -1, size);
        List<AmazonCategory> response = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(amazonCategoryResMapper.toDto(response),pages),HttpStatus.OK);
    }

    /**
     * 특정 Amazon 카테고리를 삭제.
     *
     * @param amazonCategoryId 삭제할 카테고리의 ID
     * @param authentication 현재 인증된 사용자의 정보를 포함하는 객체
     * @return HTTP 상태 NO_CONTENT 반환
     */
    @DeleteMapping("/managers/amazon-category/{amazonCategoryId}")
    public ResponseEntity deleteCategory(@PathVariable("amazonCategoryId") @Positive Long amazonCategoryId,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonCategoryService.deleteCategory(amazonCategoryId,principal);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
