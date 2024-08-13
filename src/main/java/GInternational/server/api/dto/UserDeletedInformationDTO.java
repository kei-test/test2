package GInternational.server.api.dto;

import GInternational.server.api.dto.WalletDTO;
import GInternational.server.api.entity.Wallet;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDeletedInformationDTO {

    /**
     * 탈 퇴 회 원 종 합 1-5
     */


    private Long id;
    private int lv;
    private String username;         // 유저네임
    private String nickname;         // 닉네임
    private String number;           // 계좌번호
    private String distributor;      // 총판
    private int betMoney;            // 베팅 머니
    private boolean enabled;         // 회원 활성화 여부
    private long depositTotal;       // 기간 별 입금액
    private long withdrawTotal;      // 기간 별 출금액
    private long totalSettlement;    // 기간 별 정산액
    private long visitCount;         // 로그인 횟수
    private boolean isDeleted;       // 탈퇴 여부
    private LocalDateTime deletedAt; // 탈퇴 회원 등록 일자

    private WalletDTO wallet;  // 현재 금액과 충전 횟수
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastVisit;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastBetTime;


    public UserDeletedInformationDTO(Wallet wallet) {
        this.wallet = new WalletDTO(wallet.getUser().getWallet());
    }
}
