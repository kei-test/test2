package GInternational.server.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PartnerListDTO {

    private String username;  //아이디
    private String nickname;  //별칭
    private long amazonMoney; //총판페이지에서의 머니
    private long amazonMileage; //총판 페이지의 마일리지
    private Long point;         //포인트
    private long todayDeposit; //금일 입금
    private long todayWithdraw; //금일 출금
    private long depositTotal;  //기간별 입금액
    private long withdrawTotal;  //기간별 출금액
    private long totalSettlement;  //기간별 정산액
    private long totalUserCount; //아마존 코드로 가입한 유저의 수
    private LocalDateTime createdAt; //등록일
}
