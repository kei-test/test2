package GInternational.server.kplay.game.dto.list;

import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApiResponseDTO {


    private int status;
    private Map<String, List<GameInfoDTO>> game_list;
}
