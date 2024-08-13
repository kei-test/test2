package GInternational.server.kplay.game.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "mega_game")
public class MegaGame implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    // { 게임 리스트 }
    @Column(name = "prd_id")
    private int prdId;
    @Column(name = "game_index")
    private int gameIndex;
    private String name;
    private String icon;
    private String rtp;
    private String type;
    @Column(name = "is_enabled")
    private int isEnabled;
}
