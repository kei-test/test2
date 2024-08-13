package GInternational.server.api.repository;


import GInternational.server.api.dto.LoginCountDTO;
import GInternational.server.api.entity.LoginHistory;
import GInternational.server.api.vo.AmazonUserStatusEnum;
import GInternational.server.api.entity.User;
import GInternational.server.api.vo.UserGubunEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long>, UserRepositoryCustom, JpaSpecificationExecutor<User> {
    Optional<User> findById(Long id);

    User findByUsername(String username);

    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    Optional<User> findByAasId(Integer aasId);

    @Query("SELECT u FROM users u WHERE u.aasId = :aasId AND u.role IN ('ROLE_USER', 'ROLE_TEST')")
    Optional<User> findByAasIdAndRoles(@Param("aasId") Integer aasId);

    @Query("SELECT u.aasId FROM users u WHERE u.role = :role")
    List<Integer> findAllAasIdsByRole(@Param("role") String role);

    @Query("SELECT u.password FROM users u WHERE u.id = :userId")
    String getPasswordByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM users u WHERE FUNCTION('MONTH', u.createdAt) = :month AND FUNCTION('YEAR', u.createdAt) = :year AND u.role = 'ROLE_USER'")
    List<User> findByCreatedAtMonthAndYearAndRoleUser(@Param("month") int month, @Param("year") int year);

    // 대본사에 의해 귀속된 멤버 조회
    List<User> findByDaeIdAndAmazonUserStatus(Long bigHeadOfficeId, AmazonUserStatusEnum status);

    // 본사에 의해 귀속된 멤버 조회
    List<User> findByBonIdAndAmazonUserStatus(Long headOfficeId, AmazonUserStatusEnum status);

    // 부본사에 의해 귀속된 멤버 조회
    List<User> findByBuIdAndAmazonUserStatus(Long deputyHeadOfficeId, AmazonUserStatusEnum status);

    // 총판에 의해 귀속된 멤버 조회
    List<User> findByChongIdAndAmazonUserStatus(Long distributorId, AmazonUserStatusEnum status);

    List<User> findByAmazonCode(String amazonCode);

    // 특정 partnerType을 가진 모든 User 객체를 조회하는 메서드
    List<User> findByPartnerType(String partnerType);

    List<User> findAllByRoleInAndApproveIpIsNotNull(List<String> roles);

    List<User> findByRole(String role);

    @Query("SELECT u FROM users u WHERE u.role = 'ROLE_USER' OR u.role = 'ROLE_GUEST' OR u.role = 'ROLE_TEST' ORDER BY u.id DESC")
    List<User> findAllUsersWithRoleUserOrGuest();

    Long countByRole(String role);

    @Query("SELECT u.referredBy FROM users u WHERE u.role = :role ORDER BY u.createdAt ASC")
    List<String> findOldestGuestReferredBy(@Param("role") String role, Pageable pageable);

    User findByRecommendationCode(String recommendationCode);

    List<User> findAllByUsername(String username);

    List<User> findAllByNickname(String nickname);

    @Query("SELECT u FROM users u WHERE u.username = :username OR u.recommendationCode = :recommendationCode")
    User findByUsernameOrRecommendationCode(@Param("username") String username, @Param("recommendationCode") String recommendationCode);

    int countByCreatedAtBetweenAndRole(LocalDateTime startOfDay, LocalDateTime endOfDay, String role);

    int countByCreatedAtBetweenAndUserGubunEnum(LocalDateTime startOfDay, LocalDateTime endOfDay, UserGubunEnum userGubunEnum);

    List<LoginCountDTO> findAllByOrderByVisitCountDesc();
}