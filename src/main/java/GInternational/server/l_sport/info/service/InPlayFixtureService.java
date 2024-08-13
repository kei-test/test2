package GInternational.server.l_sport.info.service;

import GInternational.server.l_sport.info.entity.Match;
import GInternational.server.l_sport.info.repository.FixtureRepository;
import GInternational.server.l_sport.info.dto.inplay.InPlayGetFixtureDTO;
import GInternational.server.l_sport.info.dto.inplay.InPlayGetFixtureResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class InPlayFixtureService {


    private final FixtureRepository fixtureRepository;
    private final JdbcTemplate jdbcTemplate;

    public InPlayFixtureService(FixtureRepository fixtureRepository, @Qualifier("lsportJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.fixtureRepository = fixtureRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<InPlayGetFixtureDTO> getInPlayFixturesDetail(String matchId,String type) {
        return fixtureRepository.getInPlayFixturesDetail(matchId,type);
    }

    public List<InPlayGetFixtureResponseDTO> getInPlayFixtures(String sportsName) {
        List<InPlayGetFixtureDTO> inPlayGetFixtures = fixtureRepository.getInPlayFixtures(sportsName);
        return InPlayGetFixtureResponseDTO.transform(inPlayGetFixtures);
    }
}
