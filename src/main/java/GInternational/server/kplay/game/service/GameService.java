package GInternational.server.kplay.game.service;

import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.kplay.game.dto.list.ApiResponseDTO;
import GInternational.server.kplay.game.dto.list.FavoriteDTO;
import GInternational.server.kplay.game.dto.list.GameInfoDTO;
import GInternational.server.kplay.game.dto.list.RequestInfoList;
import GInternational.server.kplay.game.entity.GameFavorite;
import GInternational.server.kplay.game.entity.Game;
import GInternational.server.kplay.game.entity.MegaGame;
import GInternational.server.kplay.game.repository.GameFavoriteRepository;
import GInternational.server.kplay.game.repository.GameRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(value = "clientServerTransactionManager")
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameFavoriteRepository gameFavoriteRepository;

    /**
     * 제공된 게임 목록 정보를 기반으로 게임 데이터를 처리하고 API 응답을 생성.
     *
     * @param gameList 게임 목록 정보
     * @return ApiResponseDTO 처리된 게임 목록 정보를 담은 API 응답 DTO
     */
    public ApiResponseDTO addProcessGameList(List<RequestInfoList> gameList) {
        Map<String, List<GameInfoDTO>> gameListMap = new HashMap<>();

        for (RequestInfoList requestInfo : gameList) {
            String key = String.valueOf(requestInfo.getPrdId());

            if (!gameListMap.containsKey(key)) {
                gameListMap.put(key, new ArrayList<>());
            }
            Game game = new Game();
            game.setId(requestInfo.getId());
            game.setGameIndex(requestInfo.getGameIndex());
            game.setName(requestInfo.getName());
            game.setIcon(requestInfo.getIcon());
            game.setPrdId(requestInfo.getPrdId());
            game.setRtp(requestInfo.getRtp());
            game.setIsEnabled(requestInfo.getIsEnabled());
            game.setType(requestInfo.getType());
            game.setGameCategory(requestInfo.getGameCategory());
            gameRepository.save(game);
            GameInfoDTO gameInfoDTO = new GameInfoDTO();
            gameListMap.get(key).add(gameInfoDTO);
        }

        ApiResponseDTO apiResponseDTO = new ApiResponseDTO();
        apiResponseDTO.setStatus(1);
        apiResponseDTO.setGame_list(gameListMap);

        return apiResponseDTO;
    }



    //즐겨찾기 테이블의 유무로 인해 게임리스트 업데이트 로직 필요함





    /**
     * 특정 제품 ID에 해당하는 게임을 페이지네이션하여 조회.
     *
     * @param prdId 제품 ID
     * @param page 요청 페이지 번호
     * @param size 페이지 당 항목 수
     * @return Page<Game> 조회된 게임 목록
     */
    public Page<Game> searchByPrdGame(int prdId, int page, int size) {
        Pageable pageable = PageRequest.of(page -1,size, Sort.by("id").descending());
        Page<Game> prdGame = gameRepository.searchByPrdGame(prdId,pageable);
        return new PageImpl<>(prdGame.getContent(), pageable, prdGame.getTotalElements());
    }

    /**
     * 게임 유형별로 게임을 페이지네이션하여 조회.
     *
     * @param type 게임 유형
     * @param page 요청 페이지 번호
     * @param size 페이지 당 항목 수
     * @return Page<Game> 조회된 게임 목록
     */
    public Page<Game> searchByGame(String type,String gameCategory,int page, int size) {
        Pageable pageable = PageRequest.of(page -1, size,Sort.by("id").descending());
        Page<Game> typeGame = gameRepository.searchByType(type,gameCategory,pageable);
        return new PageImpl<>(typeGame.getContent(),pageable, typeGame.getTotalElements());
    }

    /**
     * 특정 조건 없이 모든 게임을 페이지네이션하여 조회.
     *
     * @param page 요청 페이지 번호
     * @param size 페이지 당 항목 수
     * @return Page<Game> 조회된 게임 목록
     */
    public Page<Game> searchByTypeNull(int page, int size) {
        Pageable pageable = PageRequest.of(page -1, size,Sort.by("id").descending());
        Page<Game> typeNullGame = gameRepository.searchByNullCondition(pageable);
        return new PageImpl<>(typeNullGame.getContent(),pageable,typeNullGame.getTotalElements());
    }



    /**
     * 지정된 유저와 게임에 대해 즐겨찾기 상태를 토글.
     * 만약 지정된 게임이 유저의 즐겨찾기 목록에 이미 있다면, 이를 목록에서 제거.
     * 그렇지 않은 경우, 즐겨찾기 목록에 게임을 추가.
     *
     * @param userId 즐겨찾기 상태를 변경하고자 하는 유저의 ID
     * @param gameId 즐겨찾기 상태를 변경하고자 하는 게임의 ID
     * @throws RestControllerException 유저 또는 게임을 찾을 수 없는 경우 예외 발생
     */
    public void toggleFavorite(long userId, long gameId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.GAME_NOT_FOUNT, "게임을 찾을 수 없습니다."));

        Optional<GameFavorite> favorite = gameFavoriteRepository.findByUserAndGame(user, game);
        if (favorite.isPresent()) {
            // 이미 즐겨찾기 되어 있다면 제거
            gameFavoriteRepository.delete(favorite.get());
        } else {
            // 즐겨찾기 추가
            GameFavorite newFavorite = new GameFavorite();
            newFavorite.setUser(user);
            newFavorite.setGame(game);
            gameFavoriteRepository.save(newFavorite);
        }
    }

    /**
     * 지정된 유저의 즐겨찾기된 게임 목록을 조회.
     * 이 메서드는 해당 유저가 즐겨찾기한 모든 게임의 목록 반환.
     *
     * @param userId 즐겨찾기 목록을 조회하고자 하는 유저의 ID
     * @return List<Game> 유저가 즐겨찾기한 게임 목록
     * @throws IllegalArgumentException 지정된 유저 ID에 해당하는 유저를 찾을 수 없을 때 예외 발생
     */
    public List<FavoriteDTO> findFavoriteGamesByUser(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        List<GameFavorite> favorites = gameFavoriteRepository.findAllByUser(user);

        return favorites.stream()
                .map(favorite -> new FavoriteDTO(
                        favorite.getGame().getId(),
                        favorite.getGame().getPrdId(),
                        favorite.getGame().getGameIndex(),
                        favorite.getGame().getName(),
                        favorite.getGame().getIcon(),
                        favorite.getGame().getRtp(),
                        favorite.getGame().getType(),
                        favorite.getGame().getIsEnabled(),
                        favorite.getGame().getGameCategory(),
                        true
                ))
                .collect(Collectors.toList());
    }
}


