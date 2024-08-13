package GInternational.server.api.dto;

import GInternational.server.api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerProfileDTO {
    private Long id;
    private String nickname;


    public ManagerProfileDTO(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
    }
}
