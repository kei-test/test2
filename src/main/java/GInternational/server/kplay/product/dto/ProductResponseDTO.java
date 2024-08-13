package GInternational.server.kplay.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductResponseDTO {

    private int status;
    private List<ProductData> data;


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ProductData {
        private int prd_id;
        private String prd_name;
    }
}

