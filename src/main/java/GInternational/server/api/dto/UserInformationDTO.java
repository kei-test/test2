package GInternational.server.api.dto;

import GInternational.server.api.dto.WalletDTO;
import GInternational.server.api.entity.Wallet;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
public class UserInformationDTO {
    /**
     * 회 원 종 합 1-2
     */

    private Long id;
    private int lv;
    private long exp;
    private String username;
    private String nickname;
    private String distributor;   // 최상위파트너
    private String store;         // 하위파트너
    private boolean enabled;      // 회원 활성화 여부
    private long depositTotal;    // 기간별 입금액
    private long withdrawTotal;   // 기간별 출금액
    private long totalSettlement; // 기간별 정산액
    private WalletDTO wallet;     // 현재 금액과 충전 횟수
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastVisit;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastBetTime;



    //유저 금액정보 (
    // F 보유 머니,
    // F 기간별 검색
    // F 기간별 입금 총액,
    // 기간별 출금 총액,
    // 기간별 정산,
    // 기간별 충전 횟수,
    // F 최근 충전일,
    // F 최근 로그인,
    // F 최근 배팅일,
    // F 상태 )

    public UserInformationDTO(Wallet wallet) {
        this.wallet = new WalletDTO(wallet.getUser().getWallet());
    }

}
