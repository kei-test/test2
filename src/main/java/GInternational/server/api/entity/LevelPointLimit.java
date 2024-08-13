package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "level_point_limit")
public class LevelPointLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_point_limit_id")
    private Long id;

    @Column(name = "level_1")
    private int level1;
    @Column(name = "level_2")
    private int level2;
    @Column(name = "level_3")
    private int level3;
    @Column(name = "level_4")
    private int level4;
    @Column(name = "level_5")
    private int level5;
    @Column(name = "level_6")
    private int level6;
    @Column(name = "level_7")
    private int level7;
    @Column(name = "level_8")
    private int level8;
    @Column(name = "level_9")
    private int level9;
    @Column(name = "level_10")
    private int level10;
}
