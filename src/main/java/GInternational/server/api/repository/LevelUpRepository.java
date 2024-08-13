package GInternational.server.api.repository;

import GInternational.server.api.entity.LevelUp;
import GInternational.server.api.vo.LevelUpTransactionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LevelUpRepository extends JpaRepository<LevelUp, Long>, JpaSpecificationExecutor<LevelUp> {

}
