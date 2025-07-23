package com.yoga.youjia.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT服务类
 *
 * 负责JWT令牌的生成、解析和验证
 */
@Service
public class JWTService {

    /**
     * JWT密钥和过期时间配置
     * 这些配置通常在application.properties或application.yml文件中定义。
     */
    @Value("${jwt.secret}")
    private String secretKey; // JWT密钥

    /**
     * -- GETTER --
     *  获取JWT过期时间
     *
     * @return 过期时间(毫秒)
     */
    @Getter
    @Value("${jwt.expirationTime}")
    private long expirationTime; // JWT过期时间（单位：毫秒）

    /**
     * 生成JWT令牌
     *
     * @param username 用户名
     * @return 生成的JWT令牌，格式为"Bearer xxxxxx"
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * 创建JWT令牌
     *
     * @param claims 令牌中的声明
     * @param subject 令牌的主题（通常是用户名）
     * @return 生成的JWT令牌，格式为"Bearer xxxxxx"
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return "Bearer " + token;
    }

    /**
     * 从JWT令牌中提取用户名
     *
     * @param token JWT令牌（可以包含或不包含Bearer前缀）
     * @return 用户名
     */
    public String extractUsername(String token) {
        token = removeBearer(token);
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从JWT令牌中提取过期时间
     *
     * @param token JWT令牌（可以包含或不包含Bearer前缀）
     * @return 过期时间
     */
    public Date extractExpiration(String token) {
        token = removeBearer(token);
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从JWT令牌中提取声明
     *
     * @param token JWT令牌
     * @param claimsResolver 声明解析函数
     * @return 解析后的声明
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从JWT令牌中提取所有声明
     *
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查JWT令牌是否已过期
     *
     * @param token JWT令牌
     * @return 如果令牌已过期，则返回true；否则返回false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    /**
     * 验证JWT令牌
     *
     * @param token JWT令牌（可以包含或不包含Bearer前缀）
     * @param username 用户名
     * @return 如果令牌有效，则返回true；否则返回false
     */
    public Boolean validateToken(String token, String username) {
        token = removeBearer(token);
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 移除Bearer前缀的工具方法
     *
     * @param token 可能包含Bearer前缀的token
     * @return 移除Bearer前缀后的纯token
     */
    private String removeBearer(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

}
