package org.example.jwt2.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class JoinDTO {

    @NotEmpty(message = "유저 이름 입력은 필수입니다.")
    private String username;

    @NotEmpty(message = "비밀 번호 입력은 필수입니다.")
    private String password;

}
