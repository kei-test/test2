package GInternational.server.api.dto;

import GInternational.server.api.vo.UserGubunEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    private Long id;
    private String username;  // 로그인 시 아이디
    private String password;  // 로그인 시 비밀 번호  //로그인 시 비밀 번호
    private String nickname;  // 별칭
    private String phone;     // 휴대폰 번호
    private String name;      // 실명
    private String joinRoute; // 가입 경로
    private String birth;
    private String bankPassword; // 환전 비밀번호
    @Email
    private String email;
    private String referredBy;  // 추천인
    private Boolean enabled;    // 회원 활성화 여부
    private Boolean isDeleted;  // 회원 삭제 여부
    private String role;        // USER or ADMIN or MANAGER
    private Integer lv;             // 회원 등급 Lv.01 ~ Lv.20
    private String ip;
    private String ownerName;   // 예금주
    private Long number;        // 계좌번호
    private String bankName;    // 은행명
    private long sportsBalance; // 금액
    private String distributor;
    private String store;
    private UserGubunEnum userGubunEnum;
    private LocalDateTime lastPasswordChanged;  // 마지막 비밀 번호가 변경된 시각
    private LocalDateTime lastVisit;
    private boolean isAmazonUser;
    private boolean isDstUser;

    private Boolean kakaoRegistered;
    private String kakaoId;
    private Boolean telegramRegistered;
    private String telegramId;
    private Boolean smsReceipt;
    private Boolean amazonVisible;
    private Boolean accountVisible;
    private Boolean canRecommend;
    private Boolean canPost;
    private Boolean canComment;
    private Boolean canBonus;

    private String memo1;
    private String memo2;
    private String memo3;
    private String memo4;
    private String memo5;
    private String memo6;

    private int rouletteCount = 1; // 룰렛 이용 가능 횟수

    private Boolean virtualAccountEnabled;  // 가상계좌 사용여부
    private String virtualAccountOwnerName; // 가상계좌 예금주
    private String virtualAccountNumber;    // 가상계좌 계좌번호


    /**
     * 순수 영문
     * 순수 한글
     * 영문 + 숫자
     * 한글 + 숫자
     * 한글 + 영문
     */
}