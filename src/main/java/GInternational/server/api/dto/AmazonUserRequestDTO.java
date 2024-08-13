package GInternational.server.api.dto;

import GInternational.server.api.vo.AmazonUserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AmazonUserRequestDTO {

    private String username;  //로그인 시 아이디 //중복불가
    private String password;  //로그인 시 비밀 번호
    private String nickname;  //닉네임 //중복불가
    private String phone;     //휴대폰 번호 //중복불가
    private String amazonCode;    // 회원가입시 추천인 코드
    private String ownername;  //예금주
    private long number;  //계좌 번호
    private String bankname;  //은행명
    private String bankPassword; // 환전비밀번호
    private int lv; //멤버 레벨
    private String distributor; // 총판 구분을 위한 필드값. 누구에게 가입되었는지를 의미. (예: 윈드, 메가, 기타 총판 등등 최상위 값)

    private Double slotRolling; // 슬롯 롤링 %
    private Double casinoRolling; // 카지노 롤링%

    private String partnerType; // 파트너 타입 (대본사, 본사, 부본사, 총판, 아마존)




    private String approveIP;
    private String role;
    private AmazonUserStatusEnum amazonUserStatus;
}
