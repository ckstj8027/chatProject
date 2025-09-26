//package com.example.chatserver.chat.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
////Spring이 **@Configuration**을 통해 WebSocketConfig 클래스를 설정 빈으로 등록합니다.
//@Configuration
////@EnableWebSocket**을 사용하여 Spring은 **WebSocketConfigurer**를 구현한 클래스가 있다는 것을 인식합니다.
//@EnableWebSocket
//
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    // 순서 a 와 b 가 있고 서버가있을때 a 가 서버에 connect 요청을 보낸다 connect 요청은 http 요청이 아니다 따라서 컨트롤러에서 처리되지않는다
//    // 따라서 여기 설정은 "/connect"요청이 들어오면 내가 처리 하겠다 라는 핸들러를 설정하는곳이라고 보면된다
//    // 핸들러는 유저를 메모리에 저장한다 여기서 메모리는 set자료 구조다 예를들어 a 가 메시지를 보내면 해당 메시지를 메모리에 등록된 모든 사람한데 보내는 식이다
//    // 추가로 connect 요청은 http 요청이 아니다 따라서 필터에서 막히게된다 아무리 connect 요청에 토큰 값을 넣어 같이 보낸다 하더라고 http 요청이 아니기때문에
//    // filter 에서 토큰을꺼내 Authentication 객체를 만드는 작업을 해줄 수 없다
//    // 따라서 filter 에서 "/connect"인경우에는 인증 처리를 하지않겠다 처리를 해야합니다
//    // 그러면 여기서 질문 서버가 a 가 로그인한 사용자인지 아닌지는 어캐알아 ?
//    // 그러면 마음대로 "/connect" 요청보내서 서버에 연결 보낼 수 있겠네 ?
//    // 좋은질문 여기서는 흐름 파악용도 하지만 추우에 stomp 로 구현할때
//    // websocket 영역에서 사용자의 요청에 토큰이 들었는지 아닌지를 별도 코드를 만들어서 진행할거임
//    // 또한 마찬가지로 http 요청이 아니기에 cors 설정도 별도로 해줘야함
//
//    private final  SimpleWebSocketHandler simpleWebSocketHandler;
//
//    // connect url로 websocket 연결 요청이 들어오면 , 핸글러 클래스가 처리 한다
//    //
//    // gpt 피셜
//    //@EnableWebSocket 어노테이션은 내부적으로 WebSocketConfigurer를 구현한 클래스가 있으면 그 클래스를 찾아서 registerWebSocketHandlers 메서드를 자동으로 호출합니다.
//    //즉 registerWebSocketHandlers는 Spring WebSocket 설정이 초기화될 때 자동으로 실행되기 때문에, 개발자가 명시적으로 호출할 필요가 없습니다
//
//    //
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(simpleWebSocketHandler ,"/connect")
//                //http://localhost:3000는 cors 예외처리해주겠다
//                //securityconfig 에서의 cors 예외는 http 요청에 대한 예외이다 ]
//                // 따라서 웹소켓에서는 별도의 cors 예외가 필요하다 !
//                .setAllowedOrigins("http://localhost:3000");
//    }
//}
