package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.service.ChatService;
import com.example.chatserver.chat.service.RedisPubSubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
@Controller
@Slf4j
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

    private final RedisPubSubService PubSubService;


    //     // 방법 1. MessageMapping(수신)거ㅣ SendTo(topic에 메시지 전달) 한꺼번에 처리
//    @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지 발생시 MessageMapping 이 수신
//    @SendTo("/topic/{roomId}") // 해당 roomId 에 메시지를 발행하여 구독중인 클라이언트에게 메시지 전송
//    //@DestinationVariable :@MessageMapping 로 정의된 webSocket Controller 내에서만 사용
//    public String sendMessage(@DestinationVariable Long roomId,String message){
//
//        log.info("message : " +message);
//
//        return message;
//
//    }

    // 방법 2 MessageMapping 어노테이션만 활용.
    // publish/{roomId}는 클라이언트가 메시지를 발행하는 경로입니다.
    // topic/{roomId}는 클라이언트가 메시지를 구독하는 경로입니다.

    //그리고, 서버에서 @MessageMapping("/{roomId}") 메서드는 **/publish/{roomId}**로 발행된 메시지를 처리하는 메서드입니다.
    // /topic/{roomId}는 이 메서드에서 전송되는 주제입니다.
    @MessageMapping("/{roomId}")

    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto)
            throws JsonProcessingException {

        log.info("message : " + chatMessageDto.getMessage());

        chatService.saveMessage(roomId, chatMessageDto);
        chatMessageDto.setRoomId(roomId);

     //   messageTemplate.convertAndSend("/topic/"+roomId, chatMessageDto);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(chatMessageDto);

        PubSubService.publish("chat",message);
    }





}
