package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "amazon_rolling_transaction")
public class AmazonRollingTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "category")
    private String category;
    @Column(name = "username") //베팅한 유저
    private String username;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "bet_amount")
    private int betAmount;  //베팅금액
    @Column(name = "rolling_amount")
    private long rollingAmount;  //베팅으로 인해 발생된 롤링지급액
    @Column(name = "remaining_amazon_money")
    private long remainingAmazonMoney;
    @Column(name = "remaining_amazon_mileage")
    private long remainingAmazonMileage;
    @Column(name = "bet_time")
    private LocalDateTime betTime;  //베팅한 시각
    @Column(name = "processed_at")
    private LocalDateTime processedAt;  //지급한 시간

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
