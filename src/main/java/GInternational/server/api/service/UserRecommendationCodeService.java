package GInternational.server.api.service;

import GInternational.server.api.dto.RecommendationCodeResDTO;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class UserRecommendationCodeService {

    private final UserRepository userRepository;

    public void assignRecommendationCodes(String usernames, PrincipalDetails principalDetails) {
        String[] usernameArray = usernames.split(",");
        for (String username : usernameArray) {
            username = username.trim(); // 공백 제거
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다. ID를 다시 확인 후 입력하세요.");
            }
            if (user.getRecommendationCode() != null) {
                throw new RestControllerException(ExceptionCode.RECOMMENDATION_CODE_ALREADY_ISSUED, username + "이 유저는 이미 추천인코드가 발급되었습니다.");
            }
            assignRecommendationCodeToUser(user);
        }
    }

    public List<RecommendationCodeResDTO> findUsersByCriteria(Long userId, String username, String nickname, PrincipalDetails principalDetails) {
        List<User> users;
        if (userId != null) {
            users = userRepository.findById(userId)
                    .filter(user -> user.getRecommendationCode() != null)
                    .map(Collections::singletonList)
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        } else if (username != null) {
            users = userRepository.findAllByUsername(username).stream()
                    .filter(user -> user.getRecommendationCode() != null)
                    .collect(Collectors.toList());
        } else if (nickname != null) {
            users = userRepository.findAllByNickname(nickname).stream()
                    .filter(user -> user.getRecommendationCode() != null)
                    .collect(Collectors.toList());
        } else {
            users = userRepository.findAll().stream()
                    .filter(user -> user.getRecommendationCode() != null)
                    .collect(Collectors.toList());
        }

        return users.stream().map(user -> new RecommendationCodeResDTO(
                user.getId(),
                user.getDistributor(),
                user.getUsername(),
                user.getNickname(),
                user.getRecommendedCount(),
                user.getRecommendationCode(),
                user.getRecommendationCodeIssuedAt()
        )).collect(Collectors.toList());
    }

    public void deleteRecommendationCode(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        user.setRecommendationCode(null);
        user.setRecommendationCodeIssuedAt(null);
        userRepository.save(user);
    }

    private void assignRecommendationCodeToUser(User user) {
        String recommendationCode = generateRecommendationCode();
        user.setRecommendationCode(recommendationCode);
        user.setRecommendationCodeIssuedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private String generateRecommendationCode() {
        Random random = new Random();
        String generatedCode;
        boolean isUnique = false;
        do {
            generatedCode = String.format("%06d", random.nextInt(1000000));
            User existingUser = userRepository.findByRecommendationCode(generatedCode);
            if (existingUser == null) {
                isUnique = true;
            }
        } while (!isUnique);
        return generatedCode;
    }
}
