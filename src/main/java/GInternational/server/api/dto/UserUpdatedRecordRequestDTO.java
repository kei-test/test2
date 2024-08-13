package GInternational.server.api.dto;

import GInternational.server.api.vo.UserGubunEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserUpdatedRecordRequestDTO {

    private Long userId;
    private String username;      // ID
    private String nickname;      // 닉네임
    private String password;      // 비밀번호
    private String phone;         // 핸드폰번호
    private String bankName;      // 은행명
    private Long number;          // 계좌번호
    private String email;         // 이메일
    private String ownerName;     // 예금주
    private int lv;               // 레벨
    private UserGubunEnum gubun;  // 상태
    private String referredBy;    // 추천인
    private String distributor;   // 총판

    private String changedColumn; // 바뀐컬럼
    private String beforeData;    // 변경전 값
    private String afterData;     // 변경후 값
}
