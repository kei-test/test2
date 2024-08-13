package GInternational.server.l_sport.info.repository;


//import GInternational.server.l_sport.snapshot.pre.fixture.dto.GameResultListResponseDTO;

import GInternational.server.l_sport.batch.job.dto.edit.EditMatchResultDTO;
import GInternational.server.l_sport.batch.job.dto.order.MatchScoreDTO;
import GInternational.server.l_sport.info.dto.admin.AdminPreMatchDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetFixtureDTO;
import GInternational.server.l_sport.info.dto.results.GameResultListDTO;
import GInternational.server.l_sport.info.dto.results.GameResultResponseDTO;
import GInternational.server.l_sport.info.dto.inplay.InPlayGetFixtureDTO;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;

public interface FixtureRepositoryCustom {


//    프리매치
    List<PreMatchGetFixtureDTO> getPreMatchFixturesDetail(String matchId,String type,String startDate,String endDate);
    List<PreMatchGetFixtureDTO> getPreMatchFixtures(String sportName,String startDate, String endDate);

//    인플레이
    List<InPlayGetFixtureDTO> getInPlayFixturesDetail(String matchId,String type);
    List<InPlayGetFixtureDTO> getInPlayFixtures(String sportsName);

//  종목별 베팅가능 경기 카운트
    List<Tuple> getGameCount(Long type,String startDate,String endDate);

//  베팅 종류별 경기목록(인플레이,프리매치,크로스 등등 타입별 조회)
    List<PreMatchGetFixtureDTO> getSport(Long type,String startDate,String endDate);

//  베팅 종류별 결과리스트
    List<GameResultListDTO> getGameResultList(Long type, String endDate, String startDate);

    Page<GameResultResponseDTO> getGameResult(Long type,String endDate, String startDate, Pageable pageable);


    //베팅 결과 반환 시 매치에 해당하는 스코어만 가져오는 메서드
    List<MatchScoreDTO> searchByScoreData(List<String> matchId);

    //경기결과 수정을 위한 매치,배당 정보 가져오는 메서드
    List<GameResultListDTO> searchByEndedMatchData(Long type,String endDate,String startDate);


    Page<AdminPreMatchDTO> searchByAdminMatch(String type,String status,String sportsName,String leagueName, Pageable pageable);


}
