package GInternational.server.kplay.game.dto.list;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDTO {
    private Long id;
    private int prdId;
    private int gameIndex;
    private String name;
    private String icon;
    private String rtp;
    private String type;
    private int isEnabled;
    private String gameCategory;
    private boolean favorite;
}
