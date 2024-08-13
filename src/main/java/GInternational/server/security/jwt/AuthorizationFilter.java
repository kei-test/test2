package GInternational.server.security.jwt;

import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;

    public AuthorizationFilter(AuthenticationManager authenticationManager,UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(JwtProperties.HEADER_STRING);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request,response);
            return;
        }
        String token = request.getHeader(JwtProperties.HEADER_STRING)
                .replace(JwtProperties.TOKEN_PREFIX, "");

        try {
            //토큰 검증 (AuthenticationManager 필요 없음 )
            //내가 SecurityContext 에 직접 접근 -> 세션을 만들 때 자동으로 UserDetailsService 에 있는 loadByUsername 이 호출됨
            String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                    .getClaim("username").asString();

            if (username != null) {
                User user = userRepository.findByUsername(username);

                // JWT 토큰의 발행 시간(issuedAt) 가져오기
                Date issuedAtDate = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                        .getClaim("iat").asDate();
                LocalDateTime issuedAtLocalDateTime = issuedAtDate.toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();

                // 나노초를 명시적으로 설정
                LocalDateTime issuedAtWithMicros = issuedAtLocalDateTime.withNano(0);
                // 포맷터 정의 (마이크로초를 포함)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                // 문자열로 변환
                String issuedAtFormatted = issuedAtWithMicros.format(formatter);
                // 문자열을 다시 LocalDateTime으로 파싱
                LocalDateTime parsedIssuedAt = LocalDateTime.parse(issuedAtFormatted, formatter);

                // 사용자의 마지막 로그인 시간과 비교
                long secondsDifference = ChronoUnit.SECONDS.between(parsedIssuedAt, user.getLastVisit());
                if (Math.abs(secondsDifference) > 5) { // 2초 이상 차이가 나면 오류로 처리
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"다시 로그인하세요.\"}");
                    return;
                }

                //인증은 토큰 검증시 끝. 인증을 하기 위함이 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
                //아래 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장
                PrincipalDetails principalDetails = new PrincipalDetails(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        principalDetails,
                        null,
                        principalDetails.getAuthorities());

                //스프링 시큐리티의 세션에 강제 접근
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (TokenExpiredException e) {
            // 토큰 만료 시 처리
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"로그인 유효시간이 만료되었습니다\"}");
            return;
        } catch (JWTVerificationException e) {
            // 기타 JWT 검증 실패 처리
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }

        chain.doFilter(request,response);
    }
}