package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity(name = "login_statistic")
public class LoginStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 결과의 고유 ID

    @Column(name = "visit_count")
    private long visitCount; // 로그인 횟수
    @Column(name = "recharged_count")
    private long rechargedCount; // 충전 횟수
    @Column(name = "exchange_count")
    private long exchangeCount; // 환전 횟수
    @Column(name = "create_user_count")
    private long createUserCount; // 회원가입 횟수

    @Column(name = "aas_id")
    private long aasId; // 베팅한 AAS 사용자 ID
    @Column(name = "debit_count")
    private long debitCount; // 베팅회원 카운트

    @CreatedDate
    @Column(updatable = false)
    private LocalDate date;
}