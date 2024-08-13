package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.AppleResultDTO;
import GInternational.server.api.dto.AppleSettingDTO;
import GInternational.server.api.dto.AppleSettingsUpdateAllDTO;
import GInternational.server.api.dto.AppleSettingUpdateDTO;
import GInternational.server.api.entity.AppleResults;
import GInternational.server.api.entity.AppleSettings;
import GInternational.server.api.mapper.AppleResultMapper;
import GInternational.server.api.mapper.AppleSettingMapper;
import GInternational.server.api.repository.AppleResultRepository;
import GInternational.server.api.repository.AppleSettingRepository;
import GInternational.server.api.vo.PaymentStatusEnum;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AppleService {

    private final AppleSettingRepository appleSettingRepository;
    private final AppleResultRepository appleResultRepository;
    private final AppleSettingMapper appleSettingMapper;
    private final AppleResultMapper appleResultMapper;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final PointLogService pointLogService;

    /**
     * 사과줍기의 모든 결과를 반환하는 메서드.
     *
     * @return 모든 사과줍기 결과의 DTO 목록
     */
    public List<AppleResultDTO> getAllAppleResults(PrincipalDetails principalDetails) {
        List<AppleResults> appleResults = appleResultRepository.findAll();
        return appleResults.stream()
                .map(result -> {
                    AppleResultDTO dto = appleResultMapper.toDto(result);
                    if (result.getUserId() != null) {
                        dto.setUserId(result.getUserId().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 사과줍기 설정 조회 메서드.
     * @return 모든 사과줍기 설정의 리스트.
     */
    public List<AppleSettingDTO> getAllAppleSettings(PrincipalDetails principalDetails) {
        return getAllSettingsFromDB().stream()
                .map(appleSettingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 사과줍기 설정 초기화 메서드. 데이터 베이스에 설정된 AppleSetting이 없을 때 1~8 순번에 해당하는 초기값 생성.
     * @param principalDetails 인증된 사용자의 세부 정보.
     */
    public void initializeAppleSettings(PrincipalDetails principalDetails) {
        List<AppleSettings> initialSettings = IntStream.range(0, 8)
                .mapToObj(i -> AppleSettings.builder()
                        .rewardName("보상명 입력")
                        .rewardValue("0")
                        .rewardDescription("상품 세부명 입력")
                        .maxQuantity(0)
                        .originalMaxQuantity(0)
                        .probability(0.0)
                        .build())
                .collect(Collectors.toList());
        appleSettingRepository.saveAll(initialSettings);
    }

    /**
     * 데이터베이스로부터 모든 사과줍기 설정을 가져오는 메서드.
     * @return 모든 사과줍기 설정의 리스트.
     */
    private List<AppleSettings> getAllSettingsFromDB() {
        return appleSettingRepository.findAll();
    }


    /**
     * 순번 1~8까지의 사과줍기 설정값을 수정하는 메서드.
     * @param updateAppleSettingsDTO 수정할 사과줍기 설정의 정보.
     */
    @AuditLogService.Audit("사과줍기 설정 업데이트")
    public void updateAllAppleSettings(AppleSettingsUpdateAllDTO updateAppleSettingsDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        double totalProbability = updateAppleSettingsDTO.getSettings().stream()
                .mapToDouble(AppleSettingUpdateDTO::getProbability)
                .sum();

        if (totalProbability > 100) {
            throw new RestControllerException(ExceptionCode.APPLE_PROBABILITY_EXCEEDED, "사과줍기 확률 100% 초과.");
        }

        List<AppleSettings> modifiedSettings = new ArrayList<>();

        for (AppleSettingUpdateDTO updateSetting : updateAppleSettingsDTO.getSettings()) {
            AppleSettings appleSettings = appleSettingRepository.findById(updateSetting.getId()).orElseThrow(()
                    -> new RestControllerException(ExceptionCode.APPLE_SETTING_NOT_FOUND, "사과줍기 설정을 찾을 수 없습니다."));

            appleSettings.setRewardName(updateSetting.getRewardName());
            appleSettings.setRewardValue(updateSetting.getRewardValue());
            appleSettings.setRewardDescription(updateSetting.getRewardDescription());
            appleSettings.setMaxQuantity(updateSetting.getMaxQuantity());
            appleSettings.setOriginalMaxQuantity(updateSetting.getMaxQuantity());
            appleSettings.setProbability(updateSetting.getProbability());

            modifiedSettings.add(appleSettings);
        }

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("사과줍기 설정 업데이트");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        appleSettingRepository.saveAll(modifiedSettings);
    }

    /**
     * 사과줍기 실행 메서드.
     * @param userId 사용자의 아이디.
     * @param principalDetails 현재 인증된 사용자의 세부 정보.
     * @return 사과줍기 결과에 대한 정보.
     */
    public AppleResultDTO pickUpApple(Long userId, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

        if (user.getAppleCount() <= 0) {
            throw new RestControllerException(ExceptionCode.APPLE_ALREADY_PLAYED, "사과줍기 게임을 이미 했습니다");
        }

        String clientIp = request.getRemoteAddr();

        List<AppleSettings> allSettings = getAllSettingsFromDB();

        double totalProbability = allSettings.stream()
                .mapToDouble(AppleSettings::getProbability)
                .sum();

        AppleSettings selectedSetting = decideAppleResult(allSettings, totalProbability);

        if (user.getAppleCount() > 0) {
            user.decreaseAppleCount();
        }

        AppleResultDTO resultDTO = updateUserWalletAndAppleResult(user, selectedSetting, clientIp);
        return resultDTO;
    }

    /**
     * 사과줍기 결과를 결정하는 메서드.
     * @param allSettings 모든 사과줍기 설정의 리스트.
     * @param totalProbability 모든 사과줍기 확률의 총합.
     * @return 선택된 사과줍기 설정.
     */

    public AppleSettings decideAppleResult(List<AppleSettings> allSettings, double totalProbability) {
        AppleSettings failSetting = allSettings.stream()
                .filter(s -> "꽝".equals(s.getRewardName()))
                .findFirst()
                .orElseThrow(() -> new RestControllerException(ExceptionCode.APPLE_SETTING_NOT_FOUND, "사과줍기 설정을 찾을 수 없습니다."));
        failSetting.setProbability(100 - totalProbability);

        double randomNumber = ThreadLocalRandom.current().nextDouble() * totalProbability;
        double cumulativeProbability = 0;

        for (AppleSettings setting : allSettings) {
            cumulativeProbability += setting.getProbability();
            if (randomNumber <= cumulativeProbability && setting.getMaxQuantity() > 0) {
                setting.setMaxQuantity(setting.getMaxQuantity() - 1);
                appleSettingRepository.save(setting);
                return setting;
            }
        }
        throw new RestControllerException(ExceptionCode.APPLE_FAILED, "사과줍기 게임 실행 실패.");
    }

    /**
     * 사용자의 지갑과 사과줍기 결과를 업데이트하는 메서드.
     * @param user 결과를 업데이트할 사용자.
     * @param selectedSetting 선택된 사과줍기 설정.
     * @param clientIp 클라이언트의 IP 주소.
     * @return 업데이트된 사과줍기 결과에 대한 정보.
     */
    public AppleResultDTO updateUserWalletAndAppleResult(User user, AppleSettings selectedSetting, String clientIp) {
        AppleResultDTO resultDTO = new AppleResultDTO();
        String rewardValue = selectedSetting.getRewardValue();

        resultDTO.setRewardName(selectedSetting.getRewardName());
        resultDTO.setRewardDescription(selectedSetting.getRewardDescription());
        resultDTO.setUserId(user.getId());

        if (isNumeric(rewardValue)) {
            long rewardPoints = Integer.parseInt(rewardValue);
            if (rewardPoints == 0) {
                resultDTO.setStatus(PaymentStatusEnum.꽝);
            } else {
                resultDTO.setStatus(PaymentStatusEnum.지급완료);
                Wallet wallet = walletRepository.findByUser(user)
                        .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));
                wallet.setPoint(wallet.getPoint() + rewardPoints);
                walletRepository.save(wallet);

                pointLogService.recordPointLog(user.getId(), rewardPoints, PointLogCategoryEnum.사과줍기, clientIp, "");
            }
        } else {
            resultDTO.setStatus(PaymentStatusEnum.지급대기중);
        }

        resultDTO.setRewardValue(rewardValue);

        AppleResults appleResults = new AppleResults();
        appleResults.setUserId(user);
        appleResults.setRewardValue(resultDTO.getRewardValue());
        appleResults.setRewardDescription(resultDTO.getRewardDescription());
        appleResults.setRewardName(resultDTO.getRewardName());
        appleResults.setStatus(resultDTO.getStatus());
        appleResultRepository.save(appleResults);

        user.decreaseAppleCount();
        userRepository.save(user);

        return resultDTO;
    }

    /**
     * 문자열이 숫자인지 검사하는 메서드.
     * @param str 검사할 문자열.
     * @return 문자열이 숫자면 true, 그렇지 않으면 false.
     */
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 사과줍기 결과의 상태를 업데이트하는 메서드.
     *
     * @param resultId       업데이트할 사과줍기 결과의 ID
     * @param newStatus        새로 설정할 상태 값 (PaymentStatus enum 타입)
     * @param principalDetails 현재 사용자의 권한 정보
     */
    public void updateAppleStatus(Long resultId, PaymentStatusEnum newStatus, PrincipalDetails principalDetails) {
        AppleResults appleResults = appleResultRepository.findById(resultId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.APPLE_RESULT_NOT_FOUND, "사과줍기 결과를 찾을 수 없습니다."));
        appleResults.setStatus(newStatus);
        appleResultRepository.save(appleResults);
    }

    /**
     * PaymentStatus 기준으로 사과줍기 결과 조회.
     *
     * @param status            필터링할 사과줍기 결과의 상태. (PaymentStatus enum 타입)
     * @param principalDetails  현재 인증된 사용자의 보안 주체 세부 정보.
     * @return 주어진 상태와 일치하는 AppleResultDTO 객체의 리스트를 반환.
     * @throws RestControllerException 제공된 상태가 유효하지 않은 경우 예외를 던집니다.
     */
    public List<AppleResultDTO> findAppleResultsByStatus(PaymentStatusEnum status, PrincipalDetails principalDetails) {
        List<AppleResults> appleResults = appleResultRepository.findByStatus(status);
        return appleResults.stream()
                .map(result -> {
                    AppleResultDTO dto = appleResultMapper.toDto(result);
                    if (result.getUserId() != null) {
                        dto.setUserId(result.getUserId().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 매일 00:00 시에 실행
     * 사과줍기 카운트 초기화.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetAppleData() {
        List<AppleSettings> allSettings = getAllSettingsFromDB();
        for (AppleSettings setting : allSettings) {
            setting.setMaxQuantity(setting.getOriginalMaxQuantity());
            appleSettingRepository.save(setting);
        }

        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.setAppleCount(1);
        }
        userRepository.saveAll(allUsers);
    }
}
