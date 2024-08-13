package GInternational.server.kplay.product.controller;

import GInternational.server.kplay.product.dto.ProductRequestDTO;
import GInternational.server.kplay.product.dto.ProductResponseDTO;
import GInternational.server.kplay.product.entity.Product;
import GInternational.server.kplay.product.mapper.ProductResponseMapper;
import GInternational.server.kplay.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductResponseMapper productResponseMapper;

    /**
     * 제품 정보를 데이터베이스에 저장하고 결과를 반환.
     *
     * @param data 제품 정보 목록
     * @return ResponseEntity 저장 결과를 담은 ProductResponseDTO
     */
    @PostMapping("/productlist")
    public ResponseEntity insertProduct(@RequestBody List<ProductRequestDTO> data) {
        ProductResponseDTO response = productService.insertProducts(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 지정된 인덱스 범위 내의 제품 정보를 조회.
     *
     * @param startIndex 시작 인덱스
     * @param endIndex 종료 인덱스
     * @return ResponseEntity 조회된 제품 정보 목록
     */
    @GetMapping("/prd")
    public ResponseEntity getPrd(@RequestParam int startIndex,
                                 @RequestParam int endIndex) {
        List<Product> response = productService.searchByPrd(startIndex, endIndex);
        return new ResponseEntity<>(productResponseMapper.toDto(response), HttpStatus.OK);
    }

    /**
     * 제품의 활성화 상태를 변경.
     *
     * @param productId 제품 ID
     * @param isOn 활성화 상태. true로 설정하면 활성화, false로 설정하면 비활성화.
     * @return ResponseEntity 처리 상태를 담은 ProductResponseDTO
     */
    @PatchMapping("/product/{productId}/status")
    public ResponseEntity setProductStatus(@PathVariable Long productId,
                                           @RequestParam boolean isOn) {
        List<Product> response = productService.setProductStatus(productId, isOn);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
