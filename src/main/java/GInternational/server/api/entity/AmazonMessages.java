package GInternational.server.api.entity;


import GInternational.server.common.BaseEntity;
import GInternational.server.api.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "amazon_messages")
public class AmazonMessages extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amazon_message_id")
    private Long id;
    private String title;
    private String content;

    @Column(name = "is_read")
    private boolean isRead;

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
