package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRouletteSettingDTO {

    @NotBlank(message = "순번은 비어 잇을 수 없습니다.")
    private Long id; // 순번

    @NotBlank(message = "룰렛명은 비어 있을 수 없습니다.")
    private String rouletteName; // 룰렛명

    @NotBlank(message = "상품값은 비어 있을 수 없습니다.")
    private String rewardValue; // 상품 값

    @NotBlank(message = "상품 세부명은 비어 있을 수 없습니다.")
    private String rewardDescription; // 상품 세부명

    @Min(value = 0, message = "최대 지급 개수는 0 이상이어야 합니다.")
    private long maxQuantity; // 최대 지급 개수

    @DecimalMin(value = "0.0", message = "확률은 0.0 이상이어야 합니다.")
    @DecimalMax(value = "100.0", message = "확률은 100.0 이하이어야 합니다.")
    private double probability; // 확률

    private LocalDateTime createdDate;
}