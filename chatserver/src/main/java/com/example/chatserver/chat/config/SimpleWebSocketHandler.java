//package com.example.chatserver.chat.config;
//
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import org.springframework.web.socket.CloseStatus;
//
//import org.springframework.web.socket.TextMessage;
//
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//// TextWebSocketHandler 를 상송받아서 웹소켓 핸들러에 핸들러로 등록될수있는 객체로 만들었다고 보면됨
//// connect 로 웹소켓 연결요청이 들어왔을때 이를 처리할 클래스이다
//@Component
//@Slf4j
//public class SimpleWebSocketHandler  extends TextWebSocketHandler {
//    // 메서드는 TextWebSocketHandler 를 타고들어가서  AbstractWebSocketHandler 에서 가져와서 커스텀
//
//    // 연결된 세션 관리 : 스레드 safe 한 set 사용
//    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
//
//
//    // 이건 머냐면 연결이되면 set 자료구조에다가 저장
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.add(session);
//        log.info("Connected : " +session.getId());
//    }
//
//
//    // 사용자에게 메시지를 보내주는 메서드다
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        log.info("received message: " +payload);
//        //WebSocketSession(즉, 클라이언트와의 연결이 활성화된 세션)이 여전히 열려있는지 확인하는 메서드입니다.
//        // isOpen() 메서드는 해당 세션이 유효하고, 메시지를 보낼 수 있는 상태인지를 판별합니다.
//        for (WebSocketSession s : sessions) {
//            if(s.isOpen()){
//                s.sendMessage(new TextMessage(payload));
//
//            }
//        }
//
//    }
//
//
//    // 연결이 끝나면 set 자료구조에서 제외시켜줘야함
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.remove(session);
//        log.info("DisConnected : " +session.getId());
//    }
//
//
//}
