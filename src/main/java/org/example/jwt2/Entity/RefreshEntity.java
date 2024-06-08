package org.example.jwt2.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username; // 하나의 유저가 여러 개의 토큰을 들고 있을 수 있으므로 unique 설정 안함
    private String refresh;// 유저가 들고 있는 토큰
    private String expiration;
}
