package GInternational.server.api.repository;

import GInternational.server.api.dto.AmazonUserInfoDTO;
import GInternational.server.api.dto.UserCalculateDTO;
import GInternational.server.api.entity.User;
import GInternational.server.api.vo.UserGubunEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserRepositoryCustom {
    Page<User> deletedUserInfo(Pageable pageable, LocalDate startDate, LocalDate endDate);

    Map<UserGubunEnum, Long> getCountByUserGubunForLevel(int level);

    List<UserCalculateDTO> getTotalAmountForAllLevelForPeriod(int lv, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<AmazonUserInfoDTO> findUsersByIsAmazonUser();

    List<AmazonUserInfoDTO> findUsersByReferredByAndIsAmazonUser(String referredBy);

    List<User> findTop30ByLastVisitNotNullOrderByLastVisit();
}

