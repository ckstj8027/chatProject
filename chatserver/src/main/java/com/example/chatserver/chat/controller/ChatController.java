package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.dto.MyChatListResDto;
import com.example.chatserver.chat.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(@RequestParam String roomName ){
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();

    }
// 그룹 채팅 목록조회
    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupChatRooms(){
        List<ChatRoomListResDto> chatRooms = chatService.getGroupChatRooms();

        return new ResponseEntity<>(chatRooms, HttpStatus.OK);


    }


    // 그룹 채팅방 참여


    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long roomId){

        chatService.addParticipantToGroupChat(roomId);

        return ResponseEntity.ok().build();
    }



    // 이전 메시지 조회

    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId){
        List<ChatMessageDto> chatMessageDtos=chatService.getChatHistory(roomId);
        return new ResponseEntity<>(chatMessageDtos,HttpStatus.OK);

    }
    // 채팅 메시지 읽음 처리 api
    // 사실 이건 프론트 엔드가 중요 어떤 시점에 이 api 를 호출하느냐가 중요
    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> messageRead(@PathVariable Long roomId){
        chatService.messageRead(roomId);
        return ResponseEntity.ok().build();

    }

    // 내 채팅방 목록 조회 :roomId , roomName ,그룹채팅여부 ,메시지 읽음 개수
    @GetMapping("/my/rooms")
    public ResponseEntity<?> getMyChatRooms(){
        List<MyChatListResDto> myChatListResDtos =chatService.getMyChatRooms();
        return new ResponseEntity<>(myChatListResDtos, HttpStatus.OK);

    }


    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<?> leaveGroupChatRoom(@PathVariable Long roomId){

        chatService.leaveGroupChatRoom(roomId);

        return ResponseEntity.ok().build();

    }

    // 개인 채팅방 개설 또는 기존roomId return 서비스
    @PostMapping("/room/private/create")
    public ResponseEntity<?> getOrCreatePrivateRoom(@RequestParam Long otherMemberId){

        Long roomId=chatService.getOrCreatePrivateRoom(otherMemberId);
        return new ResponseEntity<>(roomId,HttpStatus.OK);

    }




}
