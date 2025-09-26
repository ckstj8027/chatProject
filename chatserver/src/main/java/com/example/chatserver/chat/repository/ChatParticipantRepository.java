package com.example.chatserver.chat.repository;

import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant,Long> {

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);
    Optional<ChatParticipant>  findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    List<ChatParticipant> findAllByMember(Member member);

    //SELECT *
    //FROM chat_participant cp1
    //JOIN chat_participant cp2 ON cp1.chat_room_id = cp2.chat_room_id
    //WHERE cp1.member_id = ? AND cp2.member_id = ?;
    // ? 는 동적 바인딩 변수
    //결론 SELECT cp1.chatRoom 를 리턴 cp1이든 cp2 든 상관없음 어처피 같은 값이니까
    @Query("SELECT cp1.chatRoom FROM ChatParticipant cp1 JOIN ChatParticipant cp2 ON cp1.chatRoom.id=cp2.chatRoom.id WHERE cp1.member.id=:myId  AND cp2.member.id=:otherMemberId AND cp1.chatRoom.isGroupChat='N'")
    Optional<ChatRoom> findExistingPrivateRoom(@Param("myId") Long myId,@Param("otherMemberId") Long otherMemberId);
}
