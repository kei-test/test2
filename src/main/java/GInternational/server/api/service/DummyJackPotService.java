package GInternational.server.api.service;

import GInternational.server.api.dto.DummyJackPotReqDTO;
import GInternational.server.api.dto.DummyJackPotResDTO;
import GInternational.server.api.entity.DummyJackPot;
import GInternational.server.api.mapper.DummyJackPotReqMapper;
import GInternational.server.api.mapper.DummyJackPotResMapper;
import GInternational.server.api.repository.DummyJackPotRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class DummyJackPotService {

    private final DummyJackPotReqMapper dummyJackPotReqMapper;
    private final DummyJackPotResMapper dummyJackPotResMapper;
    private final DummyJackPotRepository dummyJackPotRepository;

    public DummyJackPotResDTO createManualJackpotData(DummyJackPotReqDTO dummyJackPotReqDTO, PrincipalDetails principalDetails) {
        DummyJackPot dummyJackPot = dummyJackPotReqMapper.toEntity(dummyJackPotReqDTO);
        DummyJackPot savedJackPot = dummyJackPotRepository.save(dummyJackPot);
        return dummyJackPotResMapper.toDto(savedJackPot);
    }

    public List<DummyJackPotResDTO> getAllJackpotData(PrincipalDetails principalDetails) {
        List<DummyJackPot> allJackpots = dummyJackPotRepository.findAll();
        return allJackpots.stream()
                .map(dummyJackPotResMapper::toDto)
                .collect(Collectors.toList());
    }

    public DummyJackPotResDTO updateJackpotData(Long id, DummyJackPotReqDTO dummyJackPotReqDTO, PrincipalDetails principalDetails) {
        Optional<DummyJackPot> optionalDummyJackPot = dummyJackPotRepository.findById(id);
        if (optionalDummyJackPot.isPresent()) {
            DummyJackPot dummyJackPot = optionalDummyJackPot.get();
            dummyJackPot.setUsername(dummyJackPotReqDTO.getUsername());
            dummyJackPot.setBetAmount(dummyJackPotReqDTO.getBetAmount());
            dummyJackPot.setReward(dummyJackPotReqDTO.getReward());
            dummyJackPot.setRewardDate(dummyJackPotReqDTO.getRewardDate());
            DummyJackPot updatedJackPot = dummyJackPotRepository.save(dummyJackPot);
            return dummyJackPotResMapper.toDto(updatedJackPot);
        } else {
            throw new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "해당 ID의 데이터가 존재하지 않습니다.");
        }
    }

    public void deleteJackpotData(Long id, PrincipalDetails principalDetails) {
        if (dummyJackPotRepository.existsById(id)) {
            dummyJackPotRepository.deleteById(id);
        } else {
            throw new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "해당 ID의 데이터가 존재하지 않습니다.");
        }
    }

    @Scheduled(cron = "0 0 0 */14 * *") // 2주 간격으로 실행
    public void generateRandomJackpotData() {
        int numberOfRecords = 1; // 원하는 만큼 레코드를 생성
        for (int i = 0; i < numberOfRecords; i++) {
            DummyJackPot dummyJackPot = new DummyJackPot();
            dummyJackPot.setUsername(generateRandomUsername());
            dummyJackPot.setBetAmount(generateRandomBetAmount());
            dummyJackPot.setReward(generateRandomReward());
            dummyJackPot.setRewardDate(generateRandomRewardDate());
            dummyJackPotRepository.save(dummyJackPot);
        }
    }

    // username 생성
    private String generateRandomUsername() {
        Random random = new Random();
        char firstChar = (char) (random.nextBoolean() ? ('a' + random.nextInt(26)) : ('0' + random.nextInt(10)));
        int numberOfStars = random.nextInt(5) + 3; // 3 ~ 7개의 '*' 생성
        String stars = "*".repeat(numberOfStars);
        return firstChar + stars;
    }

    // betAmount 생성
    private String generateRandomBetAmount() {
        Random random = new Random();
        int amount = 1000 + (random.nextInt(2000) * 100); // 1,000원 ~ 200,000원 범위, 100원 단위
        return String.format("%,d원", amount);
    }

    // reward 생성
    private String generateRandomReward() {
        Random random = new Random();
        int reward = 5000000 + random.nextInt(45000001); // 5,000,000원 ~ 50,000,000원, 1원 단위
        return String.format("%,d원", reward);
    }

    // rewardDate 생성
    private LocalDateTime generateRandomRewardDate() {
        Random random = new Random();
        int hour = random.nextInt(24); // 0 ~ 23시
        int minute = random.nextInt(60); // 0 ~ 59분
        int second = random.nextInt(60); // 0 ~ 59초

        // 현재 날짜를 기준으로 시, 분, 초만 랜덤 설정
        return LocalDateTime.now()
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .withNano(0); // 나노초는 0으로 설정
    }
}
