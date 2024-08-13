package GInternational.server.kplay.game.controller;

import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.kplay.game.dto.list.ApiResponseDTO;
import GInternational.server.kplay.game.dto.list.FavoriteDTO;
import GInternational.server.kplay.game.dto.list.RequestInfoList;
import GInternational.server.kplay.game.entity.Game;
import GInternational.server.kplay.game.entity.MegaGame;
import GInternational.server.kplay.game.service.GameService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * 게임 목록을 처리하여 API 응답 DTO 반환.
     *
     * @param gameList 게임 정보 목록
     * @return ResponseEntity API 응답 DTO
     */
    @PostMapping("/gamelist")
    public ResponseEntity<ApiResponseDTO> gameList(@RequestBody List<RequestInfoList> gameList) {
        ApiResponseDTO apiResponseDTO = gameService.addProcessGameList(gameList);
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.OK);
    }

    /**
     * 제품 ID에 따른 게임 목록 조회.
     *
     * @param prdId 제품 ID
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return ResponseEntity 페이지네이션된 게임 목록
     */
    @GetMapping("/{prdId}/game")
    public ResponseEntity searchByPrdGame(@PathVariable ("prdId") int prdId,
                                          @RequestParam int page,
                                          @RequestParam int size) {
        Page<Game> pages = gameService.searchByPrdGame(prdId,page,size);
        List<Game> list = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(list,pages),HttpStatus.OK);
    }

    /**
     * 게임 유형별로 게임 목록을 조회.
     *
     * @param type 게임 유형
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return ResponseEntity 페이지네이션된 게임 목록
     */
    @GetMapping("/{type}/{gameCategory}/type/game")
    public ResponseEntity searchByTypeGame(@PathVariable ("type") String type,
                                           @PathVariable ("gameCategory") String gameCategory,
                                           @RequestParam int page,
                                           @RequestParam int size) {
        Page<Game> pages = gameService.searchByGame(type,gameCategory,page,size);
        List<Game> list = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(list,pages),HttpStatus.OK);
    }

    /**
     * 모든 게임을 조회.
     *
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return ResponseEntity 페이지네이션된 게임 목록
     */
    @GetMapping("/games")
    public ResponseEntity findAll(@RequestParam int page,
                                  @RequestParam int size) {
        Page<Game> pages = gameService.searchByTypeNull(page ,size);
        List<Game> list = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(list,pages),HttpStatus.OK);
    }

    /**
     * 지정된 유저와 게임에 대한 즐겨찾기 상태를 토글.
     * 사용자가 특정 게임을 즐겨찾기에 추가하거나 제거할 수 있음.
     *
     * @param userId 즐겨찾기 상태를 변경하고자 하는 유저의 ID
     * @param gameId 즐겨찾기 상태를 변경하고자 하는 게임의 ID
     * @return ResponseEntity<?> 상태 변경 성공시 HTTP 200 OK 응답
     */
    @PutMapping("/{userId}/{gameId}/toggle-favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long userId,
                                            @PathVariable Long gameId,
                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        gameService.toggleFavorite(userId, gameId, principal);
        return ResponseEntity.ok().build();
    }

    /**
     * 지정된 유저의 즐겨찾기된 게임 목록을 조회.
     * 이 메서드는 해당 유저가 즐겨찾기한 모든 게임을 반환.
     *
     * @param userId 즐겨찾기 목록을 조회하고자 하는 유저의 ID
     * @return ResponseEntity<List<Game>> 유저가 즐겨찾기한 게임 목록을 담은 응답 객체
     */
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<FavoriteDTO>> getFavoriteGames(@PathVariable Long userId,
                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<FavoriteDTO> favoriteGames = gameService.findFavoriteGamesByUser(userId, principal);
        return ResponseEntity.ok(favoriteGames);
    }
}

