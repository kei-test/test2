package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpSettingResDTO {

    private Long id;
    private long minExp;
    private long maxExp;
    private int lv;
}
