package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "dummy_jack_pot")
public class DummyJackPot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dummy_jack_pot_id")
    private Long id;

    @Column(name = "username")
    private String username;
    @Column(name = "bet_amount")
    private String betAmount;
    @Column(name = "reward")
    private String reward;
    @Column(name = "reward_date")
    private LocalDateTime rewardDate;
}
