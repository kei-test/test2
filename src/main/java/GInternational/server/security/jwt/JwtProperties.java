package GInternational.server.security.jwt;

public interface JwtProperties {
    String SECRET = "ADMIN";
    int EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String HEADER_STRING_USERNAME = "username";
}