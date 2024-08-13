package GInternational.server.api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsernameFindDto {

    //DB에 저장된 username 에 대한 정보를 담는 DTO
    private String name;
    private String phone;
}
