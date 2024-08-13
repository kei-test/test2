package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import GInternational.server.api.entity.User;
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
@Entity(name = "amazon_dedicated_account")
public class AmazonDedicatedAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dedicated_account_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @CollectionTable(name = "dedicated_account_lv", joinColumns = @JoinColumn(name = "dedicated_account_id"))
    @Column(name = "lv")
    private Set<Integer> lv = new HashSet<>(); // 여러 레벨 지정

    @Column(name = "bank_name")
    private String bankName; // 은행명

    @Column(name = "owner_name")
    private String ownerName; // 예금주

    private Long number; // 계좌번호
}
