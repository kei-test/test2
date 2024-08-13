package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRoleUpdateDTO {
    private Long id;
    private String role;  // role_user 전송
    private boolean enabled;  // enabled ture 전송
}
