package GInternational.server.kplay.debit.entity;

import GInternational.server.common.BaseEntity;
import GInternational.server.kplay.credit.entity.Credit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "debit")
public class Debit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debit_id")
    private Long id;

    @Column(name = "aas_id")
    private int user_id; // AAS 사용자 ID

    private int amount; // 베팅 금액 (게임사마다 요구하는 베팅금액 필드가 달라서 2개)

    private int credit_amount; // 당첨 금액

    private int prd_id; // 제품 ID // 슬롯 카지노 구분값.

    @Column(name = "txn_id", unique = true)
    private String txnId; // 고유 트랜잭션 ID

    @Column(name = "game_id")
    private int game_id; // 게임 ID

    private String table_id; // 테이블 ID

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime created_at;
    @Column(name = "remain_amount")
    private Long remainAmount;

    @JsonIgnore
    @OneToOne(mappedBy = "debit", cascade = CascadeType.ALL)
    private Credit credit;

    @Builder
    public Debit(Long id, int user_id, int amount, int prd_id, String txnId, int game_id, String table_id ) {
        this.id = id;
        this.user_id = user_id;
        this.amount = amount;
        this.prd_id = prd_id;
        this.txnId = txnId;
        this.game_id = game_id;
        this.table_id = table_id;
    }
}
