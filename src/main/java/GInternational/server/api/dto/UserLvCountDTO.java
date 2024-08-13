package GInternational.server.api.dto;

import GInternational.server.api.vo.UserGubunEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class UserLvCountDTO {

    private String lv;
    private UserGubunEnum userGubunEnum;


    public UserLvCountDTO(String lv, UserGubunEnum userGubunEnum) {
        this.lv = lv;
        this.userGubunEnum = userGubunEnum;
    }
}
