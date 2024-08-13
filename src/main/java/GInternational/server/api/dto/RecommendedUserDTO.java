package GInternational.server.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecommendedUserDTO {
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime lastVisit;
    private String userGubun;
    private String nickname;
}
