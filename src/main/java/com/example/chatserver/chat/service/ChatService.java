package com.example.chatserver.chat.service;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.dto.MyChatListResDto;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatParticipantRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.repository.ReadStatusRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;

    public void saveMessage(Long roomId, ChatMessageDto chatMessageDto){

        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room cannot be found"));

        // 보낸사람 조회

        Member sender = memberRepository.findByEmail(chatMessageDto.getSenderEmail())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));

        // 메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(chatMessageDto.getMessage())
                .build();

        chatMessageRepository.save(chatMessage);

        // 사용자별로 읽음여부 저장
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

        for (ChatParticipant c : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .chatMessage(chatMessage)
                    .chatRoom(chatRoom)
                    .isRead(c.getMember().equals(sender))
                    .member(c.getMember()).build();

            readStatusRepository.save(readStatus );
        }

    }

    public void createGroupRoom(String chatRoomName){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()->new EntityNotFoundException("member can not be found"));


        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)

                .isGroupChat("Y")
                .build();

        chatRoomRepository.save(chatRoom);

        //채팅참여자로 개설자를 추가

        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();

        chatParticipantRepository.save(chatParticipant);


    }

    public List<ChatRoomListResDto> getGroupChatRooms(){

        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");

        List<ChatRoomListResDto> dtos =new ArrayList<>();

        for (ChatRoom c : chatRooms) {
            dtos.add(ChatRoomListResDto.builder()
                    .roomId(c.getId())
                    .roomName(c.getName()).build());

        }
        return dtos;





    }
    public void addParticipantToGroupChat(Long roomId){

        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room can not be found"));

        //맴버 조회
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("room can not be found"));

        if(chatRoom.getIsGroupChat().equals("N")){
            throw new IllegalArgumentException("그룹채팅이 아닙니다");
        }


        // 이미 참여자인지 검증하겠다
        Optional<ChatParticipant> chatParticipant =chatParticipantRepository.findByChatRoomAndMember(chatRoom,member);
        if(!chatParticipant.isPresent()){
            addParticipantToRoom(chatRoom,member);
        }
        // chatParticipant 객체 저장



    }

    public  void addParticipantToRoom(ChatRoom chatRoom,Member member){

        chatParticipantRepository.save(ChatParticipant.builder().member(member).chatRoom(chatRoom).build());

    }

    public List<ChatMessageDto> getChatHistory(Long roomId){

        // 특정 room 에 대한 message 조회

        // 내가 이 room 에 참여자가아니면 조회 못하게 한다 ;
        // 보안부분 5:20 이전 메시지 조회 참고
        // 내가 해당 채팅바의 참여자가 아닐경우 에러

        // 그리고 히스트로 허점 보안 16 분 때 부터  ㄱㄱ // 그래서   프론트에서 베어러 토큰 헤더에 넣어서 서버에 건내고  스톰프 핸들러에 검증하기 위해 추가로직 작성

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room can not be found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("member can not be found"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        boolean check=false;
        for (ChatParticipant c : chatParticipants) {
            if(c.getMember().equals(member)){
                check=true;
            }
        }
        if(!check)throw  new IllegalArgumentException("본인이 속하지않는 채팅방입니다 ");


        List<ChatMessage> chatMessages=chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
        List<ChatMessageDto> chatMessageDtos=new ArrayList<>();

        for (ChatMessage c : chatMessages) {
            ChatMessageDto dto = ChatMessageDto.builder()
                    .senderEmail(c.getMember().getEmail())
                    .message(c.getContent())
                    .build();
            chatMessageDtos.add(dto);
        }
        return chatMessageDtos;


    }
 public boolean isRoomParticipant(String email,Long roomId){
     Member member = memberRepository.findByEmail(email)
             .orElseThrow(() -> new EntityNotFoundException("member can not be found"));

     ChatRoom chatRoom = chatRoomRepository.findById(roomId)
             .orElseThrow(() -> new EntityNotFoundException("roomcan not be found"));
     List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

     for (ChatParticipant c : chatParticipants) {
         if(c.getMember().equals(member)){
             return true;
         }
     }
     return false;


 }

 public void messageRead(Long roomId){
     ChatRoom chatRoom = chatRoomRepository.findById(roomId)
             .orElseThrow(() -> new EntityNotFoundException("room can not be found"));

     String email = SecurityContextHolder.getContext().getAuthentication().getName();
     Member member = memberRepository.findByEmail(email)
             .orElseThrow(() -> new EntityNotFoundException("member can not be found"));

     List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom,member);

     for (ReadStatus status : readStatuses) {
         status.updateIdRead(true);
     }

 }

 public List<MyChatListResDto> getMyChatRooms(){
     String email = SecurityContextHolder.getContext().getAuthentication().getName();
     Member member = memberRepository.findByEmail(email)
             .orElseThrow(() -> new EntityNotFoundException("member can not be found"));

     List<ChatParticipant> chatParticipants= chatParticipantRepository.findAllByMember(member);

     List<MyChatListResDto> chatListResDtos=new ArrayList<>();

     for (ChatParticipant c : chatParticipants) {
         // 왜 member를 매개변수로 사용하나요?
         // 특정 멤버(member)와 특정 채팅방(chatRoom)에 대해 읽지 않은 메시지의 개수를 세고 있습니다.
         // 여기서 member는 현재 인증된 사용자의 Member 객체입니다.
         // 즉, 이 member 객체는 사용자가 현재 로그인한 상태에서의 Member 객체로,
         // 메소드에서 특정 사용자가 읽지 않은 메시지를 카운팅하려는 목적입니다.

         Long count=readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(),member);

         MyChatListResDto dto = MyChatListResDto.builder()

                 .roomId(c.getChatRoom().getId())
                 .roomName(c.getChatRoom().getName())
                 .isGroupChat(c.getChatRoom().getIsGroupChat())
                 .unReadCount(count)
                 .build();
         chatListResDtos.add(dto);
     }

     return chatListResDtos;



 }

 public void leaveGroupChatRoom(Long roomId){

        // 내가 나가면 맴버를 없애는게아니라 내가 나가도
       // 내가 쓴 메시지는 남아있음 따라서 participant 만 없앰
     // 실무에서는 delYN 컬럼을 두어서 삭제한다고 실제로 삭제하는식으로 하지않음
     ChatRoom chatRoom = chatRoomRepository.findById(roomId)
             .orElseThrow(() -> new EntityNotFoundException("room can not be found"));

     String email = SecurityContextHolder.getContext().getAuthentication().getName();
     Member member = memberRepository.findByEmail(email)
             .orElseThrow(() -> new EntityNotFoundException("member can not be found"));

     if(chatRoom.getIsGroupChat().equals("N")){
         throw new IllegalArgumentException("단체 채팅방이 아닙니다 ");
     }

    ChatParticipant c = chatParticipantRepository.findByChatRoomAndMember(chatRoom,
             member).orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다"));

     chatParticipantRepository.delete(c);

     List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
     if(chatParticipants.isEmpty()){
            // 쳇룸 삭제 -> 쳇 메시지 삭제 -> 리드스택어스 삭제 이런 연쇄 삭제를 위해 cascade 를 거름
        chatRoomRepository.delete(chatRoom);
     }

     // 근데 모두가 채팅방을 나간다 ? 그러면 다 없앰  : 메시지 읽음여부 채팅방 모든 엔티티 삭제



 }

    public Long getOrCreatePrivateRoom(Long otherMemberId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("member can not be found"));



        Member otherMember = memberRepository.findById(otherMemberId)
                .orElseThrow(() -> new EntityNotFoundException("member can not be found"));

        // 이건 별도로 내가 추가함
        if (member.equals(otherMember)) {
            throw new IllegalArgumentException("You cannot start a chat with yourself.");
        }

        // select *from A inner join B on A.room_id ==b.room_id
        // where A.id =myId and B.id=otherMemberId
        //
        //
        // 내가 내가 참여하고있는 룸 id 를 찾고 해당 roomId 에 상대 참여자 아이가있는지 찾는 거면
        // 내가 참여하고있는 채팅방의 룸이 많으면 많아질수록 너무 성능이 안좋아지기 때문에
        // jpql 로 inner 조인을 해서 찾아보자
        //두 사람의 roomId와 memberId를 비교하고, 해당 채팅방을 찾은 후, 없으면 새로운 채팅방을 개설하고 추가하는 방식

        // 쿼리문 성능을 위해서 jpql 로 짠다 //여기서는 jpa 의 메서드 룰 하고 상관없이 적당히 지어주면된다
        Optional<ChatRoom> chatRoom = chatParticipantRepository.findExistingPrivateRoom(member.getId(),
                otherMember.getId());

        // 나와 상대방이 1:1 채팅에 이미 참석하고있다면 해당 roomId return
        if(chatRoom.isPresent()){
           return chatRoom.get().getId();
        }




        // 만약에 1:1 채팅방이 없을경우 기존 채팅방 개설 후에
        ChatRoom newRoom = ChatRoom.builder()
                .name(member.getName() + "-" + otherMember.getName())
                .isGroupChat("N")
                .build();
        chatRoomRepository.save(newRoom);

        // 두 사람 모두 참여자로 새롭게 추가
        addParticipantToRoom(newRoom,member);
        addParticipantToRoom(newRoom,otherMember);

        return newRoom.getId();





    }













}
