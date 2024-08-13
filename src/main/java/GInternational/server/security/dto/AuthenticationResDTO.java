package GInternational.server.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResDTO {

    private String error;

    public static AuthenticationResDTO createFailureResponse(String error) {
        return new AuthenticationResDTO(error);
    }
}
