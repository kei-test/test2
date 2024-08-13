package GInternational.server.api.repository;


import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.vo.BetFoldCountEnum;
import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.l_sport.batch.job.dto.order.DetailResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BetHistoryRepositoryCustom {

    Page<DetailResponseDTO> searchOrderList(Long userId,
                                            BetTypeEnum custom,
                                            String username,
                                            String nickname,
                                            BetFoldCountEnum betFoldCount,
                                            String ip,
                                            Long id,
                                            List<Long> betGroupId,
                                            OrderStatusEnum orderStatus,
                                            Boolean deleted,
                                            String orderBy,
                                            Pageable pageable,
                                            LocalDate startDate,
                                            LocalDate endDate);


    long countByOrder(Long userId,
                      BetTypeEnum custom,
                      String username,
                      String nickname,
                      BetFoldCountEnum betFoldCount,
                      String ip,
                      Long id,
                      List<Long> betGroupId,
                      OrderStatusEnum orderStatus,
                      Boolean deleted,
                      LocalDate startDate,
                      LocalDate endDate);

    double calculateProfit(Long groupId);


    List<BetHistory> searchByBetHistories(String matchId,String marketName,String winIdx,String drawIdx,String loseIdx);

}
