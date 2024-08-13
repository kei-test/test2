package GInternational.server.api.entity;

import GInternational.server.api.vo.PointTransactionDescriptionEnum;
import GInternational.server.api.vo.PointTransactionTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Setter
@Getter
@Entity(name = "point_transaction")
public class PointTransaction {


    //매퍼로 인해 Entity와 DTO의 필드명이 같아야함
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_transaction_id")
    private Long id;
    @Column(name = "used_point")
    private long usedPoint;  //전환에 사용한 포인트
    @Column(name = "remaining_point")
    private long remainingPoint;  //전환 후 보유 포인트
    private String ip;


    @Enumerated(EnumType.STRING)
    private PointTransactionDescriptionEnum description;  //  ex: 포인트 적립

    @Enumerated(EnumType.STRING)
    private PointTransactionTypeEnum type;  //  적립/차감 여부

    private String note;   //비고


    @CreatedDate
    @Column(name = "processed_at", updatable = false, nullable = false)
    private LocalDateTime processedAt;  // 처리 시간

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder
    public PointTransaction(Long id, long usedPoint, long remainingPoint, String ip, PointTransactionDescriptionEnum description, PointTransactionTypeEnum type, String note, LocalDateTime processedAt, User user) {
        this.id = id;
        this.usedPoint = usedPoint;
        this.remainingPoint = remainingPoint;
        this.ip = ip;
        this.description = description;
        this.type = type;
        this.note = note;
        this.processedAt = processedAt;
        this.user = user;
    }


    public PointTransaction(Long id, int usedPoint, int remainingPoint, PointTransactionDescriptionEnum description, LocalDateTime processedAt) {
        this.id = id;
        this.usedPoint = usedPoint;
        this.remainingPoint = remainingPoint;
        this.description = description;
        this.processedAt = processedAt;
    }
}
