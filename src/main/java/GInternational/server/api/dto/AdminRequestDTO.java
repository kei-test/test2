package GInternational.server.api.dto;

import GInternational.server.api.vo.AdminEnum;
import GInternational.server.api.vo.UserGubunEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminRequestDTO {


    private String username;
    private UserGubunEnum userGubunEnum;
    private AdminEnum adminEnum;
    private String password;
    private String role;
    private String name;
    private String nickname;
    private String phone;
    private String approveIp;
}
