package GInternational.server.api.controller;

import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.dto.AmazonUserHierarchyResponseDTO;
import GInternational.server.api.dto.AmazonUserInfoDTO;
import GInternational.server.api.dto.AmazonUserRequestDTO;
import GInternational.server.api.dto.AmazonUserResponseDTO;
import GInternational.server.api.service.AmazonUserService;
import GInternational.server.api.vo.AmazonUserStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/amazon/api/v2")
@RestController
@Validated
@RequiredArgsConstructor
public class AmazonUserController {

    private final AmazonUserService amazonUserService;

//    /**
//     * 어드민 계정 생성.
//     *
//     * @param amazonUserRequestDTO Amazon 사용자 생성 요청 데이터
//     * @return 생성된 어드민 사용자 정보
//     */
//    @PostMapping("/admin")
//    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> createAdmin(@RequestBody @Valid AmazonUserRequestDTO amazonUserRequestDTO) {
//        AmazonUserResponseDTO createdAdmin = amazonUserService.createAdmin(amazonUserRequestDTO);
//        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(createdAdmin);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

    /**
     * 대본사 계정 생성. 이 작업은 어드민만 수행할 수 있음.
     *
     * @param amazonUserRequestDTO Amazon 사용자 생성 요청 데이터
     * @param authentication 인증 정보
     * @return 생성된 대본사 사용자 정보
     */
    @PostMapping("/bigHeadOffice")
    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> createBigHeadOffice(@RequestBody @Valid AmazonUserRequestDTO amazonUserRequestDTO,
                                                                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO createdUser = amazonUserService.createBigHeadOffice(amazonUserRequestDTO, principal);
        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(createdUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 본사 계정 생성. 이 작업은 대본사만 수행할 수 있으나, 현재 클라이언트 요청으로 어드민만 생성 가능하도록 임시 변경되었음.
     *
     * @param amazonUserRequestDTO Amazon 사용자 생성 요청 데이터
     * @param authentication 인증 정보
     * @return 생성된 본사 사용자 정보
     */
    @PostMapping("/headOffices")
    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> createHeadOffice(@RequestBody @Valid AmazonUserRequestDTO amazonUserRequestDTO,
                                                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO createdUser = amazonUserService.createHeadOffice(amazonUserRequestDTO, principal);
        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(createdUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 부본사 계정 생성. 이 작업은 본사만 수행할 수 있으나, 현재 클라이언트 요청으로 어드민만 생성 가능하도록 임시 변경되었음.
     *
     * @param amazonUserRequestDTO Amazon 사용자 생성 요청 데이터
     * @param authentication 인증 정보
     * @return 생성된 부본사 사용자 정보
     */
    @PostMapping("/deputyHeadOffices")
    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> createDeputyHeadOffice(@RequestBody @Valid AmazonUserRequestDTO amazonUserRequestDTO,
                                                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO createdDeputyHeadOffice = amazonUserService.createDeputyHeadOffice(amazonUserRequestDTO, principal);
        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(createdDeputyHeadOffice);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 총판 계정 생성. 이 작업은 부본사만 수행할 수 있으나, 현재 클라이언트 요청으로 어드민만 생성 가능하도록 임시 변경되었음.
     *
     * @param amazonUserRequestDTO Amazon 사용자 생성 요청 데이터
     * @param authentication 인증 정보
     * @return 생성된 총판 사용자 정보
     */
    @PostMapping("/distributors")
    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> createDistributor(@RequestBody @Valid AmazonUserRequestDTO amazonUserRequestDTO,
                                                                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO createdDistributor = amazonUserService.createDistributor(amazonUserRequestDTO, principal);
        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(createdDistributor);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 매장 계정 생성. 이 작업은 총판만 수행할 수 있으나, 현재 클라이언트 요청으로 어드민만 생성 가능하도록 임시 변경되었음.
     *
     * @param userRequestDTO Amazon 사용자 생성 요청 데이터
     * @param authentication 인증 정보
     * @return 생성된 매장 사용자 정보
     */
    @PostMapping("/store")
    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> createStore(@RequestBody @Valid AmazonUserRequestDTO userRequestDTO,
                                                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO createdStore = amazonUserService.createStore(userRequestDTO, principal);
        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(createdStore);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 특정 사용자의 상위 계층 정보 조회.
     *
     * @param userId 사용자 ID
     * @param authentication 인증 정보
     * @return 상위 계층 정보
     */
    @GetMapping("/users/{userId}/hierarchy")
    public ResponseEntity<SingleResponseDto<AmazonUserHierarchyResponseDTO>> findUserHierarchy(@PathVariable Long userId,
                                                                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserHierarchyResponseDTO UserHierarchy = amazonUserService.findFartnerHierarchy(userId);
        SingleResponseDto<AmazonUserHierarchyResponseDTO> response = new SingleResponseDto<>(UserHierarchy);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인한 사용자의 역할에 따라 직속 하위 파트너 조회.
     *
     * @param authentication 인증 정보
     * @param status 사용자 상태
     * @return 하위 파트너 목록
     */
    @GetMapping("/users/partners")
    public ResponseEntity<?> getUsersByRoleAndStatus(
            Authentication authentication, @RequestParam AmazonUserStatusEnum status) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        // 관리자인 경우, 대본사별로 그룹화된 사용자 목록 반환
        if (principal.getUser().getRole().equals("ROLE_ADMIN")) {
            Map<String, List<AmazonUserResponseDTO>> usersHierarchy = amazonUserService.findDirectSubFartners(principal, status);
            return ResponseEntity.ok(usersHierarchy);
        } else {
            // 다른 역할의 사용자인 경우, 해당 사용자와 연결된 하위 사용자 목록 반환
            List<AmazonUserResponseDTO> users = amazonUserService.findDirectSubFartners(principal, status).values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        }
    }

    /**
     * 하부 파트너 추가.
     *
     * @param requestDTO 파트너 생성 요청 데이터
     * @param userId 사용자 ID
     * @param authentication 인증 정보
     * @return 생성된 파트너 정보
     */
    @PostMapping("/users/sub-account")
    public ResponseEntity<AmazonUserResponseDTO> createSubAccountForPartner(@RequestBody AmazonUserRequestDTO requestDTO,
                                                                            @RequestParam Long userId,
                                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO responseDTO = amazonUserService.createSubAccountForPartner(requestDTO, userId, principal);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 파트너 상세 정보 조회.
     *
     * @param userId 사용자 ID
     * @param authentication 인증 정보
     * @return 파트너 상세 정보
     */
    @GetMapping("/users/details/{userId}")
    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> findUserById(@PathVariable Long userId, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO userResponse = amazonUserService.findUserById(userId, principal);
        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(userResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * 파트너 정보 업데이트.
     *
     * @param userId 사용자 ID
     * @param requestDTO 업데이트 요청 데이터
     * @param authentication 인증 정보
     * @return 업데이트된 파트너 정보
     */
    @PutMapping("/users/update/{userId}")
    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> updateUser(@PathVariable Long userId,
                                                                               @RequestBody @Valid AmazonUserRequestDTO requestDTO,
                                                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO updatedUser = amazonUserService.updateUser(userId, requestDTO, principal);
        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(updatedUser);
        return ResponseEntity.ok(response);
    }

    /**
     * 접속 실패 카운트 0으로 초기화.
     *
     * @param userId 사용자 ID
     * @param authentication 인증 정보
     * @return 초기화된 사용자 정보
     */
    @PostMapping("/users/{userId}/resetFailVisit")
    public ResponseEntity<SingleResponseDto<AmazonUserResponseDTO>> resetUserFailVisitCount(@PathVariable Long userId,
                                                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AmazonUserResponseDTO updatedUser = amazonUserService.resetFailVisitCount(userId, principal);
        SingleResponseDto<AmazonUserResponseDTO> response = new SingleResponseDto<>(updatedUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * IsAmazonUser가 true인 모든 유저 조회.
     *
     * @param authentication 인증 정보
     * @return IsAmazonUser가 true인 모든 유저 목록
     */
    @GetMapping("/users/isAmazonUser")
    public ResponseEntity<List<AmazonUserInfoDTO>> getUsersWithNonNullPartnerType(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmazonUserInfoDTO> users = amazonUserService.findUsersByRoleAndReferred(principal);
        return ResponseEntity.ok(users);
    }
}
