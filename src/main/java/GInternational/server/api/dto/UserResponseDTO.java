package GInternational.server.api.dto;

import GInternational.server.api.entity.User;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private Integer aasId;
    private String username;  // 로그인 시 아이디
    private String password;  // 로그인 시 비밀 번호
    private String nickname;  // 별칭
    private String name;      // 실명
    private String phone;     // 휴대폰 번호
    private String birth;
    private String email;
    private String distributor; // 총판 구분을 위한 필드값. 누구에게 가입되었는지를 의미. (예: 윈드, 메가, 기타 총판 등등 최상위 값)
    private String store;       // 총판 중 "대본사" 제외하고 본사,부본사,총판,매장을 의미함.
    private String role;        // USER or ADMIN or MANAGER
    private int lv;             // 회원 등급 Lv.01 ~ Lv.10
    private long exp;           // 경험치
    private String ip;
    private String lastAccessedIp;
    private boolean enabled;   // 회원 활성화 여부
    private boolean isDeleted; // 회원 삭제 여부
    private long visitCount;
    private boolean isAmazonUser;
    private long nextLevelExp;
    private String referredBy;
    private UserGubunEnum userGubunEnum;
    private UserMonitoringStatusEnum monitoringStatus;

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
    private int recommendedCount;
    private long rouletteCount;
    private long bonusRouletteCount;

    private boolean virtualAccountEnabled;
    private String virtualAccountOwnerName;
    private String virtualAccountNumber;

    private String memo1;
    private String memo2;
    private String memo3;
    private String memo4;
    private String memo5;
    private String memo6;

    private Long daeId; // 대본사
    private Long bonId; // 본사
    private Long buId; // 부본사
    private Long chongId; // 총판
    private Long maeId; // 매장
    private String partnerType;

    private WalletDetailDTO wallet;

    private UserReferralInfoDTO referrerInfo; // 추천인 정보
    private List<UserReferralInfoDTO> recommendedUsersInfo; // 추천받은 유저들의 상세 정보 리스트

    public UserResponseDTO(User user, UserReferralInfoDTO referrerInfo, List<UserReferralInfoDTO> recommendedUsersInfo) {
        this.id = user.getId();
        this.aasId = user.getAasId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.birth = user.getBirth();
        this.email = user.getEmail();
        this.distributor = user.getDistributor();
        this.role = user.getRole();
        this.lv = user.getLv();
        this.ip = user.getIp();
        this.exp = user.getExp();
        this.lastAccessedIp = user.getLastAccessedIp();
        this.enabled = user.isEnabled();
        this.isDeleted = user.isDeleted();
        this.visitCount = user.getVisitCount();
        this.processedAt = user.getProcessedAt();
        this.lastVisit = user.getLastVisit();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.isAmazonUser = user.isAmazonUser();
        this.nextLevelExp = user.getNextLevelExp();
        this.referredBy = user.getReferredBy();
        this.kakaoRegistered = user.isKakaoRegistered();
        this.kakaoId = user.getKakaoId();
        this.telegramRegistered = user.isTelegramRegistered();
        this.telegramId = user.getTelegramId();
        this.smsReceipt = user.isSmsReceipt();
        this.amazonVisible = user.isAmazonVisible();
        this.accountVisible = user.isAccountVisible();
        this.canRecommend = user.isCanRecommend();
        this.canPost = user.isCanPost();
        this.canComment = user.isCanComment();
        this.canBonus = user.isCanBonus();
        this.memo1 = user.getMemo1();
        this.memo2 = user.getMemo2();
        this.memo3 = user.getMemo3();
        this.memo4 = user.getMemo4();
        this.memo5 = user.getMemo5();
        this.memo6 = user.getMemo6();
        this.recommendedCount = user.getRecommendedCount();
        this.rouletteCount = user.getRouletteCount();
        this.bonusRouletteCount = user.getBonusRouletteCount();
        this.userGubunEnum = user.getUserGubunEnum();
        this.monitoringStatus = user.getMonitoringStatus();
        this.virtualAccountEnabled = user.isVirtualAccountEnabled();
        this.virtualAccountNumber = user.getVirtualAccountNumber();
        this.virtualAccountOwnerName = user.getVirtualAccountOwnerName();
        this.wallet = (user.getWallet() != null) ? new WalletDetailDTO(user.getWallet()) : null;
        this.referrerInfo = referrerInfo;
        this.recommendedUsersInfo = recommendedUsersInfo;
    }


    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastVisit;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;
}

