package GInternational.server.l_sport.info.dto.inplay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
public class InPlayPeriodResponseDTO {

    private String period1;
    private String period1Home;
    private String period1Away;
    private String period2;
    private String period2Home;
    private String period2Away;
    private String period3;
    private String period3Home;
    private String period3Away;
    private String period4;
    private String period4Home;
    private String period4Away;
    private String period5;
    private String period5Home;
    private String period5Away;
    private String period6;
    private String period6Home;
    private String period6Away;
    private String period7;
    private String period7Home;
    private String period7Away;
    private String period8;
    private String period8Home;
    private String period8Away;
    private String period9;
    private String period9Home;
    private String period9Away;
    private String period10;
    private String period10Home;
    private String period10Away;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InPlayPeriodResponseDTO inPlayPeriodResponseDTO = (InPlayPeriodResponseDTO) o;
        return Objects.equals(period1, inPlayPeriodResponseDTO.period1) &&
                Objects.equals(period1Home, inPlayPeriodResponseDTO.period1Home) &&
                Objects.equals(period1Away, inPlayPeriodResponseDTO.period1Away) &&

                Objects.equals(period2, inPlayPeriodResponseDTO.period2) &&
                Objects.equals(period2Home, inPlayPeriodResponseDTO.period2Home) &&
                Objects.equals(period2Away, inPlayPeriodResponseDTO.period2Away) &&

                Objects.equals(period3, inPlayPeriodResponseDTO.period3) &&
                Objects.equals(period3Home, inPlayPeriodResponseDTO.period3Home) &&
                Objects.equals(period3Away, inPlayPeriodResponseDTO.period3Away) &&

                Objects.equals(period4, inPlayPeriodResponseDTO.period4) &&
                Objects.equals(period4Home, inPlayPeriodResponseDTO.period4Home) &&
                Objects.equals(period4Away, inPlayPeriodResponseDTO.period4Away) &&

                Objects.equals(period5, inPlayPeriodResponseDTO.period5) &&
                Objects.equals(period5Home, inPlayPeriodResponseDTO.period5Home) &&
                Objects.equals(period5Away, inPlayPeriodResponseDTO.period5Away) &&

                Objects.equals(period6, inPlayPeriodResponseDTO.period6) &&
                Objects.equals(period6Home, inPlayPeriodResponseDTO.period6Home) &&
                Objects.equals(period6Away, inPlayPeriodResponseDTO.period6Away) &&

                Objects.equals(period7, inPlayPeriodResponseDTO.period7) &&
                Objects.equals(period7Home, inPlayPeriodResponseDTO.period7Home) &&
                Objects.equals(period7Away, inPlayPeriodResponseDTO.period7Away) &&

                Objects.equals(period8, inPlayPeriodResponseDTO.period8) &&
                Objects.equals(period8Home, inPlayPeriodResponseDTO.period8Home) &&
                Objects.equals(period8Away, inPlayPeriodResponseDTO.period8Away) &&

                Objects.equals(period9, inPlayPeriodResponseDTO.period9) &&
                Objects.equals(period9Home, inPlayPeriodResponseDTO.period9Home) &&
                Objects.equals(period9Away, inPlayPeriodResponseDTO.period9Away) &&

                Objects.equals(period10, inPlayPeriodResponseDTO.period10) &&
                Objects.equals(period10Home, inPlayPeriodResponseDTO.period10Home) &&
                Objects.equals(period10Away, inPlayPeriodResponseDTO.period10Away);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                period1,
                period1Home,
                period1Away,
                period2,
                period2Home,
                period2Away,
                period3,
                period3Home,
                period3Away,
                period4,
                period4Home,
                period4Away,
                period5,
                period5Home,
                period5Away,
                period6,
                period6Home,
                period6Away,
                period7,
                period7Home,
                period7Away,
                period8,
                period8Home,
                period8Away,
                period9,
                period9Home,
                period9Away,
                period10,
                period10Home,
                period10Away
        );
    }
}
