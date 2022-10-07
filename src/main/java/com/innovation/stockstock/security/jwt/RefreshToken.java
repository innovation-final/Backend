package com.innovation.stockstock.security.jwt;

import com.innovation.stockstock.common.dto.Timestamped;
import com.innovation.stockstock.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends Timestamped {

    @Id
    private String email;

    private String token;

    public RefreshToken(Member member, String refreshToken) {
        this.email = member.getEmail();
        this.token = refreshToken;
    }

    public void updateToken(String refreshToken) {
        this.token = refreshToken;
    }

}
