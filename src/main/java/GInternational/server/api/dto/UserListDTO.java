package GInternational.server.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class UserListDTO {

    private Long id;
    private String username;  // 로그인 시 아이디
    private String password;  // 로그인 시 비밀 번호
    private String nickname;  // 닉네임
    private String name;      // 실명
    private String phone;     // 휴대폰 번호
    private String joinRoute; // 가입 경로
    private boolean enabled;  // 회원 활성화 여부
    private boolean withdraw; // 회원 삭제 여부
    private String role;      // USER or ADMIN or MANAGER
    private int lv;           // 회원 등급 Lv.01 ~ Lv.10
    private LocalDateTime last_password_changed;  //마지막 비밀 번호가 변경된 시각
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastVisit;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;


//    @QueryProjection
//    public UserListDTO(Long id, String username, String nickname, String phone, String role, String lv) {
//        this.id = id;
//        this.username = username;
//        this.nickname = nickname;
//        this.name = name;
//        this.phone = phone;
//        this.role = role;
//        this.lv = lv;
//
//    }
}
