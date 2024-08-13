package GInternational.server.api.dto;


import GInternational.server.api.entity.CasinoTransaction;
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
public class CasinoTransactionResponseDTO {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;  //처리 시간

    private long usedCasinoBalance;  //전환에 사용한 카지노머니
    private long remainingCasinoBalance;  //현재 보유 카지노머니
    private long usedSportsBalance;  //전환에 사용한 스포츠머니
    private long remainingSportsBalance;  // 현재 보유 스포츠머니
    private long exchangedCount;  //전환 횟수
    private String description;
    private String ip;
    private UserProfileDTO user;




    public CasinoTransactionResponseDTO(CasinoTransaction transaction) {
        this.id = transaction.getId();
        this.processedAt = transaction.getProcessedAt();
        this.usedCasinoBalance = transaction.getUsedCasinoBalance();
        this.remainingCasinoBalance = transaction.getRemainingCasinoBalance();
        this.usedSportsBalance = transaction.getUsedSportsBalance();
        this.remainingSportsBalance = transaction.getRemainingSportsBalance();
        this.exchangedCount = transaction.getExchangedCount();
        this.description = transaction.getDescription();
        this.ip = transaction.getIp();
        this.user = new UserProfileDTO(transaction.getUser());
    }
}
