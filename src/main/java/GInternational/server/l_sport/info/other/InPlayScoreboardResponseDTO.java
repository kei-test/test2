//package GInternational.server.l_sport.lsports.info.dto.inplay;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.List;
//import java.util.Objects;
//
//@NoArgsConstructor
//@Setter
//@Getter
//public class InPlayScoreboardResponseDTO {
//
//    private Long scoreboardStatus;
//    private Long scoreboardCurrentPeriod;
//    private String scoreboardTime;
//    private String scoreboardHomeResultPosition;
//    private String scoreboardHomeResultValue;
//    private String scoreboardAwayResultPosition;
//    private String scoreboardAwayResultValue;
//    private List<InPlayPeriodResponseDTO> periods;
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        InPlayScoreboardResponseDTO inPlayScoreboardResponseDTO = (InPlayScoreboardResponseDTO) o;
//        return Objects.equals(scoreboardStatus, inPlayScoreboardResponseDTO.scoreboardStatus) &&
//                Objects.equals(scoreboardCurrentPeriod, inPlayScoreboardResponseDTO.scoreboardCurrentPeriod) &&
//                Objects.equals(scoreboardTime, inPlayScoreboardResponseDTO.scoreboardTime) &&
//                Objects.equals(scoreboardHomeResultPosition, inPlayScoreboardResponseDTO.scoreboardHomeResultPosition) &&
//                Objects.equals(scoreboardHomeResultValue, inPlayScoreboardResponseDTO.scoreboardHomeResultValue) &&
//                Objects.equals(scoreboardAwayResultPosition, inPlayScoreboardResponseDTO.scoreboardAwayResultPosition) &&
//                Objects.equals(scoreboardAwayResultValue, inPlayScoreboardResponseDTO.scoreboardAwayResultValue) &&
//                Objects.equals(periods, inPlayScoreboardResponseDTO.periods);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(
//                scoreboardStatus,
//                scoreboardCurrentPeriod,
//                scoreboardTime,
//                scoreboardHomeResultPosition,
//                scoreboardHomeResultValue,
//                scoreboardAwayResultPosition,
//                scoreboardAwayResultValue,
//                periods
//        );
//    }
//}
