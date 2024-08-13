package GInternational.server.kplay.game.dto.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InfoDTO {

    private int game_id;
    private int prd_id;
    private String icon;
    private String game_name;
    private String game_type;

}
