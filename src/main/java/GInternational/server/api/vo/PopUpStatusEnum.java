package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PopUpStatusEnum {

    활성("활성"),
    비활성("비활성");

    private String value;
}
