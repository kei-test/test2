package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppleSettingUpdateDTO {

    @NotNull(message = "순번은 비어 있을 수 없습니다.")
    private Long id;

    @NotBlank(message = "룰렛명은 비어 있을 수 없습니다.")
    private String rewardName; // 룰렛명

    @NotBlank(message = "상품값은 비어 있을 수 없습니다.")
    private String rewardValue; // 상품 값

    @NotBlank(message = "상품 세부명은 비어 있을 수 없습니다.")
    private String rewardDescription; // 상품 세부명

    @Min(value = 0, message = "최대 지급 개수는 0 이상 이어야 합니다.")
    private long maxQuantity; // 최대 지급 개수

    @DecimalMin(value = "0.0", message = "확률은 0.0 이상 이어야 합니다.")
    @DecimalMax(value = "100.0", message = "확률은 100.0 이하 이어야 합니다.")
    private double probability; // 확률

    private LocalDateTime lastModifiedDate;
}