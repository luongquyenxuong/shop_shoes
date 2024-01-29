package com.example.shoes_shop.security;


import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final UserDetailsService userDetailsService;
    //Token có hạn trong 7 ngày kể từ thời điểm tạo, thời gian tính theo giây
    @Value("${jwt.duration}")
    public Integer duration;

    //Lấy giá trị key được cấu hình trong file application.properties
    //Key này được dùng dể mã hóa và giải mã
    @Value("${jwt.secret}")
    private String secret;


    //Sinh token
    public String generateToken(UserDetails userDetails) {
        //Lưu thông tin Authorities của user vào claims
        Map<String, Object> claims = new HashMap<>();

        //1. Định nghĩa các claims: issuer, expiration, subject, id
        //2. Mã hóa token sử dụng thuật toán HS512 và key bí mật
        //3. Convert thành chuỗi URL an toàn
        //4. Cộng chuỗi đã sinh ra với tiền tố Bearer

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + duration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //Lấy thông tin được lưu trong token
    public Claims getClaimsFromToken(String token) {
        //Kiểm tra token có bắt đầu bằng tiền tố
        if (token == null) return null;
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token" + ex.getMessage(), ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token" + ex.getMessage(), ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token" + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty." + ex.getMessage(), ex);
        }
        return false;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(Claims claims) {
        String username = claims.getSubject();

        if (username != null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);

            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        return null;
    }

}
