package GInternational.server.l_sport.info.repository;

import GInternational.server.l_sport.info.entity.Odd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OddRepository extends JpaRepository<Odd, String>,OddRepositoryCustom {

    Odd findByIdxAndMatchId(String idx,String matchId);



}
