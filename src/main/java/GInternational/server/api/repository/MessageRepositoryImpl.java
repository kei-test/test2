package GInternational.server.api.repository;

import GInternational.server.api.entity.Messages;
import GInternational.server.api.entity.User;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

import static GInternational.server.api.entity.QMessages.*;


@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    //optional : title 에 따른 검색 기능


    @Override
    public Page<Messages> getUserReceivedMessages(User receiver, boolean isRead, Pageable pageable) {
        BooleanExpression condition = messages.receiver.eq(receiver)
                .and(messages.isRead.eq(isRead));

        List<Messages> results = queryFactory.selectFrom(messages)
                .where(condition)
                .orderBy(messages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Long countByUserMessages(User receiver, boolean isRead) {
        return (long) queryFactory.selectFrom(messages)
                .where(messages.receiver.eq(receiver)
                        .and(messages.isRead.eq(isRead)))
                .fetch().size();
    }


    //[Fix] 발신자 쪽지 조회
    @Override
    public Page<Messages> getAdminSenderMessages(User sender, boolean isRead, boolean deletedBySender, Pageable pageable) {
        BooleanExpression condition = messages.sender.eq(sender)
                .and(messages.deletedBySender.eq(deletedBySender))
                .and(messages.isRead.eq(isRead));

        List<Messages> results = queryFactory.selectFrom(messages)
                .where(condition)
                .orderBy(messages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(messages)
                .where(messages.sender.eq(sender)
                        .and(messages.deletedBySender.eq(deletedBySender))
                        .and(messages.isRead.eq(isRead)))
                .fetch().size();



        return new PageImpl<>(results, pageable, totalElements);
    }


    @Override
    public Page<Messages> findByReceiverMessages(User receiver, boolean deletedByReceiver, Pageable pageable) {
        List<Messages> results = queryFactory.selectFrom(messages)
                .where(messages.receiver.id.eq(receiver.getId()).and(messages.deletedByReceiver.eq(deletedByReceiver)))
                .orderBy(messages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(messages)
                .where(messages.receiver.id.eq(receiver.getId()).and(messages.deletedByReceiver.eq(deletedByReceiver)))
                .fetch().size();

        return new PageImpl<>(results, pageable, totalElements);
    }

    @Override
    public Page<Messages> findByReceiverMessagesAndType(User receiver, String type, boolean deletedByReceiver, Pageable pageable) {

        boolean readCondition = Objects.equals(type, "read");

        List<Messages> results = queryFactory.selectFrom(messages)
                .where(messages.receiver.id.eq(receiver.getId()).and(messages.deletedByReceiver.eq(deletedByReceiver)).and(messages.isRead.eq(readCondition)))
                .orderBy(messages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(messages)
                .where(messages.receiver.id.eq(receiver.getId()).and(messages.deletedByReceiver.eq(deletedByReceiver)).and(messages.isRead.eq(readCondition)))
                .fetch().size();

        return new PageImpl<>(results, pageable, totalElements);
    }

    @Override
    public Long countByReceiverMessages(User receiver, boolean deletedByReceiver) {
        return (long) queryFactory.selectFrom(messages)
                .where(messages.receiver.id.eq(receiver.getId()).and(messages.deletedByReceiver.eq(deletedByReceiver)))
                .fetch().size();
    }

    @Override
    public Long countByReceiverMessagesAndType(User receiver, String type, boolean deletedByReceiver) {
        boolean readCondition = Objects.equals(type, "read");

        return (long) queryFactory.selectFrom(messages)
                .where(messages.receiver.id.eq(receiver.getId()).and(messages.deletedByReceiver.eq(deletedByReceiver)).and(messages.isRead.eq(readCondition)))
                .fetch().size();
    }


    @Override
    public Page<Messages> findBySenderMessages(User sender, boolean deletedBySender, Pageable pageable) {
        List<Messages> results = queryFactory.selectFrom(messages)
                .where(messages.sender.id.eq(sender.getId()).and(messages.deletedBySender.eq(deletedBySender)))
                .orderBy(messages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Long countBySenderMessages(User sender, boolean deletedBySender) {
        return (long) queryFactory.selectFrom(messages)
                .where(messages.sender.id.eq(sender.getId()).and(messages.deletedBySender.eq(deletedBySender)))
                .fetch().size();
    }
}
