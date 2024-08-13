package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExpRecordEnum {

    로그인경험치("로그인경험치"),
    스포츠베팅경험치("스포츠베팅경험치"),
    케이플레이스포츠베팅경험치("케이플레이스포츠베팅경험치"),
    카지노베팅경험치("카지노베팅경험치"),
    슬롯베팅경험치("슬롯베팅경험치"),
    미니게임경험치("미니게임경험치"),
    신규회원추천경험치("신규회원추천경험치"),
    게시글작성경험치("게시글작성경험치"),
    댓글작성경험치("댓글작성경험치"),
    아케이드베팅경험치("아케이드베팅경험치"),

    스포츠베팅누적경험치("스포츠베팅누적경험치"),
    카지노베팅누적경험치("카지노베팅누적경험치"),
    슬롯베팅누적경험치("슬롯베팅누적경험치");




    private String value;
}
