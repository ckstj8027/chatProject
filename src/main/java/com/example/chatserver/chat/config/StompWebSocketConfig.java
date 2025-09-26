package com.example.chatserver.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Value("${websocket.allowed-origins}")
    private String allowedOrigins;

    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/connect")
                .setAllowedOrigins(allowedOrigins.split(","))
                // ws:// 가아닌 http://  엔드포인트를 사용할 수 있게 해주는 sockJs 라이브러리를 통한 요청을 허용하는 설정이다 .
                // 이러한 sockJs 라이브러리를 프론트에서 사용하게 허용해주겠다
                .withSockJS();
    }

    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 발신할때 /publish/1 형태로 메시지 발행해야함을  설정
        //  /publish로 시작하는 url패턴으로 멧지ㅣ가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅된다
        registry.setApplicationDestinationPrefixes("/publish");

        // 메시지 수신할때 /topic/1 형태로 메시지를 수신 해야함을 설정
        registry.enableSimpleBroker("/topic");
    }

    // 웹소켓요청(connect ,subscribe, disconnect) 등의 요청시에는 http 헤더등 http 메시지를 넣어올수있고 , 이를 인터셉터를 통해 가로채 토큰을 검증할수있음
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(stompHandler);

    }
}
