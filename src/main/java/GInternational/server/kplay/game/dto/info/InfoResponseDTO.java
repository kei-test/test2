package GInternational.server.kplay.game.dto.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InfoResponseDTO {
    //외부
    private int status;
    private Map<String, List<RequestInfoDTO>> list;
}
