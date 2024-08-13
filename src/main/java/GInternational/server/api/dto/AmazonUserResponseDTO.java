package GInternational.server.api.dto;

import GInternational.server.api.vo.AmazonUserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AmazonUserResponseDTO {

    private Long id;
    private String username;  //로그인 시 아이디 //중복불가
    private String password;  //로그인 시 비밀 번호
    private String nickname;  //닉네임 //중복불가
    private String phone;     //휴대폰 번호 //중복불가
    private String amazonCode;    // 상위코드 (상위 총판?)
    private String ownername;  //예금주
    private long number;  //계좌 번호
    private String bankname;  //은행명

    private String ip; // 아이피
    private String role; // 아마존 파트너들의 role은 admin 제외하고 전부 ROLE_USER
    private AmazonUserStatusEnum amazonUserStatus;  // 대기-정상-정지

    private long money; //보유머니
    private long point; //포인트
    private long todayDeposit; //금일 입금
    private long todayWithdraw; //금일 출금
    private long totalDeposit; //총 입금
    private long totalWithdraw; //총 출금
    private long totalProfitLoss; // 총손익 = 총입금 - 총출금

    private LocalDateTime createdAt; //가입일
    private LocalDateTime lastVisit; //최근접속
    private long failVisitCount; //접속실패 횟수

    private double slotRolling; // 슬롯 롤링 %
    private double casinoRolling; // 카지노 롤링%

    private Long daeId; // 대본사
    private Long bonId; // 본사
    private Long buId; // 부본사
    private Long chongId; // 총판
    private Long maeId; // 매장
    private String partnerType; // 파트너 타입 (대본사, 본사, 부본사, 총판, 아마존)

    private String distributor; // 총판 구분을 위한 필드값. 누구에게 가입되었는지를 의미. (예: 윈드, 메가, 기타 총판 등등 최상위 값)
}
