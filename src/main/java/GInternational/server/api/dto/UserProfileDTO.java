package GInternational.server.api.dto;

import GInternational.server.api.entity.User;
import GInternational.server.api.vo.UserGubunEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private int lv;
    private long exp;
    private String role;
    private String ip;
    private String distributor;
    private UserGubunEnum userGubunEnum;
    private String site;


    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
        this.lv = user.getLv();
        this.exp = user.getExp();
        this.role = user.getRole();
        this.ip = user.getIp();
        this.distributor = user.getDistributor();
        this.userGubunEnum = user.getUserGubunEnum();
        this.site = user.getSite();
    }
}
