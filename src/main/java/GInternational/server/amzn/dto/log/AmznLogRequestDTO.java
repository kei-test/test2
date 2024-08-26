package GInternational.server.amzn.dto.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmznLogRequestDTO {
    private String username;
    private String password;
    private String temporaryPassword;
}
