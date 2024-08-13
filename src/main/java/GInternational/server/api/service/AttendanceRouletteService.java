package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.AttendanceRouletteSettingDTO;
import GInternational.server.api.dto.AttendanceRouletteSpinResultDTO;
import GInternational.server.api.dto.AttendanceRouletteSettingsUpdateAllDTO;
import GInternational.server.api.dto.AttendanceRouletteSettingUpdateDTO;
import GInternational.server.api.entity.AttendanceRouletteResults;
import GInternational.server.api.entity.AttendanceRouletteSettings;
import GInternational.server.api.mapper.AttendanceRouletteResultMapper;
import GInternational.server.api.mapper.AttendanceRouletteSettingMapper;
import GInternational.server.api.repository.AttendanceRouletteResultRepository;
import GInternational.server.api.repository.AttendanceRouletteSettingRepository;
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
public class AttendanceRouletteService {

    private final AttendanceRouletteSettingRepository attendanceRouletteSettingRepository;
    private final AttendanceRouletteResultRepository attendanceRouletteResultRepository;
    private final PointLogService pointLogService;
    private final AttendanceRouletteSettingMapper attendanceRouletteSettingMapper;
    private final AttendanceRouletteResultMapper attendanceRouletteResultMapper;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    /**
     * 룰렛 설정 조회 메서드. 초기값이 없을 경우 초기값 생성.
     * @return 모든 룰렛 설정의 리스트.
     */
    public List<AttendanceRouletteSettingDTO> getAllAttendanceRouletteSettings(PrincipalDetails principalDetails) {
        List<AttendanceRouletteSettings> currentSettings = getAllSettingsFromDB();

        if (currentSettings.isEmpty()) {
            initializeAttendanceRouletteSettings(principalDetails);
            currentSettings = getAllSettingsFromDB();
        }

        return currentSettings.stream()
                .map(attendanceRouletteSettingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 데이터 베이스에 설정된 RouletteSetting이 없을 때 1~4 순번에 해당하는 초기값 생성.
     * @param principalDetails 인증된 사용자의 세부 정보.
     */
    public void initializeAttendanceRouletteSettings(PrincipalDetails principalDetails) {
        List<AttendanceRouletteSettings> initialSettings = IntStream.range(0, 4)
                .mapToObj(i -> AttendanceRouletteSettings.builder()
                        .rouletteName("룰렛명 입력")
                        .rewardValue("0")
                        .rewardDescription("상품 세부명 입력")
                        .maxQuantity(0)
                        .originalMaxQuantity(0)
                        .probability(0.0)
                        .build())
                .collect(Collectors.toList());
        attendanceRouletteSettingRepository.saveAll(initialSettings);
    }

    /**
     * 모든 룰렛 스핀 결과를 조회하여 반환하는 메서드.
     *
     * @return 모든 룰렛 스핀 결과의 DTO 목록
     */
    public List<AttendanceRouletteSpinResultDTO> getAllAttendanceRouletteSpinResults(PrincipalDetails principalDetails) {
        List<AttendanceRouletteResults> attendanceRouletteResults = attendanceRouletteResultRepository.findAll();

        return attendanceRouletteResults.stream()
                .map(attendanceRouletteResultMapper::toDto) // 매퍼 인스턴스 사용
                .collect(Collectors.toList());
    }

    /**
     * PaymentStatus 기준으로 룰렛 결과 조회.
     *
     * @param status            필터링할 룰렛 결과의 상태. (PaymentStatus enum 타입)
     * @param principalDetails  현재 인증된 사용자의 보안 주체 세부 정보.
     * @return 주어진 상태와 일치하는 RouletteSpinResultDTO 객체의 리스트를 반환.
     * @throws RestControllerException 제공된 상태가 유효하지 않은 경우 예외를 던집니다.
     */
    public List<AttendanceRouletteSpinResultDTO> findRouletteResultsByStatus(PaymentStatusEnum status, PrincipalDetails principalDetails) {
        List<AttendanceRouletteResults> rouletteResults = attendanceRouletteResultRepository.findByStatus(status);
        return rouletteResults.stream()
                .map(result -> {
                    AttendanceRouletteSpinResultDTO dto = attendanceRouletteResultMapper.toDto(result);
                    if (result.getUserId() != null) {
                        dto.setUserId(result.getUserId().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 순번 1~4까지의 룰렛 설정값을 수정하는 메서드.
     * @param updateAttendanceSettingsDTO 수정할 룰렛 설정의 정보.
     */
    @AuditLogService.Audit("출석체크룰렛 설정 업데이트")
    public void updateAllAttendanceRouletteSettings(AttendanceRouletteSettingsUpdateAllDTO updateAttendanceSettingsDTO,
                                                    HttpServletRequest request,
                                                    PrincipalDetails principalDetails) {
        double totalProbability = updateAttendanceSettingsDTO.getSettings().stream()
                .mapToDouble(AttendanceRouletteSettingUpdateDTO::getProbability)
                .sum();

        if (totalProbability > 100) {
            throw new RestControllerException(ExceptionCode.ATTENDANCE_ROULETTE_PROBABILITY_EXCEEDED, "출석체크 룰렛 확률 100% 초과.");
        }

        List<AttendanceRouletteSettings> modifiedSettings = new ArrayList<>();

        for (AttendanceRouletteSettingUpdateDTO updateSetting : updateAttendanceSettingsDTO.getSettings()) {
            AttendanceRouletteSettings rouletteSetting = attendanceRouletteSettingRepository.findById(updateSetting.getId()).orElseThrow(()
                    -> new RestControllerException(ExceptionCode.ATTENDANCE_ROULETTE_SETTING_NOT_FOUND, "출석체크 룰렛 설정을 찾을 수 없습니다."));

            rouletteSetting.setRouletteName(updateSetting.getRouletteName());
            rouletteSetting.setRewardValue(updateSetting.getRewardValue());
            rouletteSetting.setRewardDescription(updateSetting.getRewardDescription());
            rouletteSetting.setMaxQuantity(updateSetting.getMaxQuantity());
            rouletteSetting.setOriginalMaxQuantity(updateSetting.getMaxQuantity());
            rouletteSetting.setProbability(updateSetting.getProbability());

            modifiedSettings.add(rouletteSetting);
        }
        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(null);
        context.setUsername(null);
        context.setDetails("출석체크룰렛 설정 업데이트");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        attendanceRouletteSettingRepository.saveAll(modifiedSettings);
    }

    /**
     * 룰렛 결과의 상태를 업데이트하는 메서드.
     *
     * @param id               업데이트할 룰렛 결과의 ID
     * @param newStatus        새로 설정할 상태 값
     * @param principalDetails 현재 사용자의 권한 정보
     */
    public void updateAttendanceRouletteStatus(Long id, PaymentStatusEnum newStatus, PrincipalDetails principalDetails) {
        AttendanceRouletteResults attendanceRouletteResults = attendanceRouletteResultRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.ATTENDANCE_RESULT_NOT_FOUND, "출석체크 룰렛 결과를 찾을 수 없습니다."));
        attendanceRouletteResults.setStatus(newStatus);
        attendanceRouletteResultRepository.save(attendanceRouletteResults);
    }

    /**
     * 사용자의 룰렛을 돌리는 메서드.
     * @param userId 사용자의 아이디.
     * @param principalDetails 현재 인증된 사용자의 세부 정보.
     * @return 룰렛 결과에 대한 정보.
     */
    public AttendanceRouletteSpinResultDTO spinAttendanceRoulette(Long userId, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

        if (user.getAttendanceRouletteCount() <= 0) {
            throw new RestControllerException(ExceptionCode.ATTENDANCE_ROULETTE_ALREADY_SPUN, "출석체크 룰렛 이용권이 없습니다.");
        }

        String clientIp = request.getRemoteAddr();

        user.decreaseAttendanceRouletteCount();

        List<AttendanceRouletteSettings> allSettings = getAllSettingsFromDB();
        double totalProbability = allSettings.stream()
                .mapToDouble(AttendanceRouletteSettings::getProbability)
                .sum();

        AttendanceRouletteSettings selectedSetting = decideAttendanceRouletteResult(allSettings, totalProbability);

        AttendanceRouletteSpinResultDTO resultDTO = updateUserWalletAndAttendanceRouletteResult(user, selectedSetting, clientIp);
        return resultDTO;
    }

    /**
     * 룰렛 결과를 결정하는 메서드.
     * @param allSettings 모든 룰렛 설정의 리스트.
     * @param totalProbability 모든 룰렛 확률의 총합.
     * @return 선택된 룰렛 설정.
     */
    public AttendanceRouletteSettings decideAttendanceRouletteResult(List<AttendanceRouletteSettings> allSettings, double totalProbability) {
        AttendanceRouletteSettings failSetting = allSettings.stream()
                .filter(s -> "꽝".equals(s.getRouletteName()))
                .findFirst()
                .orElseThrow(() -> new RestControllerException(ExceptionCode.ATTENDANCE_ROULETTE_SETTING_NOT_FOUND, "출석체크 룰렛 설정을 찾을 수 없습니다."));
        failSetting.setProbability(100 - totalProbability);

        if (totalProbability > 100) {
            throw new RestControllerException(ExceptionCode.ATTENDANCE_ROULETTE_PROBABILITY_EXCEEDED, "출석체크 룰렛 확률 100% 초과.");
        }

        double randomNumber = ThreadLocalRandom.current().nextDouble() * totalProbability;
        double cumulativeProbability = 0;

        for (AttendanceRouletteSettings setting : allSettings) {
            cumulativeProbability += setting.getProbability();
            if (randomNumber <= cumulativeProbability && setting.getMaxQuantity() > 0) {
                setting.setMaxQuantity(setting.getMaxQuantity() - 1);
                attendanceRouletteSettingRepository.save(setting);
                return setting;
            }
        }
        throw new RestControllerException(ExceptionCode.ATTENDANCE_ROULETTE_SPIN_FAILED, "출석체크 룰렛 스핀이 실패 했습니다.");
    }

    /**
     * 사용자의 지갑과 룰렛 결과를 업데이트하는 메서드.
     * @param user 결과를 업데이트할 사용자.
     * @param selectedSetting 선택된 룰렛 설정.
     * @param clientIp 사용자의 ip 주소.
     * @return 업데이트된 룰렛 결과에 대한 정보.
     */
    public AttendanceRouletteSpinResultDTO updateUserWalletAndAttendanceRouletteResult(User user, AttendanceRouletteSettings selectedSetting, String clientIp) {
        AttendanceRouletteSpinResultDTO resultDTO = new AttendanceRouletteSpinResultDTO();
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
                Wallet wallet = walletRepository.findByUser(user)
                        .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다"));
                wallet.setPoint(wallet.getPoint() + rewardPoints);
                walletRepository.save(wallet);

                pointLogService.recordPointLog(user.getId(), rewardPoints, PointLogCategoryEnum.출석체크룰렛, clientIp, "");
            }
        } else {
            resultDTO.setStatus(PaymentStatusEnum.지급대기중);
        }

        resultDTO.setRewardValue(rewardValue);

        AttendanceRouletteResults rouletteResult = new AttendanceRouletteResults();
        rouletteResult.setUserId(user);
        rouletteResult.setRewardValue(resultDTO.getRewardValue());
        rouletteResult.setRewardDescription(resultDTO.getRewardDescription());
        rouletteResult.setRouletteName(resultDTO.getRouletteName());
        rouletteResult.setStatus(resultDTO.getStatus());
        rouletteResult.setSpinDate(resultDTO.getSpinDate());
        attendanceRouletteResultRepository.save(rouletteResult);

        user.decreaseAttendanceRouletteCount();
        userRepository.save(user);

        return resultDTO;
    }

    /**
     * 데이터베이스로부터 모든 룰렛 설정을 가져오는 메서드.
     * @return 모든 룰렛 설정의 리스트.
     */
    private List<AttendanceRouletteSettings> getAllSettingsFromDB() {
        return attendanceRouletteSettingRepository.findAll();
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
}
