package GInternational.server.api.dto;

import GInternational.server.api.vo.ExpRecordEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpRecordRequestDTO {
    private Long userId;
    private String username;
    private String nickname;
    private long exp;
    private String ip;
    private ExpRecordEnum content;
}
