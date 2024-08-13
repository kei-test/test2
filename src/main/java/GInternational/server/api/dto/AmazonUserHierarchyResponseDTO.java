package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AmazonUserHierarchyResponseDTO {
    private String bigHeadOfficeInfo;
    private String headOfficeInfo;
    private String deputyHeadOfficeInfo;
    private String distributorInfo;
}