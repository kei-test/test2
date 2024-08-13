package GInternational.server.partner.controller;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.User;
import GInternational.server.api.mapper.UserRequestMapper;
import GInternational.server.api.service.UserService;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.common.exception.RestControllerException;

import GInternational.server.partner.dto.PartnerUserRequestDTO;
import GInternational.server.partner.service.PartnerUserService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.nio.charset.IllegalCharsetNameException;
import java.util.List;

@RequestMapping("dst/api/v2")
@RestController
@RequiredArgsConstructor
@Validated
public class DstController {

    private final PartnerUserService partnerUserService;
    private static final Logger logger = LoggerFactory.getLogger(DstController.class);

    /**
     * 사용자 계정을 생성하고 관련 정보를 반환. 이 메서드는 사용자가 제공한 정보를 기반으로 새로운 사용자 계정을 생성.
     * 생성 과정 중에 검증이 실패하거나 서버 내부에서 예외가 발생한 경우, 적절한 HTTP 상태 코드와 에러 메시지를 포함한 응답을 반환.
     *
     * @param userRequestDTO 사용자 생성을 위해 필요한 정보를 담고 있는 DTO. 사용자 이름, 비밀번호 등의 필수 정보를
     *                       포함해야 함.
     * @param request        클라이언트의 HTTP 요청 정보. 이 정보는 사용자의 IP 주소 검증 등 추가적인 검증에 사용됨.
     * @return ResponseEntity 객체로, 성공적으로 사용자가 생성된 경우 생성된 사용자 정보를 담고 있는 DTO와 함께 HTTP
     *         상태 코드 201(Created)을 반환.
     *         예외 상황(예: 유효하지 않은 사용자 정보, 차단된 IP, 중복된 사용자 이름 등)에서는 해당 에러 메시지와 적절한 HTTP
     *         상태 코드를 반환.
     * @throws RestControllerException 사용자 생성 과정에서 발생한 예외를 처리. 이 예외에는 다양한 유형이 있으며,
     *                                 각각의 경우에 따라 다른 메시지와 HTTP 상태 코드가 반환됨.
     *                                 - BLOCKED_IP: 접근이 차단된 IP에서 요청이 발생한 경우
     *                                 - REFERRER_NOT_FOUNT: 존재하지 않는 추천인 코드를 입력한 경우
     *                                 - USERNAME_NOT_PROVIDED, INVALID_USERNAME,
     *                                 USERNAME_DUPLICATE: 유효하지 않은 사용자 이름을 제공한 경우
     * @throws Exception               그 외 예외 처리. 예상치 못한 오류나 서버 내부 오류 발생 시 반환됨.
     */
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody PartnerUserRequestDTO userRequestDTO,
            HttpServletRequest request) {
        try {
            UserResponseDTO response = partnerUserService.createUser(userRequestDTO, request);
            return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.CREATED);
        } catch (RestControllerException e) {
            switch (e.getExceptionCode()) {
                case BLOCKED_IP:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근이 차단된 IP입니다.");
                case REFERRER_NOT_FOUNT:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 추천인(추천코드)입니다.");
                case USERNAME_NOT_PROVIDED:
                case INVALID_USERNAME:
                case USERNAME_DUPLICATE:
                    return ResponseEntity.badRequest().body(e.getMessage());
                default:
                    return ResponseEntity.internalServerError().body("서버 내부 오류가 발생했습니다.");
            }
        } catch (Exception e) {
            logger.error("Unexpected server error", e);
            return ResponseEntity.internalServerError().body("서버에서 오류가 발생했습니다. 자세한 내용은 로그를 확인해주세요.");
        }
    }
}
