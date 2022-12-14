package com.innovation.stockstock.account.repository;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findByMember(Member member);

    List<Account> findFirst10ByOrderByTotalReturnRateDesc();

    void deleteByMember(Member member);
}
