package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "dedicated_account")
public class DedicatedAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dedicated_account_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @ElementCollection
    @CollectionTable(name = "dedicated_account_levels", joinColumns = @JoinColumn(name = "dedicated_account_id"))
    @Column(name = "level")
    private Set<Integer> levels = new HashSet<>(); // 여러 레벨 지정

    @Column(name = "bank_name")
    private String bankName; // 은행명
    @Column(name = "owner_name")
    private String ownerName; // 예금주
    private Long number; // 계좌번호

    @Column(name = "is_active")
    private boolean isActive; // 활성화/비활성화 상태
}
