package GInternational.server.api.service;

import GInternational.server.api.entity.CompRewardRate;
import GInternational.server.api.entity.RollingRewardRate;
import GInternational.server.api.repository.CompRewardRateRepository;
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
public class CompRewardRateService {

    private final CompRewardRateRepository compRewardRateRepository;

    public List<CompRewardRate> setRates(Map<Integer, BigDecimal> rates, PrincipalDetails principalDetails) {
        List<CompRewardRate> savedRates = new ArrayList<>();
        rates.forEach((level, rate) -> {
            CompRewardRate rewardRate = new CompRewardRate();
            rewardRate.setLevel(level);
            BigDecimal adjustedRate = rate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            rewardRate.setRate(adjustedRate);
            savedRates.add(compRewardRateRepository.save(rewardRate));
        });
        return savedRates;
    }

    public List<CompRewardRate> updateRates(Map<Integer, BigDecimal> rates, PrincipalDetails principalDetails) {
        List<CompRewardRate> updatedRates = new ArrayList<>();
        rates.forEach((level, newRate) -> {
            CompRewardRate updatedRate = compRewardRateRepository.findById(level)
                    .map(rewardRate -> {
                        BigDecimal adjustedRate = newRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        rewardRate.setRate(adjustedRate);
                        return compRewardRateRepository.save(rewardRate);
                    })
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));
            updatedRates.add(updatedRate);
        });
        return updatedRates;
    }

    public List<CompRewardRate> getAllRates(PrincipalDetails principalDetails) {
        return compRewardRateRepository.findAll();
    }

    public void deleteRate(Integer level, PrincipalDetails principalDetails) {
        compRewardRateRepository.deleteById(level);
    }
}
