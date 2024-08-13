package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActiveUserResponseDTO {
    private Long id;
    private Integer lv;                    // 레벨
    private String username;               // 로그인 ID
    private String nickname;               // 닉네임
    private String userLocation;           // 현재위치
    private long sportsBalance;            // 스포츠 머니
    private long casinoBalance;            // 카지노 머니
    private long point;                    // 포인트
    private long depositTotal;             // 총 입금액
    private long withdrawTotal;            // 총 출금액
    private long visitCount;               // 총 로그인 횟수
    private String lastAccessedIp;         // 로그인 ip
    private String lastAccessedDevice;     // 로그인 단말기 구분 (M = mobile, P = pc)
    private String distributor;            // 최상위파트너
    private String store;                  // 하위파트너
    private String lastVisit;              // 로그인 시간
    private String lastRechargedAt;        // 마지막 충전일
    private String lastAccessedCountry;    // 로그인 국가
}
