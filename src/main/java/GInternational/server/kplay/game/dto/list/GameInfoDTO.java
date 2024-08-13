package GInternational.server.kplay.game.dto.list;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GameInfoDTO {

    //내부

    private Long id;
    private int prdId;
    private int gameIndex;
    private String name;
    private String icon;
    private String rtp;
    private String type;
    private int isEnabled;
}
