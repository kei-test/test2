package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "messages")
public class Messages extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;
    private String title;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "is_read")
    private boolean isRead;
    @Column(name = "is_popup")
    private boolean isPopup;
    @Column(name = "read_date")
    private LocalDateTime readDate;

    private String site = "test";

    @Column(name = "deleted_by_sender")
    private boolean deletedBySender;
    @Column(name = "deleted_by_receiver")
    private boolean deletedByReceiver;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User sender;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receiver_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User receiver;
}
