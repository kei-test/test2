package GInternational.server.api.service;

import GInternational.server.api.dto.DummyExchangeResDTO;
import GInternational.server.api.dto.DummyJackPotReqDTO;
import GInternational.server.api.dto.DummyJackPotResDTO;
import GInternational.server.api.entity.DummyExchange;
import GInternational.server.api.entity.DummyJackPot;
import GInternational.server.api.mapper.DummyExchangeReqMapper;
import GInternational.server.api.mapper.DummyExchangeResMapper;
import GInternational.server.api.mapper.DummyJackPotReqMapper;
import GInternational.server.api.mapper.DummyJackPotResMapper;
import GInternational.server.api.repository.DummyExchangeRepository;
import GInternational.server.api.repository.DummyJackPotRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class DummyExchangeService {

    private final DummyExchangeReqMapper dummyExchangeReqMapper;
    private final DummyExchangeResMapper dummyExchangeResMapper;
    private final DummyExchangeRepository dummyExchangeRepository;

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 00:00:00에 실행
    public void createDummyExchanges() {
        Random random = new Random();

        // 5,000,000 ~ 10,000,000원 사이의 랜덤 값 생성 (만원 단위)
        long minAmount = 5000000;
        long maxAmount = 10000000;
        long range = maxAmount - minAmount + 1;
        for (int i = 0; i < 5; i++) {
            DummyExchange dummyExchange = new DummyExchange();
            dummyExchange.setUsername("*******");

            // 랜덤값 생성 및 만원 단위로 변환
            long amountInTenThousand = minAmount + (random.nextInt((int) range / 10000 + 1) * 10000);
            dummyExchange.setExchangeAmount(String.valueOf(amountInTenThousand));

            // 00:00:00 형식으로 랜덤 시간 생성
            LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));
            dummyExchange.setExchangeTime(randomTime);

            dummyExchangeRepository.save(dummyExchange);
        }
    }

    public List<DummyExchangeResDTO> getLastFiveDummyExchanges() {
        // 최근 5개의 데이터를 가져옴
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        List<DummyExchange> lastFiveExchanges = dummyExchangeRepository.findAll(pageable).getContent();

        // 가져온 데이터들을 exchangeTime을 기준으로 정렬
        List<DummyExchange> sortedExchanges = lastFiveExchanges.stream()
                .sorted(Comparator.comparing(DummyExchange::getExchangeTime))
                .collect(Collectors.toList());

        // DTO로 변환하여 반환
        return sortedExchanges.stream()
                .map(dummyExchangeResMapper::toDto)
                .collect(Collectors.toList());
    }
}
