package GInternational.server.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MultiResponseDto<T> {
    private List<T> data;
    private PageInfo pageInfo;


    public MultiResponseDto(List<T> data, Page<?> page) {
        this.data = data;
        this.pageInfo = new PageInfo(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public MultiResponseDto(List<T> data) {
        this.data = data;
        this.pageInfo = null;
    }
}
