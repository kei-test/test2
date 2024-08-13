package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReferredGubunEnum {

    추천인("추천인"),
    추천코드("추천코드");


    private String value;
}
