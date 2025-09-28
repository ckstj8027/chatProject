package com.example.chatserver.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveReqDto {
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    @NotBlank(message = "이메일은 필수입니다")
    private String email;
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
