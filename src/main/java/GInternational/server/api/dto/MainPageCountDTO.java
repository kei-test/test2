package GInternational.server.api.dto;

import GInternational.server.api.vo.TransactionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MainPageCountDTO {
    // 충전관련 카운트
    private Long rechargeUnreadCount;
    private Long rechargeWaitingCount;
    private Long rechargeApprovalCount;

    // 환전관련 카운트
    private Long exchangeUnreadCount;
    private Long exchangeWaitingCount;
    private Long exchangeApprovalCount;

    // 고객센터문의 관련 카운트
    private Long requestAnswerCount;
    private Long waitingAnswerCount;

    // 유저 회원수 관련 카운트
    private Long userCount;
    private Long guestCount;

    // 가장 오래전 가입한 ROLE_GUEST의 추천인
    private String referredByOldGuestUser;

    // 유저의 monitoringStatus가 초과베팅인 유저
    private String oldestExceedingBetUsername;
    // 유저의 monitoringStatus가 주시베팅인 유저
    private String oldestMonitoringBetUsername;
}
