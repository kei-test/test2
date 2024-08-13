package GInternational.server.api.entity;


import GInternational.server.api.vo.AppType;
import GInternational.server.api.vo.TransactionEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

//문자 본문과 함께 문자내용을 저장
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "auto_transaction")
public class AutoTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_transaction_id")
    private Long id;
    private String distributor;
    private long amount;
    private String bank;
    private String owner;
    private String phone;
    @Column(columnDefinition = "TEXT")
    private String message; //문자 본문 저장
    @Enumerated(EnumType.STRING)
    private AppType type;
    @Enumerated(EnumType.STRING)
    private TransactionEnum status;


    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;
    @Column(name = "deposit_time")
    private LocalDateTime depositTime;
    @Column(name = "reception_time")
    private LocalDateTime receptionTime;


    @JsonIgnore  // 순환 참조로 인해 설정
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
