package com.example.chatserver.chat.service;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPubSubService implements MessageListener {

    private  final StringRedisTemplate stringRedisTemplate;

    private final SimpMessageSendingOperations messageTemplate;

    public void publish(String channel ,String message){
        stringRedisTemplate.convertAndSend(channel,message);
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload=new String (message.getBody());
        //그니까 message.getBody()를 하고 스트링으로 바꾸면 {
        //  "roomId": 123,
        //  "senderId": 1,
        //  "message": "Hello, World!"
        //} 이거

        ObjectMapper objectMapper = new ObjectMapper();
        //이걸 다시  자바객체로 readValue

        try {
            ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
            // 레디스 도입전에는 stompController 에서 메시지 매핑이 받자마자 토픽에다 발행했었는데 레디스에다 퍼블리시한 이후  발행하는 식으로 구조 변경
            //그러면 이 서버를 subscribe 하는 서버는 메시지를 수신한다
            messageTemplate.convertAndSend("/topic/"+chatMessageDto .getRoomId(), chatMessageDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
