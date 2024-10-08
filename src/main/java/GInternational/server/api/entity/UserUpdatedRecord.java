package GInternational.server.api.entity;

import GInternational.server.api.vo.UserGubunEnum;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "user_updated_record")
public class UserUpdatedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_updated_record_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String username;      // ID
    private String nickname;      // 닉네임
    private String password;      // 비밀번호
    private String phone;         // 핸드폰번호
    @Column(name = "bank_name")
    private String bankName;      // 은행명
    private Long number;          // 계좌번호
    private String email;         // 이메일
    @Column(name = "owner_name")
    private String ownerName;     // 예금주
    private int lv;               // 레벨
    private UserGubunEnum gubun;  // 상태
    @Column(name = "referred_by")
    private String referredBy;    // 추천인
    private String distributor;   // 최상위 파트너 (대본사 or DST의 username)
    private String store = "";         // 하위 파트너 (본사, 부본사, 총판, 매장의 username)

    // 아마존인지 DST인지 구분값, 유저 엔티티에서 isAmazonUser가 true면 "아마존", isDstUser가 true면 "DST"로 저장. 둘다 true일 수 없음.
    @Column(name = "is_amazon_user", columnDefinition = "bit(1) default 0")
    private Boolean isAmazonUser; // 아마존코드로 가입된 계정인지 여부 (총판 회원조회/관리를 위한 값)
    @Column(name = "is_dst_user", columnDefinition = "bit(1) default 0")
    private Boolean isDstUser; // DST로 가입된 계정인지 여부 (총판 회원조회/관리를 위한 값)

    @Column(name = "kakao_registered")
    private Boolean kakaoRegistered = false; // 카카오톡 등록 여부
    @Column(name = "kakao_id")
    private String kakaoId; // 카카오톡 아이디
    @Column(name = "telegram_registered")
    private Boolean telegramRegistered = false; // 텔레그램 등록 여부
    @Column(name = "telegram_id")
    private String telegramId; // 텔레그램 아이디

    @Column(name = "virtual_account_enabled")
    private Boolean virtualAccountEnabled = false; // 가상계좌 사용여부
    @Column(name = "virtual_account_owner_name")
    private String virtualAccountOwnerName; // 가상계좌 예금주
    @Column(name = "virtual_account_number")
    private String virtualAccountNumber; // 가상계좌 계좌번호

    @Column(columnDefinition = "TEXT")
    private String memo1;
    @Column(columnDefinition = "TEXT")
    private String memo2;
    @Column(columnDefinition = "TEXT")
    private String memo3;
    @Column(columnDefinition = "TEXT")
    private String memo4;
    @Column(columnDefinition = "TEXT")
    private String memo5;
    @Column(columnDefinition = "TEXT")
    private String memo6;

    @Column(name = "sms_receipt")
    private boolean smsReceipt = true; // sms 수신여부
    @Column(name = "amazon_visible")
    private boolean amazonVisible = false; // 아마존(총판)페이지 노출여부
    @Column(name = "account_visible")
    private boolean accountVisible = false; // 계좌 노출여부
    @Column(name = "can_recommend")
    private boolean canRecommend = false; // 추천 가능여부
    @Column(name = "can_post")
    private boolean canPost = true; // 게시글 작성 가능여부
    @Column(name = "can_comment")
    private boolean canComment = true; // 댓글 작성 가능여부
    @Column(name = "can_bonus")
    private boolean canBonus = true; // 매충 지급여부

    @Column(name = "exp")
    private Long exp;

//    @Column(name = "changed_by")
//    private String changedBy;     // 관리자가 변경 = (관), 유저가 변경 = (유)

    @Column(name = "changed_column")
    private String changedColumn; // 바뀐컬럼
    @Column(name = "before_data")
    private String beforeData;    // 변경전 값
    @Column(name = "after_data")
    private String afterData;     // 변경후 값

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
