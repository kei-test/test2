package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonMessages;
import GInternational.server.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AmazonMessageRepositoryCustom {


    Page<AmazonMessages> getUserReceivedMessages(User receiver, boolean isRead, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Long countByUserMessages(User receiver, boolean isRead);

    Page<AmazonMessages> getAdminSenderMessages(User sender, boolean deletedBySender,LocalDateTime startDate, LocalDateTime endDate, boolean isRead, Pageable pageable);



    //모든 쪽지 조회
    Page<AmazonMessages> findByReceiverMessages(User receiver, boolean deletedByReceiver, Pageable pageable);

    Page<AmazonMessages> findByReceiverMessagesAndType(User receiver,LocalDateTime startDate, LocalDateTime endDate, String type, boolean deletedByReceiver, Pageable pageable);

    Long countByReceiverMessages(User receiver, boolean deletedByReceiver);

    Long countByReceiverMessagesAndType(User receiver, String type, boolean deletedByReceiver);


    Page<AmazonMessages> findBySenderMessages(User sender, LocalDateTime startDate, LocalDateTime endDate, boolean deletedBySender, Pageable pageable);
    Long countBySenderMessages(User sender,boolean deletedBySender);


}

