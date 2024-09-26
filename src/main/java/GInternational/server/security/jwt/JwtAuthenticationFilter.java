package GInternational.server.security.jwt;


import GInternational.server.api.dto.LoginRequestDto;
import GInternational.server.api.dto.LoginResponseDto;
import GInternational.server.api.entity.Ip;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.WhiteIp;
import GInternational.server.api.repository.IpRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WhiteIpRepository;
import GInternational.server.api.service.*;
import GInternational.server.api.vo.AdminEnum;
import GInternational.server.api.vo.ExpRecordEnum;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.common.ipinfo.service.IpInfoService;
import GInternational.server.security.auth.PrincipalDetails;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.ipinfo.api.model.IPResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final IpRepository ipRepository;
    private final IpInfoService ipInfoService;
    private final LoginHistoryService loginHistoryService;
    private final LoginStatisticService loginStatisticService;
    private final AdminLoginHistoryService adminLoginHistoryService;
    private final AmazonLoginHistoryService amazonLoginHistoryService;
    private final WhiteIpRepository whiteIpRepository;
    private final ExpRecordService expRecordService;
    private final LoginInfoService loginInfoService;
    private final LoginSuccessHistoryService loginSuccessHistoryService;


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;
        User user = null;
        String countryCode = null;
        String loginType = null;
        loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);

        String ip = ipInfoService.getClientIp(request);
        Optional<WhiteIp> whiteIpOptional = whiteIpRepository.findByWhiteIp(ip);

        // 디바이스 타입 추출
        String userAgentString = request.getHeader("User-Agent");
        String deviceType = ipInfoService.extractDeviceTypeFromUserAgentForAdmin(userAgentString);

        Ip validateCheckIp = ipRepository.findByIpContent(ip);
        IPResponse ipResponse = ipInfoService.getIpInfo(ip);
        countryCode = ipResponse.getCountryCode();
        Set<UserGubunEnum> blockedStatuses = EnumSet.of(
                UserGubunEnum.거절,
                UserGubunEnum.정지,
                UserGubunEnum.하락탈퇴,
                UserGubunEnum.탈퇴1,
                UserGubunEnum.탈퇴2,
                UserGubunEnum.탈퇴3);

        user = userRepository.findByUsername(loginRequestDto.getUsername());
        if (user == null) {
            AuthenticationException exception = new AuthenticationServiceException("가입한 회원 정보를 확인해주세요.");
            loginHistoryService.saveLoginHistory(loginRequestDto, ip, ipResponse, null, request, countryCode, "불특정 비회원의 로그인 시도");
            unsuccessfulAuthentication(request, response, exception);
            return null;
        }
        String role = user.getRole();
        String partnerType = user.getPartnerType();
        System.out.println(partnerType +"@@@@@@@@@@@@@");
        if (user.getPartnerType() != null) {
            loginType = "파트너";
        } else if ("ROLE_ADMIN".equals(user.getRole()) || "ROLE_MANAGER".equals(user.getRole())) {
            loginType = "관리자";
        }

        if (user != null && loginRequestDto.getUrlGubun().equals("user")) {
            if (user != null && user.getRole().equals("ROLE_GUEST")) {
                loginHistoryService.saveLoginHistory(loginRequestDto, ip, ipResponse, user.getNickname(), request, countryCode, "회원가입 신청이 미승인된 회원의 로그인");
                AuthenticationException exception = new AuthenticationServiceException("회원가입 신청이 미승인된 회원입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if (user.getPartnerType() != null) {
                AuthenticationException exception = new AuthenticationServiceException("파트너 회원은 접근이 불가합니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if (user != null && validateCheckIp != null) {
                loginHistoryService.saveLoginHistory(loginRequestDto, ip, ipResponse, user.getNickname(), request, countryCode, "차단 IP");
                AuthenticationException exception = new AuthenticationServiceException("접근이 차단된 IP입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if ((user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) &&
                    user.getAdminEnum() != null &&
                    user.getAdminEnum().equals(AdminEnum.사용불가)) {
                AuthenticationException exception = new AuthenticationServiceException("계정이 사용 불가 상태입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if ("ROLE_USER".equals(role) || "ROLE_TEST".equals(role) || "ROLE_ADMIN".equals(role) || "ROLE_MANAGER".equals(role)) {
                if (blockedStatuses.contains(user.getUserGubunEnum())) {
                    recordAdminLoginAttemptIfAdmin(user, loginRequestDto.getUsername(), false, ip, countryCode, deviceType);
                    AuthenticationException exception = new AuthenticationServiceException("정지 또는 삭제된 회원입니다.");
                    unsuccessfulAuthentication(request, response, exception);
                    return null;
                }
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            return authentication;
        } else if (user != null && loginRequestDto.getUrlGubun().equals("amazon")) {
            if (user.getRole().equals("ROLE_USER") && user.getPartnerType() == null) {
                AuthenticationException exception = new AuthenticationServiceException("파트너 페이지입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if (user != null && user.getRole().equals("ROLE_GUEST")) {
                AuthenticationException exception = new AuthenticationServiceException("미승인된 회원입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if (user != null && validateCheckIp != null) {
                AuthenticationException exception = new AuthenticationServiceException("접근이 차단된 IP입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if ((user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) &&
                    user.getAdminEnum() != null &&
                    user.getAdminEnum().equals(AdminEnum.사용불가)) {
                AuthenticationException exception = new AuthenticationServiceException("계정이 사용 불가 상태입니다.");
                unsuccessfulAuthentication(request, response, exception);
                if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) {
                    recordAdminLoginAttemptIfAdmin(user, loginRequestDto.getUsername(), false, ip, countryCode, deviceType);
                }
                if (user.getPartnerType() != null) {
                    amazonLoginHistoryService.saveAmazonLoginHistory(loginRequestDto, ip, null, "실패", loginType + " - 계정 사용 불가 상태");
                }
                return null;
            } else if ("ROLE_USER".equals(role) || "ROLE_TEST".equals(role) || "ROLE_ADMIN".equals(role) || "ROLE_MANAGER".equals(role)) {
                if (blockedStatuses.contains(user.getUserGubunEnum())) {
                    AuthenticationException exception = new AuthenticationServiceException("정지 또는 삭제된 회원입니다.");
                    unsuccessfulAuthentication(request, response, exception);
                    if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) {
                        recordAdminLoginAttemptIfAdmin(user, loginRequestDto.getUsername(), false, ip, countryCode, deviceType);
                    }
                    if (user.getPartnerType() != null) {
                        amazonLoginHistoryService.saveAmazonLoginHistory(loginRequestDto, ip, null, "실패", loginType + " - 정지 또는 삭제된 계정");
                    }
                    return null;
                }


            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            if (principalDetails.getUser().getPartnerType() != null) {
                amazonLoginHistoryService.saveAmazonLoginHistory(loginRequestDto, ip, user.getNickname(), "성공", loginType);
            }
            return authentication;

        } else if (user != null && loginRequestDto.getUrlGubun().equals("admin")) {
            if (user != null && user.getRole().equals("ROLE_GUEST") || user.getRole().equals("ROLE_USER")) {
                AuthenticationException exception = new AuthenticationServiceException("관리자 페이지입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if (user != null && validateCheckIp != null) {
                recordAdminLoginAttemptIfAdmin(user, loginRequestDto.getUsername(), false, ip, countryCode, deviceType);
                AuthenticationException exception = new AuthenticationServiceException("접근이 차단된 IP입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if ((user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_MANAGER")) &&
                    user.getAdminEnum() != null &&
                    user.getAdminEnum().equals(AdminEnum.사용불가)) {
                AuthenticationException exception = new AuthenticationServiceException("계정이 사용 불가 상태입니다.");
                unsuccessfulAuthentication(request, response, exception);
                return null;
            } else if ("ROLE_USER".equals(role) || "ROLE_TEST".equals(role) || "ROLE_ADMIN".equals(role) || "ROLE_MANAGER".equals(role)) {
                if (blockedStatuses.contains(user.getUserGubunEnum())) {
                    recordAdminLoginAttemptIfAdmin(user, loginRequestDto.getUsername(), false, ip, countryCode, deviceType);
                    AuthenticationException exception = new AuthenticationServiceException("정지 또는 삭제된 회원입니다.");
                    unsuccessfulAuthentication(request, response, exception);
                    return null;
                }
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return authentication;
    }


    private void recordAdminLoginAttemptIfAdmin(User user, String username, boolean success, String ip, String countryCode, String deviceType) {
        if ("ROLE_ADMIN".equals(user.getRole()) || "ROLE_MANAGER".equals(user.getRole())) {
            adminLoginHistoryService.recordLoginAttempt(username, success, ip, null, countryCode, deviceType);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        String message;
        try {
            if (failed.getMessage().equals("접근이 차단된 IP입니다.")) {
                message = "접근이 차단된 IP입니다.";
            } else if (failed.getMessage().equals("회원가입 신청이 미승인된 회원입니다.")) {
                message = "회원가입 신청이 미승인된 회원입니다.";
            } else if (failed.getMessage().equals("정지 또는 삭제된 회원입니다.")) {
                message = "정지 또는 삭제된 회원입니다.";
            } else if (failed.getMessage().equals("계정이 사용 불가 상태입니다.")) {
                message = "계정이 사용 불가 상태입니다.";
            } else if (failed.getMessage().equals("아이디 또는 비밀번호가 일치하지 않습니다.")) {
                message = "아이디 또는 비밀번호가 일치하지 않습니다.";
            } else if (failed.getMessage().equals("가입한 회원 정보를 확인해주세요.")) {
                message = "가입한 회원 정보를 확인해주세요.";
            } else if (failed.getMessage().equals("올바른 회원 정보를 입력해주세요.")) {
                message = "올바른 회원 정보를 입력해주세요.";
            } else if (failed.getMessage().equals("관리자 페이지입니다.")) {
                message = "관리자 페이지입니다.";
            } else if (failed.getMessage().equals("미승인된 회원입니다.")) {
                message = "미승인된 회원입니다.";
            } else if (failed.getMessage().equals("파트너 회원은 접근이 불가합니다.")) {
                message = "파트너 회원은 접근이 불가합니다.";
            } else if (failed.getMessage().equals("파트너 페이지입니다.")) {
                message = "파트너 페이지입니다.";
            } else {
                message = "잘못된 요청입니다.";
            }
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            throw new IllegalStateException("sad");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        logger.info("성공");

        // 로그인 시 방문(로그잇 횟수) 증가
        User user = principalDetails.getUser();

        // IP 정보 조회
        String ip = ipInfoService.getClientIp(request);
        IPResponse ipResponse = ipInfoService.getIpInfo(ip);
        String countryCode = ipResponse.getCountryCode();

        Optional<WhiteIp> whiteIpOptional = whiteIpRepository.findByWhiteIp(ip);

        // 디바이스 타입 추출
        String userAgentString = request.getHeader("User-Agent");
        String deviceType = ipInfoService.extractDeviceTypeFromUserAgentForAdmin(userAgentString);

        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("userId", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING,
                JwtProperties.TOKEN_PREFIX + jwtToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setUserId(principalDetails.getUser().getId());
        loginResponseDto.setUsername(principalDetails.getUsername());
        loginResponseDto.setNickname(principalDetails.getUser().getNickname());
        loginResponseDto.setName(principalDetails.getUser().getName());
        loginResponseDto.setLv(principalDetails.getUser().getLv());
        loginResponseDto.setLastAccessedIp(ip);
        loginResponseDto.setRole(principalDetails.getUser().getRole());
        loginResponseDto.setVisitCount(user.getVisitCount());
        loginResponseDto.setVisitLog(LocalDateTime.now());
        if (user.getRole().equals("ROLE_USER")) {
            loginResponseDto.setSportsBalance(user.getWallet().getSportsBalance());
            loginResponseDto.setPoint(user.getWallet().getPoint());
            loginResponseDto.setCasinoBalance(user.getWallet().getCasinoBalance());
            loginResponseDto.setWalletId(user.getWallet().getId());
            loginStatisticService.recordLogin();
        }
        // issuedAt 과의 비교검증을 위해 lastVisit을 저장할 때 나노초 제거 후 저장
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime truncatedToSeconds = now.truncatedTo(ChronoUnit.SECONDS);
        user.setLastVisit(truncatedToSeconds);
        user.setVisitCount(user.getVisitCount() + 1);
        user.setLastAccessedIp(ip);
        user.setLastAccessedDevice(deviceType);
        user.setLastAccessedCountry(countryCode);
        userRepository.save(user);



        if (whiteIpOptional.isEmpty()) {
            if ("ROLE_ADMIN".equals(user.getRole()) || "ROLE_MANAGER".equals(user.getRole())) {
                adminLoginHistoryService.recordLoginAttempt(user.getUsername(), true, ip, user.getNickname(), countryCode, deviceType);
            }
        }
        if ("ROLE_USER".equals(user.getRole()) || "ROLE_TEST".equals(user.getRole())) {
            loginInfoService.saveLoginInfo(user.getUsername(), user.getNickname(), user.getDistributor(), user.getStore(), ip, deviceType);
        }

        expRecordService.recordDailyExp(user.getId(), user.getUsername(), user.getNickname(), 1, ip, ExpRecordEnum.로그인경험치);
        loginSuccessHistoryService.saveLoginHistory(user.getId(), ip, request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String responseBody = objectMapper.writeValueAsString(loginResponseDto);

        response.setContentType("application/json");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(responseBody.getBytes());
        outputStream.flush();
    }
}