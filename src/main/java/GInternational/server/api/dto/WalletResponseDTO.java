package GInternational.server.api.dto;


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
public class WalletResponseDTO {
    private Long id;
    private String ownerName;   // 예금주
    private long number;        // 계좌 번호
    private String bankName;    // 은행명

    private long sportsBalance; //스포츠 머니
    private long casinoBalance; //카지노 머니
    private long point;         //포인트

    private long amazonMoney; //총판페이지에서의 머니
    private long amazonPoint; //총판페이지에서의 포인트


    private UserProfileDTO user;  //계좌를 등록한 회원의 정보

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;


    //Wallet 주인을 가져오는 로직
    public WalletResponseDTO(Wallet wallet) {
        this.user = new UserProfileDTO(wallet.getUser());
    }
}
