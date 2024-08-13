package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecommendationCodeResDTO {
    private Long id;
    private String distributor;
    private String username;
    private String nickname;
    private int recommendedCount;
    private String recommendationCode;
    private LocalDateTime createdAt;
}
