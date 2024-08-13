package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LevelPointLimitDTO {

    private Long id;

    private int level1;
    private int level2;
    private int level3;
    private int level4;
    private int level5;
    private int level6;
    private int level7;
    private int level8;
    private int level9;
    private int level10;
}
