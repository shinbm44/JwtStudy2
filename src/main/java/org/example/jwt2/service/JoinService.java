package org.example.jwt2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt2.Entity.UserEntity;
import org.example.jwt2.dto.JoinDTO;
import org.example.jwt2.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Service
@Slf4j
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public void joinprocess(@RequestBody JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        log.info("user2={}", joinDTO.getUsername(), joinDTO.getPassword());
        Boolean Exists = userRepository.existsByUsername(username);

        if(Exists) {
            log.info("Exists={}", Exists);
            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return;
        }
        UserEntity data = new UserEntity();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");


        log.info("user={}", data.getId(), data.getPassword(), data.getRole());
        log.info("------------------------------------------");


        userRepository.save(data);
    }


}
