package GInternational.server.kplay.game.dto.list;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestInfoList {

    //서버로 들어올 값을 가진 DTO

    private Long id;
    private int prdId;
    private int gameIndex;
    private String name;
    private String icon;
    private String rtp;
    private String type;
    private int isEnabled;
    private String gameCategory;
}
