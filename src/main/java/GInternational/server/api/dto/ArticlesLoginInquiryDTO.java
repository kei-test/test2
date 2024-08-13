package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticlesLoginInquiryDTO {
    private Long id; // 게시물 ID
    private String username; // 사용자 아이디
    private String ownerName; // 예금주 이름
    private String phone; // 핸드폰 번호
    private String memo; // 메모
}
