package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByKakaoId(Long kakaoId);
}
