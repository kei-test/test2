package GInternational.server.kplay.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "game")
public class Game implements Serializable {

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
    @Column(length = 1, nullable = false,columnDefinition = "varchar(1) default 'S'")
    private String gameCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "game",cascade = CascadeType.REMOVE)
    private List<GameFavorite> gameFavorites = new ArrayList<>();
}
