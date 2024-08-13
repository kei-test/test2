package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SideBar1DTO {
    private int todayRechargeRequestCount; // "충전요청", 트랜잭션 상태 UNREAD 건수 (당일)
    private int todayExchangeRequestCount; // "환전요청", 트랜잭션 상태 UNREAD 건수 (당일)
    private int todayJoinRequestCount;     // "가입신청", ROLE이 GUEST인 유저 수 (당일)
    private int totalCustomerCenterCount;  // "고객센터", 고객센터 게시글 중 "답변대기" 건수 (전체기간)
    private int totalLoginRequestCount;    // "비번찾기문의", 로그인창에서 로그인문의 누르면 등록되는 게시글 "답변대기" 건수 (전체기간)
    private int totalPasswordRequestCount; // "비번변경문의", 비밀번호 변경신청에서 대기건수 (전체기간)
    private long centerSiteBalance;        // "센터사이트잔고", 차액통계에서 가장마지막에 입력된 합산금액 (앞중뒤) totalAccount
    // 관리자공지사항, 스포츠등록대기, 베팅모니터링 보류
    private int inplayBetMonitoring;       // "인플레이 베팅 모니터링", 회원상세에서 주시베팅, 초과베팅 등록된 유저가 베팅한 건수. (미확인건)
    // 미니게임 결과 모니터링, 캐시백 이벤트요청, 슬롯 롤링 이벤트 요청, 쿠폰지급요창 보류
}