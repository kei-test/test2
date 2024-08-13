package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompRequestDTO {

    private Long userId; // 콤프 적립을 신청한 유저의 id
    private LocalDateTime createdAt; // 콤프 적립을 신청한 시간
}
