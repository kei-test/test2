package GInternational.server.api.repository;

import GInternational.server.api.entity.Messages;
import GInternational.server.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepositoryCustom {


    Page<Messages> getUserReceivedMessages(User receiver, boolean isRead, Pageable pageable);

    Long countByUserMessages(User receiver, boolean isRead);


    Page<Messages> getAdminSenderMessages(User sender, boolean deletedBySender, boolean isRead, Pageable pageable);



    //모든 쪽지 조회
    Page<Messages> findByReceiverMessages(User receiver, boolean deletedByReceiver, Pageable pageable);

    Page<Messages> findByReceiverMessagesAndType(User receiver, String type, boolean deletedByReceiver, Pageable pageable);

    Long countByReceiverMessages(User receiver, boolean deletedByReceiver);

    Long countByReceiverMessagesAndType(User receiver, String type, boolean deletedByReceiver);


    Page<Messages> findBySenderMessages(User sender,boolean deletedBySender,Pageable pageable);
    Long countBySenderMessages(User sender,boolean deletedBySender);


}

