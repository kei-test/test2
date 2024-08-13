package GInternational.server.api.service;

import GInternational.server.api.entity.User;
import GInternational.server.api.entity.UserUpdatedRecord;
import GInternational.server.api.repository.UserUpdatedRecordRepository;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class UserUpdatedRecordService {

    private final UserUpdatedRecordRepository userUpdatedRecordRepository;

    public UserUpdatedRecord createUserUpdatedRecord(Long userId, User user, String changedColumn, String beforeData, String afterData) {
        UserUpdatedRecord record = new UserUpdatedRecord();
        // 기본 정보 설정
        record.setUserId(userId);
        record.setUsername(user.getUsername());
        record.setNickname(user.getNickname());
        record.setPassword(user.getPassword());
        record.setPhone(user.getPhone());
        record.setBankName(user.getWallet().getBankName());
        record.setNumber(user.getWallet().getNumber());
        record.setEmail(user.getEmail());
        record.setOwnerName(user.getWallet().getOwnerName());
        record.setLv(user.getLv());
        record.setGubun(user.getUserGubunEnum());
        record.setReferredBy(user.getReferredBy());
        record.setDistributor(user.getDistributor());
        record.setStore(user.getStore());
        record.setIsAmazonUser(user.isAmazonUser());
        record.setIsDstUser(user.isDstUser());
        // 변경된 정보 설정
        record.setChangedColumn(changedColumn);
        record.setBeforeData(beforeData);
        record.setAfterData(afterData);
        record.setCreatedAt(LocalDateTime.now());

        return record;
    }

    public void recordChanges(Long userId, User user, Map<String, String> prevState) {
        Map<String, Object> currentState = new HashMap<>();
        // 현재 상태 맵핑
        currentState.put("username", Optional.ofNullable(user.getUsername()).orElse(""));
        currentState.put("nickname", Optional.ofNullable(user.getNickname()).orElse(""));
        currentState.put("password", Optional.ofNullable(user.getPassword()).orElse(""));
        currentState.put("phone", Optional.ofNullable(user.getPhone()).orElse(""));
        currentState.put("bankName", Optional.ofNullable(user.getWallet().getBankName()).orElse(""));
        currentState.put("number", Optional.of(user.getWallet().getNumber().toString()).orElse(""));
        currentState.put("email", Optional.ofNullable(user.getEmail()).orElse(""));
        currentState.put("ownerName", Optional.ofNullable(user.getWallet().getOwnerName()).orElse(""));
        currentState.put("lv", String.valueOf(user.getLv()));

        UserGubunEnum gubun = user.getUserGubunEnum();
        currentState.put("user_gubun", gubun != null ? gubun.name() : ""); // user_gubun이 null이 아니면 이름을, null이면 빈 문자열을 사용

        currentState.put("referredBy", Optional.ofNullable(user.getReferredBy()).orElse(""));
        currentState.put("distributor", Optional.ofNullable(user.getDistributor()).orElse(""));
        currentState.put("store", Optional.ofNullable(user.getStore()).orElse(""));
        currentState.put("isAmazonUser", String.valueOf(user.isAmazonUser()));
        currentState.put("isDstUser", String.valueOf(user.isDstUser()));

        currentState.forEach((key, value) -> {
            String prevValue = prevState.get(key);
            if ((prevValue == null && !String.valueOf(value).isEmpty()) || (prevValue != null && !prevValue.equals(value))) {
                UserUpdatedRecord record = createUserUpdatedRecord(userId, user, key, prevValue, String.valueOf(value));
                userUpdatedRecordRepository.save(record);
            }
        });
    }

    public Map<String, String> capturePreviousState(User user) {
        Map<String, String> prevState = new HashMap<>();
        prevState.put("username", user.getUsername());
        prevState.put("nickname", user.getNickname());
        prevState.put("password", user.getPassword());
        prevState.put("phone", user.getPhone());
        prevState.put("bankName", user.getWallet().getBankName());
        prevState.put("number", String.valueOf(user.getWallet().getNumber()));
        prevState.put("email", user.getEmail());
        prevState.put("ownerName", user.getWallet().getOwnerName());
        prevState.put("lv", String.valueOf(user.getLv()));
        prevState.put("user_gubun", String.valueOf(user.getUserGubunEnum()));
        prevState.put("referredBy", user.getReferredBy());
        prevState.put("distributor", user.getDistributor());
        prevState.put("store", user.getStore());
        prevState.put("isAmazonUser", String.valueOf(user.isAmazonUser()));
        prevState.put("isDstUser", String.valueOf(user.isDstUser()));
        return prevState;
    }

    public List<UserUpdatedRecord> findByConditions(LocalDate startDate, LocalDate endDate, String username,
                                                    String nickname, Boolean passwordChanged, String bankName,
                                                    String email, Integer lv, String referredBy, String distributor,
                                                    String userGubun, String store, Boolean isAmazonUser, Boolean isDstUser,
                                                    PrincipalDetails principalDetails) {
        Specification<UserUpdatedRecord> specification = createSpecification(startDate, endDate, username, nickname, passwordChanged, bankName, email, lv, referredBy, distributor, userGubun, store, isAmazonUser, isDstUser);
        return userUpdatedRecordRepository.findAll(specification);
    }

    public Specification<UserUpdatedRecord> createSpecification(LocalDate startDate, LocalDate endDate, String username,
                                                                String nickname, Boolean passwordChanged, String bankName,
                                                                String email, Integer lv, String referredBy, String distributor,
                                                                String userGubun, String store, Boolean isAmazonUser, Boolean isDstUser) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 날짜 조건
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("createdAt"), startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()));
            }

            // 비밀번호 변경 이력 조회
            if (Boolean.TRUE.equals(passwordChanged)) {
                predicates.add(cb.equal(root.get("changedColumn"), "password"));
            }

            // 다른 조건들
            if (username != null) predicates.add(cb.equal(root.get("username"), username));
            if (nickname != null) predicates.add(cb.equal(root.get("nickname"), nickname));
            if (bankName != null) predicates.add(cb.equal(root.get("bankName"), bankName));
            if (email != null) predicates.add(cb.equal(root.get("email"), email));
            if (lv != null) predicates.add(cb.equal(root.get("lv"), lv));
            if (referredBy != null) predicates.add(cb.equal(root.get("referredBy"), referredBy));
            if (distributor != null) predicates.add(cb.equal(root.get("distributor"), distributor));
            if (userGubun != null) predicates.add(cb.equal(root.get("gubun"), UserGubunEnum.valueOf(userGubun)));
            if (store != null) predicates.add(cb.equal(root.get("store"), store));
            if (isAmazonUser != null) predicates.add(cb.equal(root.get("isAmazonUser"), isAmazonUser));
            if (isDstUser != null) predicates.add(cb.equal(root.get("isDstUser"), isDstUser));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
