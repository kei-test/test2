package GInternational.server.api.entity;

import GInternational.server.api.vo.AdminEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import GInternational.server.common.BaseEntity;
import GInternational.server.kplay.game.entity.GameFavorite;

import GInternational.server.api.vo.AmazonUserStatusEnum;
import GInternational.server.api.vo.UserGubunEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "users")
// @Table(uniqueConstraints = {
// @UniqueConstraint(columnNames = {"distributor", "username"}),
// @UniqueConstraint(columnNames = {"distributor", "phone"}),
// @UniqueConstraint(columnNames = {"distributor", "nickname"})
// })
public class User extends BaseEntity implements Serializable {

    // GUEST < USER < MANAGER < ADMIN
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "aas_id")
    private Integer aasId; // aas 값이 들어있음

    @JsonIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_recommended_users", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "recommended_user")
    private List<String> recommendedUsers; // 사용자가 추천한 다른 사용자들의 목록

    @Column(name = "recommended_count")
    private int recommendedCount; // 추천 횟수

    @Column(name = "referred_by", nullable = false)
    private String referredBy; // 사용자가 처음 가입할 때 추천받은 사용자의 username

    @Column(name = "recommendation_code", unique = true)
    private String recommendationCode; // 추천 코드

    // user의 추천인과 같은 역할. 총판페이지에서의 추천코드. 이 코드를 입력 후 가입하면, 코드생성자의 username이 신규가입자
    // referredBy에 저장됨.
    @Column(name = "amazon_code", unique = true)
    private String amazonCode;

    // 기본 정보
    @Column(nullable = false, unique = true)
    private String username; // 로그인 시 아이디 //중복불가
    @Column(nullable = false)
    private String password; // 로그인 시 비밀 번호
    @Column(nullable = false)
    private String nickname; // 별칭 //중복불가
    @Column(unique = true)
    private String phone; // 휴대폰 번호 //중복불가
    @Column(nullable = false)
    private String email; // 이메일
    @Column(nullable = false)
    private String birth; // 생년월일
    @Column(nullable = false)
    private String role; // GUEST or USER or ADMIN or MANAGER or TEST
    @Column(name = "name")
    private String name; // 실명
    @Column(name = "lv")
    private int lv; // 회원 레벨 Lv.01 ~ Lv.10
    @Column(name = "exp")
    private long exp; // 경험치
    @Column(name = "next_level_exp")
    private long nextLevelExp;// 다음 레벨업을 위한 필요 경험치 (다음레벨 최소경험치 - 현재 경험치)

    // 아이피 관련
    @Column(name = "ip")
    private String ip; // 최초 가입시점의 ip
    @Column(name = "approve_ip")
    private String approveIp; // 로그인이 승인된 IP 주소
    @Column(name = "updated_at_approve_ip")
    private LocalDateTime updatedAtApproveIp; // 승인된 IP가 업데이트 된 시간
    @Column(name = "last_accessed_ip")
    private String lastAccessedIp; // 최근 접속 IP
    @Column(name = "last_accessed_device")
    private String lastAccessedDevice; // 최근 접속 단말기
    @Column(name = "last_accessed_country")
    private String lastAccessedCountry; // 최근 접속 국가

    @Column(name = "enabled")
    private boolean enabled; // 회원 활성화 여부
    @Column(name = "is_deleted")
    private boolean isDeleted; // 회원 삭제 여부

    // 이넘
    @Enumerated(EnumType.STRING)
    @Column(name = "user_gubun")
    private UserGubunEnum userGubunEnum; // 유저 구분 이넘값
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_enum")
    private AdminEnum adminEnum; // 어드민 사용중, 사용불가 상태 이넘값
    @Enumerated(EnumType.STRING)
    @Column(name = "amazon_user_status")
    private AmazonUserStatusEnum amazonUserStatus; // 대기-정상-정지
    @Enumerated(EnumType.STRING)
    @Column(name = "monitoring_status")
    private UserMonitoringStatusEnum monitoringStatus; // 정상, 주시베팅, 초과베팅

    // 시간 관련
    @Column(name = "last_visit")
    private LocalDateTime lastVisit; // 최근 로그인 시간
    @Column(name = "visit_count")
    private long visitCount; // 로그인 카운트
    @Column(name = "processed_at")
    private LocalDateTime processedAt; // 처리 시간
    @Column(name = "last_bet_time")
    private LocalDateTime lastBetTime; // 마지막 베팅 시간
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 회원 삭제 시간
    @Column(name = "recommendation_code_issued_at")
    private LocalDateTime recommendationCodeIssuedAt; // 추천인 코드 발급 시간

    // 이벤트 관련
    @Column(name = "roulette_count")
    private long rouletteCount = 1; // 룰렛 이용 가능 횟수 (이용시 차감)
    @Column(name = "bonus_roulette_count")
    private long bonusRouletteCount = 0; // 보너스 룰렛 이용 횟수 (이용시 차감)
    @Column(name = "last_bonus_roulette_date")
    private LocalDate lastBonusRouletteDate; // 보너스 룰렛을 마지막으로 받은 날짜
    @Column(name = "attendance_roulette_count")
    private long attendanceRouletteCount = 0; // 한달에 25일 이상 출석시 출석 체크 룰렛 이용 횟수 (이용시 차감)
    @Column(name = "apple_count")
    private long appleCount = 1; // 사과줍기 이용가능 횟수 (이용시 차감)

    // 총판 관련
    // 계층구조 형성을 위한 아이디값.
    @Column(name = "dae_id")
    private Long daeId; // 대본사
    @Column(name = "bon_id")
    private Long bonId; // 본사
    @Column(name = "bu_id")
    private Long buId; // 부본사
    @Column(name = "chong_id")
    private Long chongId; // 총판
    @Column(name = "mae_id")
    private Long maeId; // 매장
    @Column(name = "partner_type")
    private String partnerType; // 파트너 타입 (대본사, 본사, 부본사, 총판, 아마존)
    @Column(name = "is_amazon_user",columnDefinition = "bit(1) default 0")
    private boolean isAmazonUser; // 아마존코드로 가입된 계정인지 여부 (총판 회원조회/관리를 위한 값)
    @Column(name = "is_dst_user", columnDefinition = "bit(1) default 0")
    private boolean isDstUser; // DST로 가입된 계정인지 여부 (총판 회원조회/관리를 위한 값)

    @Column(name = "fail_visit_count")
    private long failVisitCount; // 접속실패 횟수
    @Column(name = "slot_rolling")
    private double slotRolling; // 슬롯 롤링 %
    @Column(name = "casino_rolling")
    private double casinoRolling; // 카지노 롤링%
    @Column(name = "distributor")
    private String distributor; // 총판 구분을 위한 필드값. 누구에게 가입되었는지를 의미. (예: 윈드, 메가, 총판 중 "대본사", "DST"만)
    @Column(name = "store")
    private String store; // 총판 중 "대본사" 제외하고 본사,부본사,총판,매장을 의미함.

    private String site = "test";

    /**
     *  아마존 계층구조
     *  "대본사(username)-본사(username)-부본사(username)-총판(username)-매장(username)" 형식으로 기입.
     *  이 유저를 추천한 파트너의 username이 "qwe123" 파트너타입이 "본사"라면
     *  예)
     *  대본사(qwe123의 referrerBy값)-본사(qwe123)-부본사(qwe123의 recommendedUsers중 partnerType이 "부본사"인 유저의 첫번째값)-총판(부본사 username의 recommendedUsers중 partnerType이 "본사"인 유저의 첫번째값)-매장(총판 username의 recommendedUsers중 partnerType이 "총판"인 유저의 첫번째값)
     *  위 예시처럼 이 유저를 추천한 파트너의 계층구조 위치를 파악하고, 상위, 하위 파트너들의 username을 모두 찾아야 함.
     *  recommendedUsers는 여러명이 있을 수 있으며, 그 여러명 중 파트너가 아닌 일반유저가 있을수 있으므로 위 예시처럼 조건을 달아놓았음.
     *  단, 이 유저를 추천한 파트너(qwe123)의 partnerType이 "DST"인 경우 "대본사(qwe123)" 형식으로만 기입한다. DST는 계층구조가 없다.
     *  ROLE_USER가 아니면 이 값이 "" 빈칸임.
     */
    @Column(name = "structure")
    private String structure = "";

    @Column(name = "sms_receipt")
    private boolean smsReceipt = true; // sms 수신여부
    @Column(name = "amazon_visible")
    private boolean amazonVisible = false; // 아마존(총판)페이지 노출여부
    @Column(name = "account_visible")
    private boolean accountVisible = false; // 계좌 노출여부
    @Column(name = "can_recommend")
    private boolean canRecommend = true; // 추천 가능여부
    @Column(name = "can_post")
    private boolean canPost = true; // 게시글 작성 가능여부
    @Column(name = "can_comment")
    private boolean canComment = true; // 댓글 작성 가능여부
    @Column(name = "can_bonus")
    private boolean canBonus = true; // 매충 지급여부

    @Column(name = "kakao_registered")
    private boolean kakaoRegistered = false; // 카카오톡 등록 여부
    @Column(name = "kakao_id")
    private String kakaoId; // 카카오톡 아이디
    @Column(name = "telegram_registered")
    private boolean telegramRegistered = false; // 텔레그램 등록 여부
    @Column(name = "telegram_id")
    private String telegramId; // 텔레그램 아이디

    @Column(name = "virtual_account_enabled")
    private boolean virtualAccountEnabled = false; // 가상계좌 사용여부
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

    /**
     * 연관관계 외래키로 인해 유저 객체 삭제 불가
     * charged_count null 로 잡히는 부분 확인해야함
     */

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Wallet wallet;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Account account;

    @JsonIgnore
    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Articles> articles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<AdminLoginHistory> adminLoginHistories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    private List<Messages> senders = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<Messages> receivers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<RechargeTransaction> rechargeTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ExchangeTransaction> exchangeTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PointTransaction> pointTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE)
    private List<RouletteResults> rouletteResults = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<CheckAttendance> checkAttendances = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<RollingTransaction> rollingTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<CouponTransaction> couponTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE)
    private List<PointLog> pointLogs = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<MoneyLog> moneyLogs = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<NewCheckAttendance> newCheckAttendances = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<EventsBoard> events = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE)
    private List<AppleResults> appleResults = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE)
    private List<AttendanceRouletteResults> attendanceRouletteResults = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE)
    private List<DedicatedAccount> dedicatedAccounts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<AutoDepositTransaction> autoDepositTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<AutoTransaction> autoTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<CasinoTransaction> casinoTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<BetHistory> inPlayBetHistories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    private List<AmazonMessages> amazonSenders = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<AmazonMessages> amazonReceivers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<AmazonCommunity> writers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<AmazonComment> amazonComments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<AmazonRechargeTransaction> amazonRechargeTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<AmazonExchangeTransaction> amazonExchangeTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<AuditLog> auditLogs = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<GameFavorite> favoriteGames = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<LoginInfo> loginInfos = new ArrayList<>();

    // 슬롯롤링적립 감소 로직
    public void setSlotRolling(Double newSlotRolling) {
        if (newSlotRolling < this.slotRolling) {
            throw new IllegalArgumentException("보유한 슬롯롤링적립 보다 부여할 슬롯롤링 적립이 더 큽니다.");
        }
        this.slotRolling = Math.round(newSlotRolling * 100.0) / 100.0;
    }

    // 카지노롤링적립 감소 로직
    public void setCasinoRolling(Double newCasinoRolling) {
        if (newCasinoRolling < this.casinoRolling) {
            throw new IllegalArgumentException("보유한 카지노롤링적립 보다 부여할 카지노롤링적립 적립이 더 큽니다.");
        }
        this.casinoRolling = Math.round(newCasinoRolling * 100.0) / 100.0;
    }

    // 유저가 룰렛을 돌릴 때 마다 카운트를 1씩 감소시킴
    public void decreaseRouletteCount() {
        if (this.rouletteCount > 0) {
            this.rouletteCount -= 1;
        }
    }

    // 유저가 하루에 1만원 이상 충전시 보너스카운트를 1씩 증가시킴
    public void increaseBonusRouletteCount() {
        this.bonusRouletteCount += 1;
    }

    // 룰렛 돌리면 보너스룰렛 카운트 감소시킴
    public void decreaseBonusRouletteCount() {
        if (this.bonusRouletteCount > 0) {
            this.bonusRouletteCount -= 1;
        }
    }

    // 한달에 25일 이상 출석시 증가
    public void increaseAttendanceRouletteCount() {
        this.attendanceRouletteCount += 1;
    }

    public void decreaseAttendanceRouletteCount() {
        if (this.attendanceRouletteCount > 0) {
            this.attendanceRouletteCount -= 1;
        }
    }

    // 사과줍기 게임을 할때마다 AppleCount를 감소시킴
    public void decreaseAppleCount() {
        if (this.appleCount > 0) {
            this.appleCount -= 1;
        }
    }

    public List<String> getRoleList() {
        if (this.role.length() > 0) {
            return Arrays.asList(this.role.split(","));
        }
        return new ArrayList<>();
    }

    public User(int lv) {
        this.lv = lv;
    }
}
