package com.innovation.stockstock.member.repository;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Member findByAccount(Account account);
}
