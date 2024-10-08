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

import javax.persistence.Column;
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

        record.setKakaoRegistered(user.isKakaoRegistered());
        record.setKakaoId(user.getKakaoId());
        record.setTelegramRegistered(user.isTelegramRegistered());
        record.setTelegramId(user.getTelegramId());
        record.setVirtualAccountEnabled(user.isVirtualAccountEnabled());
        record.setVirtualAccountOwnerName(user.getVirtualAccountOwnerName());
        record.setVirtualAccountNumber(user.getVirtualAccountNumber());
        record.setMemo1(user.getMemo1());
        record.setMemo2(user.getMemo2());
        record.setMemo3(user.getMemo3());
        record.setMemo4(user.getMemo4());
        record.setMemo5(user.getMemo5());
        record.setMemo6(user.getMemo6());
        record.setSmsReceipt(user.isSmsReceipt());
        record.setAmazonVisible(user.isAmazonVisible());
        record.setAccountVisible(user.isAccountVisible());
        record.setCanRecommend(user.isCanRecommend());
        record.setCanPost(user.isCanPost());
        record.setCanBonus(user.isCanBonus());
        record.setExp(user.getExp());
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
        currentState.put("username", user.getUsername());
        currentState.put("nickname", user.getNickname());
        currentState.put("password", user.getPassword());
        currentState.put("phone", user.getPhone());
        currentState.put("bankName", user.getWallet().getBankName());
        currentState.put("number", user.getWallet().getNumber() != null ? String.valueOf(user.getWallet().getNumber()) : null);
        currentState.put("email", user.getEmail());
        currentState.put("ownerName", user.getWallet().getOwnerName());
        currentState.put("lv", String.valueOf(user.getLv()));

        UserGubunEnum gubun = user.getUserGubunEnum();
        currentState.put("user_gubun", gubun != null ? gubun.name() : null); // user_gubun이 null이 아니면 이름을, null이면 null 사용

        currentState.put("referredBy", user.getReferredBy());
        currentState.put("distributor", user.getDistributor());
        currentState.put("store", user.getStore());
        currentState.put("isAmazonUser", String.valueOf(user.isAmazonUser()));
        currentState.put("isDstUser", String.valueOf(user.isDstUser()));
        currentState.put("isKakaoRegistered", String.valueOf(user.isKakaoRegistered()));
        currentState.put("kakaoId", user.getKakaoId());
        currentState.put("isTelegramRegistered", String.valueOf(user.isTelegramRegistered()));
        currentState.put("telegramId", user.getTelegramId());
        currentState.put("isVirtualAccountEnabled", String.valueOf(user.isVirtualAccountEnabled()));
        currentState.put("virtualAccountOwnerName", user.getVirtualAccountOwnerName());
        currentState.put("virtualAccountNumber", user.getVirtualAccountNumber());
        currentState.put("memo1", user.getMemo1());
        currentState.put("memo2", user.getMemo2());
        currentState.put("memo3", user.getMemo3());
        currentState.put("memo4", user.getMemo4());
        currentState.put("memo5", user.getMemo5());
        currentState.put("memo6", user.getMemo6());
        currentState.put("isSmsReceipt", String.valueOf(user.isSmsReceipt()));
        currentState.put("isAmazonVisible", String.valueOf(user.isAmazonVisible()));
        currentState.put("isAccountVisible", String.valueOf(user.isAccountVisible()));
        currentState.put("isCanRecommend", String.valueOf(user.isCanRecommend()));
        currentState.put("isCanPost", String.valueOf(user.isCanPost()));
        currentState.put("isCanBonus", String.valueOf(user.isCanBonus()));
        currentState.put("exp", String.valueOf(user.getExp()));

        currentState.forEach((key, value) -> {
            String prevValue = prevState.get(key);
            // 이전 값이 null이었고 현재 값이 null이 아닌 경우 또는 이전 값과 현재 값이 다른 경우에만 저장
            if ((prevValue == null && value != null) || (prevValue != null && !prevValue.equals(String.valueOf(value)))) {
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
        prevState.put("bankName", user.getWallet() != null ? user.getWallet().getBankName() : null);
        prevState.put("number", user.getWallet() != null ? String.valueOf(user.getWallet().getNumber()) : null);
        prevState.put("email", user.getEmail());
        prevState.put("ownerName", user.getWallet() != null ? user.getWallet().getOwnerName() : null);
        prevState.put("lv", String.valueOf(user.getLv()));
        prevState.put("user_gubun", user.getUserGubunEnum() != null ? user.getUserGubunEnum().name() : null);
        prevState.put("referredBy", user.getReferredBy());
        prevState.put("distributor", user.getDistributor());
        prevState.put("store", user.getStore());
        prevState.put("isAmazonUser", String.valueOf(user.isAmazonUser()));
        prevState.put("isDstUser", String.valueOf(user.isDstUser()));
        prevState.put("isKakaoRegistered", String.valueOf(user.isKakaoRegistered()));
        prevState.put("kakaoId", user.getKakaoId());
        prevState.put("isTelegramRegistered", String.valueOf(user.isTelegramRegistered()));
        prevState.put("telegramId", user.getTelegramId());
        prevState.put("isVirtualAccountEnabled", String.valueOf(user.isVirtualAccountEnabled()));
        prevState.put("virtualAccountOwnerName", user.getVirtualAccountOwnerName());
        prevState.put("virtualAccountNumber", user.getVirtualAccountNumber());
        prevState.put("memo1", user.getMemo1());
        prevState.put("memo2", user.getMemo2());
        prevState.put("memo3", user.getMemo3());
        prevState.put("memo4", user.getMemo4());
        prevState.put("memo5", user.getMemo5());
        prevState.put("memo6", user.getMemo6());
        prevState.put("isSmsReceipt", String.valueOf(user.isSmsReceipt()));
        prevState.put("isAmazonVisible", String.valueOf(user.isAmazonVisible()));
        prevState.put("isAccountVisible", String.valueOf(user.isAccountVisible()));
        prevState.put("isCanRecommend", String.valueOf(user.isCanRecommend()));
        prevState.put("isCanPost", String.valueOf(user.isCanPost()));
        prevState.put("isCanBonus", String.valueOf(user.isCanBonus()));
        prevState.put("exp", String.valueOf(user.getExp()));

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
