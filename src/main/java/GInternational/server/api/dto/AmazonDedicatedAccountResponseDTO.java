package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmazonDedicatedAccountResponseDTO {

    private String bankName; // 은행명
    private String ownerName; // 예금주
    private Long number; // 계좌번호
    private Set<Integer> lv; // 유저레벨
    private boolean isActive; // 활성상태
}
