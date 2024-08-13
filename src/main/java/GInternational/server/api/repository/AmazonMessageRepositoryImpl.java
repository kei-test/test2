package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonMessages;
import GInternational.server.api.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static GInternational.server.api.entity.QAmazonMessages.amazonMessages;

@RequiredArgsConstructor
public class AmazonMessageRepositoryImpl implements AmazonMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //optional : title 에 따른 검색 기능


    @Override
    public Page<AmazonMessages> getUserReceivedMessages(User receiver, boolean isRead, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        BooleanExpression condition = amazonMessages.receiver.eq(receiver)
                .and(amazonMessages.isRead.eq(isRead)
                        .and(amazonMessages.createdAt.between(startDate,endDate)));

        List<AmazonMessages> results = queryFactory.selectFrom(amazonMessages)
                .where(condition)
                .orderBy(amazonMessages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Long countByUserMessages(User receiver, boolean isRead) {
        return (long) queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.receiver.eq(receiver)
                        .and(amazonMessages.isRead.eq(isRead)))
                .fetch().size();
    }

    //[Fix] 발신자 쪽지 조회
    @Override
    public Page<AmazonMessages> getAdminSenderMessages(User sender, boolean isRead,LocalDateTime startDate, LocalDateTime endDate, boolean deletedBySender, Pageable pageable) {
        BooleanExpression condition = amazonMessages.sender.eq(sender)
                .and(amazonMessages.deletedBySender.eq(deletedBySender))
                .and(amazonMessages.isRead.eq(isRead)
                        .and(amazonMessages.createdAt.between(startDate,endDate)));

        List<AmazonMessages> results = queryFactory.selectFrom(amazonMessages)
                .where(condition)
                .orderBy(amazonMessages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.sender.eq(sender)
                        .and(amazonMessages.deletedBySender.eq(deletedBySender))
                        .and(amazonMessages.isRead.eq(isRead)))
                .fetch().size();

        return new PageImpl<>(results, pageable, totalElements);
    }

    @Override
    public Page<AmazonMessages> findByReceiverMessages(User receiver, boolean deletedByReceiver, Pageable pageable) {
        List<AmazonMessages> results = queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.receiver.id.eq(receiver.getId()).and(amazonMessages.deletedByReceiver.eq(deletedByReceiver)))
                .orderBy(amazonMessages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.receiver.id.eq(receiver.getId()).and(amazonMessages.deletedByReceiver.eq(deletedByReceiver)))
                .fetch().size();

        return new PageImpl<>(results, pageable, totalElements);
    }

    @Override
    public Page<AmazonMessages> findByReceiverMessagesAndType(User receiver,LocalDateTime startDate, LocalDateTime endDate, String type, boolean deletedByReceiver, Pageable pageable) {

        boolean readCondition = Objects.equals(type, "read");

        List<AmazonMessages> results = queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.receiver.id.eq(receiver.getId()).and(amazonMessages.deletedByReceiver.eq(deletedByReceiver))
                        .and(amazonMessages.isRead.eq(readCondition))
                        .and(amazonMessages.createdAt.between(startDate,endDate)))
                .orderBy(amazonMessages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.receiver.id.eq(receiver.getId()).and(amazonMessages.deletedByReceiver.eq(deletedByReceiver)).and(amazonMessages.isRead.eq(readCondition)))
                .fetch().size();

        return new PageImpl<>(results, pageable, totalElements);
    }

    @Override
    public Long countByReceiverMessages(User receiver, boolean deletedByReceiver) {
        return (long) queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.receiver.id.eq(receiver.getId()).and(amazonMessages.deletedByReceiver.eq(deletedByReceiver)))
                .fetch().size();
    }

    @Override
    public Long countByReceiverMessagesAndType(User receiver, String type, boolean deletedByReceiver) {
        boolean readCondition = Objects.equals(type, "read");

        return (long) queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.receiver.id.eq(receiver.getId()).and(amazonMessages.deletedByReceiver.eq(deletedByReceiver)).and(amazonMessages.isRead.eq(readCondition)))
                .fetch().size();
    }

    @Override
    public Page<AmazonMessages> findBySenderMessages(User sender, LocalDateTime startDate, LocalDateTime endDate, boolean deletedBySender, Pageable pageable) {
        List<AmazonMessages> results = queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.sender.id.eq(sender.getId()).and(amazonMessages.deletedBySender.eq(deletedBySender)).and(amazonMessages.createdAt.between(startDate,endDate)))
                .orderBy(amazonMessages.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Long countBySenderMessages(User sender, boolean deletedBySender) {
        return (long) queryFactory.selectFrom(amazonMessages)
                .where(amazonMessages.sender.id.eq(sender.getId()).and(amazonMessages.deletedBySender.eq(deletedBySender)))
                .fetch().size();
    }
}
