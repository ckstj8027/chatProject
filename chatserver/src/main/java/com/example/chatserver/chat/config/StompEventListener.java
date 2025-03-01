package com.example.chatserver.chat.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

// 스프링과 stomp 는 기본적으로 세션관리를 내부적으로 처리한다
//연결 /해제 이벤트를 기록, 연결 세션수를 실시간으로 확인할 목적으로 이벤트 리스너를 생성 -> 로그, 디버깅 목적
@Component
@Slf4j
public class StompEventListener {

    private final Set<String> sessions= ConcurrentHashMap.newKeySet();

    @EventListener
    public void connectHandle(SessionConnectedEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.add(accessor.getSessionId());
        log.info("connect session Id : "+accessor.getSessionId());
        log.info("total sessions : " +sessions.size());
    }


    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.remove(accessor.getSessionId());
        log.info("disconnect session Id : "+accessor.getSessionId());
        log.info("total sessions : " +sessions.size());
    }


}
