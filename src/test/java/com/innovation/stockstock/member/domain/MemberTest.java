package com.innovation.stockstock.member.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.innovation.stockstock.member.domain.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@Commit
class MemberTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Member member1 = new Member("1@test.com", "1");
        Member member2 = new Member("2@test.com", "2");
        Member member3 = new Member("3@test.com", "3");
        Member member4 = new Member("4@test.com", "4");
        Member member5 = new Member("5@test.com", "5");

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
    }

    @Test
    public void testEntityByJpql() {
        List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("member = " + member.getNickname());
        }
    }

    @Test
    public void testEntityByQuerydsl() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.nickname.eq("4"))
                .fetchOne();

        assertThat(findMember.getEmail()).isEqualTo("4@test.com");
    }

}