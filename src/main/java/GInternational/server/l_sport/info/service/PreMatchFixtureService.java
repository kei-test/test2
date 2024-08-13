package GInternational.server.l_sport.info.service;

import GInternational.server.api.entity.Account;
import GInternational.server.api.vo.AppStatus;
import GInternational.server.l_sport.info.dto.count.PreMatchGameCountResponseDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetFixtureDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetFixtureResponseDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetSportResponseDTO;
import GInternational.server.l_sport.info.dto.results.GameResultListDTO;
import GInternational.server.l_sport.info.dto.results.GameResultListResponseDTO;
import GInternational.server.l_sport.info.dto.results.GameResultResponseDTO;
import GInternational.server.l_sport.info.repository.FixtureRepository;

import GInternational.server.security.auth.PrincipalDetails;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PreMatchFixtureService {

    private final FixtureRepository fixtureRepository;


    public List<PreMatchGetFixtureDTO> getPreMatchFixturesDetail(String matchId,String type) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedStartTime = currentDateTime.format(currentFormatter);

        LocalDateTime endDateDateTime = currentDateTime.plusDays(3);
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedEndTime = endDateDateTime.format(endFormatter);
        return fixtureRepository.getPreMatchFixturesDetail(matchId,type,formattedStartTime,formattedEndTime);
    }


    public List<PreMatchGetFixtureResponseDTO> getPreMatchFixtures(String sportName) {

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedStartTime = currentDateTime.format(currentFormatter);

        LocalDateTime endDateDateTime = currentDateTime.plusDays(3);
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedEndTime = endDateDateTime.format(endFormatter);

        List<PreMatchGetFixtureDTO> preMatchFixtures = fixtureRepository.getPreMatchFixtures(sportName,formattedStartTime,formattedEndTime);
        return PreMatchGetFixtureResponseDTO.transform(preMatchFixtures);
    }


    public Map<String, PreMatchGameCountResponseDTO> getGameCount(Long type) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedStartTime = currentDateTime.format(currentFormatter);

        LocalDateTime endDateDateTime = currentDateTime.plusDays(3);
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedEndTime = endDateDateTime.format(endFormatter);

        List<Tuple> queryResult = fixtureRepository.getGameCount(type,formattedStartTime,formattedEndTime);

        return queryResult.stream()
                .map(tuple -> {
                    String sportName = tuple.get(0, String.class);
                    Long count = tuple.get(1, Long.class);

                    PreMatchGameCountResponseDTO dto = new PreMatchGameCountResponseDTO();
                    dto.setSportsName(sportName);
                    dto.setCount(count);

                    return dto;
                })
                .sorted(Comparator.comparing(PreMatchGameCountResponseDTO::getCount, Comparator.reverseOrder()))
                .collect(Collectors.toMap(PreMatchGameCountResponseDTO::getSportsName, dto -> dto, (dto1, dto2) -> dto2, LinkedHashMap::new));
    }



    public List<PreMatchGetSportResponseDTO> getSport(Long type) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedStartTime = currentDateTime.format(currentFormatter);

        LocalDateTime endDateDateTime = currentDateTime.plusDays(3);
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedEndTime = endDateDateTime.format(endFormatter);

        List<PreMatchGetFixtureDTO> preMatchFixtures = fixtureRepository.getSport(type,formattedStartTime, formattedEndTime);
        return PreMatchGetSportResponseDTO.transform(preMatchFixtures);
    }


    public Page<GameResultListResponseDTO> getGameResultList(Long type, int page, int size) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedStartTime = currentDateTime.format(currentFormatter);

        LocalDateTime endDateDateTime = currentDateTime.minusDays(10);
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedEndTime = endDateDateTime.format(endFormatter);

        List<GameResultListDTO> list = fixtureRepository.getGameResultList(type, formattedEndTime,formattedStartTime);
        List<GameResultListResponseDTO> transformedContent = GameResultListResponseDTO.transform(list);
        Map<String, Map<String, GameResultListResponseDTO>> groupedByMatchIdAndMarketName = transformedContent.stream()
                .collect(Collectors.groupingBy(GameResultListResponseDTO::getMatchId,
                        Collectors.toMap(GameResultListResponseDTO::getGameType,
                                Function.identity(),
                                (existing, replacement) -> existing)));

        List<GameResultListResponseDTO> deduplicatedList = groupedByMatchIdAndMarketName.values().stream()
                .flatMap(map -> map.values().stream())
                .sorted(Comparator.comparing(GameResultListResponseDTO::getStartDate).reversed())
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), deduplicatedList.size());
        List<GameResultListResponseDTO> paginatedContent = start >= deduplicatedList.size() ? Collections.emptyList() : deduplicatedList.subList(start, end);

        return new PageImpl<>(paginatedContent, pageable, deduplicatedList.size());
    }



    // 인플레이, 프리매치
    public Page<GameResultResponseDTO> getGameResult(Long type, int page, int size) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedStartTime = currentDateTime.format(currentFormatter);

        LocalDateTime endDateDateTime = currentDateTime.minusDays(10);
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedEndTime = endDateDateTime.format(endFormatter);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("start_date").descending());
        Page<GameResultResponseDTO> pages = fixtureRepository.getGameResult(type,formattedEndTime,formattedStartTime, pageable);
        return new PageImpl<>(pages.getContent(), pageable, pages.getTotalElements());
    }
}
