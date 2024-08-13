package GInternational.server.api.dto;

import GInternational.server.api.vo.AppStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountStatusUpdateDTO {

    private AppStatus status;
}
