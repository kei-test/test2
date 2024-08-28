package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "dummy_exchange")
public class DummyExchange extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dummy_exchange_id")
    private Long id;

    @Column(name = "username")
    private String username;
    @Column(name = "exchange_amount")
    private String exchangeAmount;
    @Column(name = "exchange_time")
    private LocalTime exchangeTime;
}
