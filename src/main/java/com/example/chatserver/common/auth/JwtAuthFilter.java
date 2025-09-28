package com.example.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private  String secretKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse= (HttpServletResponse) response;

        // 인증이 필요 없는 경로는 바로 통과
        String path = httpServletRequest.getRequestURI();
        if (path.equals("/health") ||
            path.equals("/member/create") ||
            path.equals("/member/doLogin") ||
            path.startsWith("/connect/")) {
            chain.doFilter(request, response);
            return;
        }

        String token =httpServletRequest.getHeader("Authorization");

        try {
            if(token!=null){
                if(!token.substring(0,7).equals("Bearer ")){
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }
                String jwtToken = token.substring(7);

                // 토큰 검증 및 claims 추출 그중 페이로드 부분을 꺼냇다 우리가 이전에 너논 이메일과 롤 그 부분 ㅇㅋ ?
                Claims claims=    Jwts.parser()
                        .setSigningKey(secretKey.getBytes())
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();


                ArrayList<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_"+claims.get("role")));
                UserDetails userDetails=new User(claims.getSubject(),"",authorities );

                Authentication authentication=new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities())   ;


                SecurityContextHolder.getContext().setAuthentication(authentication);


            }




        }


        catch (Exception e){
            e.printStackTrace();
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("invalid token");
            return;
        }
        chain.doFilter(request,response);







    }
}
