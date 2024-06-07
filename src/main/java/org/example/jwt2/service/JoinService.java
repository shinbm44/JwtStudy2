package org.example.jwt2.service;

import lombok.RequiredArgsConstructor;
import org.example.jwt2.Entity.UserEntity;
import org.example.jwt2.dto.JoinDTO;
import org.example.jwt2.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void joinprocess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        boolean Exists = userRepository.existsByUsername(username);

        if(!Exists) {
            return;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.setRoll("ROLE_ADMIN");

        userRepository.save(userEntity);
    }


}
