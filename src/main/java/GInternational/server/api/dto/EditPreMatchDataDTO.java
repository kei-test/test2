package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EditPreMatchDataDTO {

    private List<EditPreMatchDataList> preMatchDataList;

}
