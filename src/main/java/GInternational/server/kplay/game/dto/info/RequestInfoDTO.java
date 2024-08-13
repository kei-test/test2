package GInternational.server.kplay.game.dto.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestInfoDTO {

    //프론트에서 주는 값
    private int game_id;
    private int prd_id;
    private String game_name;
    private String game_type;
}
