package GInternational.server.api.entity;


import GInternational.server.api.vo.AppStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;
    private String bank;
    private String owner;
    private String number;
    private String site;
    @Enumerated(EnumType.STRING)
    private AppStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;


    @Builder
    public Account(Long id, String bank, String owner, String number, String site, AppStatus status, LocalDateTime createdAt, LocalDateTime processedAt, User user) {
        this.id = id;
        this.bank = bank;
        this.owner = owner;
        this.number = number;
        this.site = site;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.user = user;
    }
}
