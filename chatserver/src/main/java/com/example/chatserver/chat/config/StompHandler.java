package com.example.chatserver.chat.config;


import com.example.chatserver.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Value("${jwt.secretKey}")
    private String secretKey;
    private final ChatService chatService;

    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);


        if(StompCommand.CONNECT==accessor.getCommand()){
            log.info("connect 요청시 토큰 유효성 검증");
            String bearerToken =accessor.getFirstNativeHeader("Authorization");
            String token=bearerToken.substring(7);
            //토큰검증
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("토큰 검증 완료 ");

        }

        //보안 검증 추가
        if(StompCommand.SUBSCRIBE==accessor.getCommand()){
            log.info("subscribe 검증");
            String bearerToken =accessor.getFirstNativeHeader("Authorization");
            String token=bearerToken.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String email = claims.getSubject();
            String roomId = accessor.getDestination().split("/")[2];

            if(!chatService.isRoomParticipant(email,Long.parseLong(roomId) )){
                throw new AuthenticationServiceException("해당 room에 권한이 없습니다");
            }

        }



        return message;
    }
}
