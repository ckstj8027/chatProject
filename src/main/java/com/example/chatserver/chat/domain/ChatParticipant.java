package com.example.chatserver.chat.domain;

import com.example.chatserver.common.domain.BaseTimeEntity;
import com.example.chatserver.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatParticipant  extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    // 하나의 채팅방에는 여러 명의 회원이 ChatParticipant 할 수 있다
    // 여러 명의 ChatParticipant가 하나의 ChatRoom을 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="chat_room_id",nullable = false)
    private ChatRoom chatRoom;


    // 하나의 회원은 여러개의 방 참여자가 될 수 있다 즉 어디든 참가 할 수 있다
    //여러 개의 ChatParticipant가 하나의 Member를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id",nullable = false)
    private Member member;


}
