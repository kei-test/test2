package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BetLogFilterDTO {
    private Optional<String> username;
    private Optional<String> nickname;
    private Optional<String> gameName;
    private Optional<String> gameType; // "라이브 카지노" 또는 "슬롯"
    private Optional<String> result; // "전체", "당첨"
}
