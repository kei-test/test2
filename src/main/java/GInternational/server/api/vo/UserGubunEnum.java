package GInternational.server.api.vo;


import lombok.Getter;

@Getter
public enum UserGubunEnum {


    //회원 상태 관리
    대기("대기"),
    정상("정상"),
    단폴("단폴"),
    배당("배당"),
    호원("호원"),
    불량("불량"),
    악의("악의"),
    정지("정지"),
    거절("거절"),
    하락탈퇴("하락탈퇴"),
    탈퇴1("탈퇴1"),
    탈퇴2("탈퇴2"),
    탈퇴3("탈퇴3");

    private String 표시이름;


    UserGubunEnum(String 표시이름) {
        this.표시이름 = 표시이름;
    }

    public String get표시이름() {
        return 표시이름;
    }
}
