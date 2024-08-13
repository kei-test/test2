package GInternational.server.kplay.credit.entity;

import GInternational.server.common.BaseEntity;
import GInternational.server.kplay.debit.entity.Debit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "credit")
public class Credit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_id")
    private Long id;
    private int user_id; // AAS 사용자 ID
    private int amount; // 당첨금액
    private int prd_id; // 제품 ID
    @Column(name = "txn_id", unique = true)
    private String txnId; // 고유 트랜잭션 ID
    private int game_id; // 게임 ID
    private String table_id; // 테이블 ID
    private int is_cancel;
    @Column(name = "remain_amount")
    private int remainAmount;

    @JsonIgnore  // 순환 참조로 인해 설정
    @OneToOne
    @JoinColumn(name = "debit_id", unique = true)
    private Debit debit;
}
