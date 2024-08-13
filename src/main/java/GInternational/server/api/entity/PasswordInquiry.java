package GInternational.server.api.entity;

import GInternational.server.api.vo.PasswordInquiryStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "password_inquiry")
public class PasswordInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "password_inquiry_id")
    private Long id;

    private String username;
    @Column(name = "owner_name")
    private String ownerName;
    private String ip;
    private String phone;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private PasswordInquiryStatusEnum status;
    @Column(name = "admin_memo")
    private String adminMemo;
}
