package GInternational.server.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserCalculateResultDTO {

    private List<UserCalculateDTO> levelCalculations;
    private UserCalculateTotalDTO totalCalculation;

    public UserCalculateResultDTO(List<UserCalculateDTO> levelCalculations, UserCalculateTotalDTO totalCalculation) {
        this.levelCalculations = levelCalculations;
        this.totalCalculation = totalCalculation;
    }
}