package GInternational.server.api.service;

import GInternational.server.api.dto.RechargeRequestDTO;
import GInternational.server.api.entity.ExpSetting;
import GInternational.server.api.entity.LevelUp;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.ExpSettingRepository;
import GInternational.server.api.repository.LevelUpRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.vo.LevelUpTransactionEnum;
import GInternational.server.api.vo.ReferredGubunEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LevelUpService {

    private final UserRepository userRepository;
    private final LevelUpRepository levelUpRepository;
    private final ExpSettingRepository expSettingRepository;

    public void applyLevelUp(PrincipalDetails principalDetails) {
        // 사용자 정보 조회
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow(
                () -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));

        int targetLv = user.getLv() + 1;

        // 대상 레벨의 ExpSetting 조회
        ExpSetting expSetting = expSettingRepository.findByLv(targetLv)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "대상 레벨의 설정 정보를 찾을 수 없습니다."));

        // 신청자의 현재 경험치가 대상 레벨의 경험치 범위에 해당하는지 검증
        if (user.getExp() < expSetting.getMinExp()) {
            throw new RestControllerException(ExceptionCode.INVALID_EXP_RANGE, "최소 경험치 범위에 미달하여 레벨업을 신청할 수 없습니다.");
        }

        // 레벨업 신청 정보 생성
        LevelUp levelUp = new LevelUp();
        levelUp.setReferredBy(user.getReferredBy());
        levelUp.setUserId(user.getId());
        levelUp.setUsername(user.getUsername());
        levelUp.setNickname(user.getNickname());
        levelUp.setApplyLv(user.getLv()); // 현재 사용자 레벨
        levelUp.setTargetLv(targetLv); // 신청 대상 레벨
        levelUp.setMemo(user.getLv() + "레벨에서 " + (user.getLv() + 1) + "레벨로 레벨업 신청"); // 신청 시 메모
        levelUp.setStatus(LevelUpTransactionEnum.WAITING);
        levelUp.setDepositTotal(user.getWallet().getDepositTotal());
        levelUp.setWithdrawTotal(user.getWallet().getWithdrawTotal());
        levelUp.setTotalSettlement(user.getWallet().getTotalSettlement());
        levelUp.setCreatedAt(LocalDateTime.now());

        if (user.isAmazonUser()) {
            levelUp.setReferredGubun(ReferredGubunEnum.추천코드); // 아마존 코드를 통한 가입이면 "추천코드"
        } else {
            levelUp.setReferredGubun(ReferredGubunEnum.추천인); // 아마존 코드가 아닌 일반 추천이면 "추천인"
        }

        levelUpRepository.save(levelUp);
    }

    public void approveLevelUp(Long levelUpId, PrincipalDetails principalDetails) {
        LevelUp levelUp = levelUpRepository.findById(levelUpId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "해당 레벨업 신청을 찾을 수 없습니다."));
        User user = userRepository.findByUsername(levelUp.getUsername());
        if (user == null) {
            throw new RestControllerException(ExceptionCode.USER_NOT_FOUND, "레벨업 신청과 연관된 사용자를 찾을 수 없습니다.");
        }
        user.setLv(levelUp.getTargetLv());
        levelUp.setStatus(LevelUpTransactionEnum.APPROVAL);
        levelUp.setProcessedAt(LocalDateTime.now());
        userRepository.save(user);
        levelUpRepository.save(levelUp);
    }

    public void cancelLevelUp(Long levelUpId, PrincipalDetails principalDetails) {
        LevelUp levelUp = levelUpRepository.findById(levelUpId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "해당 레벨업 신청을 찾을 수 없습니다."));
        levelUp.setStatus(LevelUpTransactionEnum.CANCELLATION);
        levelUp.setProcessedAt(LocalDateTime.now());
        levelUpRepository.save(levelUp);
    }

    // 상태와 검색 조건을 기반으로 LevelUp 목록 조회
    public List<LevelUp> searchLevelUps(LevelUpTransactionEnum status, String username, String nickname, String memo, Integer applyLv, PrincipalDetails principalDetails) {
        return levelUpRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (username != null && !username.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
            }

            if (nickname != null && !nickname.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + nickname + "%"));
            }

            if (memo != null && !memo.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("memo"), "%" + memo + "%"));
            }

            if (applyLv != null) {
                predicates.add(criteriaBuilder.equal(root.get("applyLv"), applyLv));
            }

            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
