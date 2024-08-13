package GInternational.server.api.service;

import GInternational.server.api.entity.RollingRewardRate;
import GInternational.server.api.repository.RollingRewardRateRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class RollingRewardRateService {

    private final RollingRewardRateRepository rollingRewardRateRepository;

    public List<RollingRewardRate> setRates(Map<Integer, BigDecimal> rates, PrincipalDetails principalDetails) {
        List<RollingRewardRate> savedRates = new ArrayList<>();
        rates.forEach((level, rate) -> {
            RollingRewardRate rewardRate = new RollingRewardRate();
            rewardRate.setLevel(level);
            BigDecimal adjustedRate = rate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            rewardRate.setRate(adjustedRate);
            savedRates.add(rollingRewardRateRepository.save(rewardRate));
        });
        return savedRates;
    }

    public List<RollingRewardRate> updateRates(Map<Integer, BigDecimal> rates, PrincipalDetails principalDetails) {
        List<RollingRewardRate> updatedRates = new ArrayList<>();
        rates.forEach((level, newRate) -> {
            RollingRewardRate updatedRate = rollingRewardRateRepository.findById(level)
                    .map(rewardRate -> {
                        BigDecimal adjustedRate = newRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        rewardRate.setRate(adjustedRate);
                        return rollingRewardRateRepository.save(rewardRate);
                    })
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));
            updatedRates.add(updatedRate);
        });
        return updatedRates;
    }

    public List<RollingRewardRate> getAllRates(PrincipalDetails principalDetails) {
        return rollingRewardRateRepository.findAll();
    }

    public void deleteRate(Integer level, PrincipalDetails principalDetails) {
        rollingRewardRateRepository.deleteById(level);
    }
}
