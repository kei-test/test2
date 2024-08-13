package GInternational.server.api.service;

import GInternational.server.api.controller.RouletteController;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.RouletteSettingDTO;
import GInternational.server.api.dto.RouletteSpinResultDTO;
import GInternational.server.api.dto.RouletteSettingsUpdateAllDTO;
import GInternational.server.api.dto.RouletteSettingUpdateDTO;
import GInternational.server.api.entity.RouletteResults;
import GInternational.server.api.entity.RouletteSettings;
import GInternational.server.api.mapper.RouletteResultMapper;
import GInternational.server.api.mapper.RouletteSettingMapper;
import GInternational.server.api.repository.RouletteResultRepository;
import GInternational.server.api.repository.RouletteSettingRepository;
import GInternational.server.api.vo.PaymentStatusEnum;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class RouletteService {

    private final RouletteSettingRepository rouletteSettingRepository;
    private final RouletteResultRepository rouletteResultRepository;
    private final PointLogService pointLogService;
    private final RouletteSettingMapper rouletteSettingMapper;
    private final RouletteResultMapper rouletteResultMapper;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(RouletteService.class);



    public List<RouletteSpinResultDTO> findRouletteResultsByUser(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

        List<RouletteResults> rouletteResults = rouletteResultRepository.findByUserId(user);
        return rouletteResults.stream()
                .map(result -> {
                    RouletteSpinResultDTO dto = rouletteResultMapper.toDto(result);
                    if (result.getUserId() != null) {
                        dto.setUserId(result.getUserId().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    /**
     * 모든 룰렛 스핀 결과를 조회하여 반환하는 메서드.
     *
     * @return 모든 룰렛 스핀 결과의 DTO 목록
     */
    public List<RouletteSpinResultDTO> getAllRouletteSpinResults(PrincipalDetails principalDetails) {
        List<RouletteResults> rouletteResults = rouletteResultRepository.findAll();
        return rouletteResults.stream()
                .map(result -> {
                    RouletteSpinResultDTO dto = rouletteResultMapper.toDto(result);
                    if (result.getUserId() != null) {
                        dto.setUserId(result.getUserId().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 룰렛 설정 조회 메서드.
     * @return 모든 룰렛 설정의 리스트.
     */
    public List<RouletteSettingDTO> getAllRouletteSettings(PrincipalDetails principalDetails) {
        return getAllSettingsFromDB().stream()
                .map(rouletteSettingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 룰렛 설정 초기화 메서드. 데이터 베이스에 설정된 RouletteSetting이 없을 때 1~8 순번에 해당하는 초기값 생성.
     * @param principalDetails 인증된 사용자의 세부 정보.
     */
    public void initializeRouletteSettings(PrincipalDetails principalDetails) {
        List<RouletteSettings> initialSettings = IntStream.range(0, 8)
                .mapToObj(i -> RouletteSettings.builder()
                        .rouletteName("룰렛명 입력")
                        .rewardValue("0")
                        .rewardDescription("상품 세부명 입력")
                        .maxQuantity(0)
                        .originalMaxQuantity(0)
                        .probability(0.0)
                        .build())
                .collect(Collectors.toList());
        rouletteSettingRepository.saveAll(initialSettings);
    }

    /**
     * 데이터베이스로부터 모든 룰렛 설정을 가져오는 메서드.
     * @return 모든 룰렛 설정의 리스트.
     */
    private List<RouletteSettings> getAllSettingsFromDB() {
        return rouletteSettingRepository.findAll();
    }


    /**
     * 순번 1~8까지의 룰렛 설정값을 수정하는 메서드.
     * @param updateSettingsDTO 수정할 룰렛 설정의 정보.
     */
    @AuditLogService.Audit("룰렛 설정 업데이트")
    public void updateAllRouletteSettings(RouletteSettingsUpdateAllDTO updateSettingsDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        double totalProbability = updateSettingsDTO.getSettings().stream()
                .mapToDouble(RouletteSettingUpdateDTO::getProbability)
                .sum();

        if (totalProbability > 100) {
            throw new RestControllerException(ExceptionCode.ROULETTE_PROBABILITY_EXCEEDED, "룰렛 확률 100% 초과");
        }

        List<RouletteSettings> modifiedSettings = new ArrayList<>();

        for (RouletteSettingUpdateDTO updateSetting : updateSettingsDTO.getSettings()) {
            RouletteSettings rouletteSettings = rouletteSettingRepository.findById(updateSetting.getId()).orElseThrow(()
                    -> new RestControllerException(ExceptionCode.ROULETTE_SETTING_NOT_FOUND, "룰렛 설정을 찾을 수 없습니다."));

            rouletteSettings.setRouletteName(updateSetting.getRouletteName());
            rouletteSettings.setRewardValue(updateSetting.getRewardValue());
            rouletteSettings.setRewardDescription(updateSetting.getRewardDescription());
            rouletteSettings.setMaxQuantity(updateSetting.getMaxQuantity());
            rouletteSettings.setOriginalMaxQuantity(updateSetting.getMaxQuantity());
            rouletteSettings.setProbability(updateSetting.getProbability());

            modifiedSettings.add(rouletteSettings);
        }
        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("룰렛 설정 업데이트");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        rouletteSettingRepository.saveAll(modifiedSettings);
    }

    /**
     * 사용자의 룰렛을 돌리는 메서드.
     * @param userId 사용자의 아이디.
     * @param principalDetails 현재 인증된 사용자의 세부 정보.
     * @return 룰렛 결과에 대한 정보.
     */
    public RouletteSpinResultDTO spinRoulette(Long userId, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

        if (user.getBonusRouletteCount() <= 0 && user.getRouletteCount() <= 0) {
            throw new RestControllerException(ExceptionCode.ROULETTE_ALREADY_SPUN, "룰렛 이용권이 없습니다.");
        }

        String clientIp = request.getRemoteAddr();

        List<RouletteSettings> allSettings = getAllSettingsFromDB();

        double totalProbability = allSettings.stream()
                .mapToDouble(RouletteSettings::getProbability)
                .sum();

        RouletteSettings selectedSetting = decideRouletteResult(allSettings, totalProbability);

        RouletteSpinResultDTO resultDTO = updateUserWalletAndRouletteResult(user, selectedSetting, principalDetails, clientIp);
        return resultDTO;
    }

    /**
     * 룰렛 결과를 결정하는 메서드.
     * @param allSettings 모든 룰렛 설정의 리스트.
     * @param totalProbability 모든 룰렛 확률의 총합.
     * @return 선택된 룰렛 설정.
     */
    public RouletteSettings decideRouletteResult(List<RouletteSettings> allSettings, double totalProbability) {
        // 실패 설정 찾기
        RouletteSettings failSetting = allSettings.stream()
                .filter(s -> "꽝".equals(s.getRouletteName()))
                .findFirst()
                .orElseThrow(() -> new RestControllerException(ExceptionCode.ROULETTE_SETTING_NOT_FOUND, "룰렛 설정을 찾을 수 없습니다."));

        // 총 확률이 100% 미만일 경우 실패 설정의 확률을 나머지 확률로 설정
        if (totalProbability < 100) {
            double remainingProbability = 100 - totalProbability;
            failSetting.setProbability(failSetting.getProbability() + remainingProbability);
        }

        // 총 확률과 실패 확률 로깅
        logger.info("Total probability: {}, Fail setting probability: {}", totalProbability, failSetting.getProbability());
        for (RouletteSettings setting : allSettings) {
            logger.info("Roulette setting: {}, Probability: {}, Max quantity: {}", setting.getRouletteName(), setting.getProbability(), setting.getMaxQuantity());
        }

        // 100% 기준으로 랜덤 숫자 생성
        double randomNumber = ThreadLocalRandom.current().nextDouble() * 100;
        double cumulativeProbability = 0;

        // 룰렛 설정 반복
        for (RouletteSettings setting : allSettings) {
            cumulativeProbability += setting.getProbability();
            if (randomNumber <= cumulativeProbability && setting.getMaxQuantity() > 0) {
                logger.info("Selected setting: {}, cumulativeProbability: {}, randomNumber: {}", setting.getRouletteName(), cumulativeProbability, randomNumber);
                setting.setMaxQuantity(setting.getMaxQuantity() - 1);
                rouletteSettingRepository.save(setting);
                return setting;
            }
        }

        // 실패 시 예외 발생
        logger.error("룰렛 스핀 실패: 랜덤 숫자: {}, 누적 확률: {}", randomNumber, cumulativeProbability);
        throw new RestControllerException(ExceptionCode.ROULETTE_SPIN_FAILED, "룰렛 스핀이 실패했습니다.");
    }

    /**
     * 사용자의 지갑과 룰렛 결과를 업데이트하는 메서드.
     * @param user 결과를 업데이트할 사용자.
     * @param selectedSetting 선택된 룰렛 설정.
     * @param clientIp 사용자 ip 주소.
     * @return 업데이트된 룰렛 결과에 대한 정보.
     */
    public RouletteSpinResultDTO updateUserWalletAndRouletteResult(User user, RouletteSettings selectedSetting, PrincipalDetails principalDetails, String clientIp) {
        RouletteSpinResultDTO resultDTO = new RouletteSpinResultDTO();
        String rewardValue = selectedSetting.getRewardValue();

        resultDTO.setRouletteName(selectedSetting.getRouletteName());
        resultDTO.setRewardDescription(selectedSetting.getRewardDescription());
        resultDTO.setSpinDate(LocalDateTime.now());
        resultDTO.setUserId(user.getId());

        if (isNumeric(rewardValue)) {
            long rewardPoints = Integer.parseInt(rewardValue);
            if (rewardPoints == 0) {
                resultDTO.setStatus(PaymentStatusEnum.꽝);
            } else {
                resultDTO.setStatus(PaymentStatusEnum.지급완료);
                Wallet userWallet = walletRepository.findByUser(user)
                        .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));
                userWallet.setPoint(userWallet.getPoint() + rewardPoints);
                walletRepository.save(userWallet);

                pointLogService.recordPointLog(user.getId(), rewardPoints, PointLogCategoryEnum.룰렛, clientIp, "");
            }
        } else {
            resultDTO.setStatus(PaymentStatusEnum.지급대기중);
        }

        resultDTO.setRewardValue(rewardValue);

        RouletteResults rouletteResults = new RouletteResults();
        rouletteResults.setUserId(user);
        rouletteResults.setRewardValue(resultDTO.getRewardValue());
        rouletteResults.setRewardDescription(resultDTO.getRewardDescription());
        rouletteResults.setRouletteName(resultDTO.getRouletteName());
        rouletteResults.setStatus(resultDTO.getStatus());
        rouletteResults.setSpinDate(resultDTO.getSpinDate());
        rouletteResultRepository.save(rouletteResults);

        if (user.getBonusRouletteCount() > 0) {
            user.decreaseBonusRouletteCount();
        } else if (user.getRouletteCount() > 0) {
            user.decreaseRouletteCount();
        }
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
     * 룰렛 결과의 상태를 업데이트하는 메서드.
     *
     * @param resultId       업데이트할 룰렛 결과의 ID
     * @param newStatus        새로 설정할 상태 값 (PaymentStatus enum 타입)
     * @param principalDetails 현재 사용자의 권한 정보
     */
    public void updateRouletteStatus(Long resultId, PaymentStatusEnum newStatus, PrincipalDetails principalDetails) {
        RouletteResults rouletteResults = rouletteResultRepository.findById(resultId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.ROULETTE_RESULT_NOT_FOUND, "룰렛 결과를 찾을 수 없습니다"));
        rouletteResults.setStatus(newStatus);
        rouletteResultRepository.save(rouletteResults);
    }

    /**
     * PaymentStatus 기준으로 룰렛 결과 조회.
     *
     * @param status            필터링할 룰렛 결과의 상태. (PaymentStatus enum 타입)
     * @param principalDetails  현재 인증된 사용자의 보안 주체 세부 정보.
     * @return 주어진 상태와 일치하는 RouletteSpinResultDTO 객체의 리스트를 반환.
     * @throws RestControllerException 제공된 상태가 유효하지 않은 경우 예외를 던집니다.
     */
    public List<RouletteSpinResultDTO> findRouletteResultsByStatus(PaymentStatusEnum status, PrincipalDetails principalDetails) {
        List<RouletteResults> rouletteResults = rouletteResultRepository.findByStatus(status);
        return rouletteResults.stream()
                .map(result -> {
                    RouletteSpinResultDTO dto = rouletteResultMapper.toDto(result);
                    if (result.getUserId() != null) {
                        dto.setUserId(result.getUserId().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 매일 00:00 시와 12:00 시에 실행
     * 최대 지급개수, 룰렛카운트 초기화.
     */
    @Scheduled(cron = "0 0 0,12 * * ?")
    public void resetRouletteData() {
        List<RouletteSettings> allSettings = getAllSettingsFromDB();
        for (RouletteSettings setting : allSettings) {
            setting.setMaxQuantity(setting.getOriginalMaxQuantity());
            rouletteSettingRepository.save(setting);
        }

        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.setRouletteCount(1);
        }
        userRepository.saveAll(allUsers);
    }

    /**
     * 매일 00:00 시에만 실행
     * 보너스 룰렛 카운트 초기화
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetBonusRouletteCountDaily() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.setBonusRouletteCount(0);
        }
        userRepository.saveAll(allUsers);
    }

    /**
     * 충전 금액이 1만원 이상일 경우 사용자에게 룰렛 보너스를 제공하는 메서드.
     * 하루에 한 번만 보너스를 제공하기 위해 마지막으로 보상을 받은 날짜를 확인함.
     *
     * @param userId 사용자 ID
     * @param amount 충전 금액
     */
    public void bonusRouletteSpinForRecharge(Long userId, BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("10000")) >= 0) {
            User user = userRepository.findById(userId).orElseThrow(()
                    -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

            LocalDate today = LocalDate.now();

            if (!today.equals(user.getLastBonusRouletteDate())) {
                user.increaseBonusRouletteCount();
                user.setLastBonusRouletteDate(today);
                userRepository.save(user);
            }
        }
    }
}
