package com.example.chatserver.member.controller;

import com.example.chatserver.common.auth.JwtTokenProvider;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberListResDto;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.service.MemberService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")
    public HttpEntity<?> createMember(@RequestBody MemberSaveReqDto memberSaveReqDto){
        Member member=memberService.create(memberSaveReqDto);

        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    private HttpEntity<?> doLogin(@RequestBody MemberLoginReqDto memberLoginReqDto){

        // 이메일 ,비번 검증하고
       Member member= memberService.login(memberLoginReqDto);

        // 일치하면 jwt 토큰 발행 하겠다
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String,Object> loginInfo=new HashMap<>();
        loginInfo.put("id",member.getId());
        loginInfo.put("token",jwtToken);
        return new ResponseEntity<>(loginInfo,HttpStatus.OK);

    }

    @GetMapping("/list")
    public  ResponseEntity<?> memberList(){
       List<MemberListResDto> dtos= memberService.findAll();
       return new ResponseEntity<>(dtos,HttpStatus.OK);
    }

}
