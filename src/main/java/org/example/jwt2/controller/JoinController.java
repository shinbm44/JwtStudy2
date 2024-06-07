package org.example.jwt2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jwt2.dto.JoinDTO;
import org.example.jwt2.service.JoinService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public String join(@Valid @RequestBody JoinDTO joinDTO ) {

        joinService.joinprocess(joinDTO);

        return "ok";
    }
}
