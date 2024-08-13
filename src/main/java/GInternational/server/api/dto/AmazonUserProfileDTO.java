package GInternational.server.api.dto;

import GInternational.server.api.vo.AmazonUserStatusEnum;
import GInternational.server.api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmazonUserProfileDTO {
    private Long id;
    private String username;
    private String nickname;
    private AmazonUserStatusEnum amazonUserStatus;


    public AmazonUserProfileDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.amazonUserStatus = user.getAmazonUserStatus();
    }
}
