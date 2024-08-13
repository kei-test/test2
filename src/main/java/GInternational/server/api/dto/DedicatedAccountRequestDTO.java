package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DedicatedAccountRequestDTO {

    private Long id;

    @NotBlank(message = "은행명은 비어 있을 수 없습니다.")
    private String bankName; // 은행명

    @NotBlank(message = "예금주은 비어 있을 수 없습니다.")
    private String ownerName; // 예금주

    @NotNull(message = "계좌번호는 비어 있을 수 없습니다.")
    private Long number; // 계좌번호

    @NotEmpty(message = "유저레벨은 비어 있을 수 없습니다.")
    private Set<Integer> lv; // 유저레벨
}
