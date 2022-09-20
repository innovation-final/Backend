package com.innovation.stockstock;

import com.innovation.stockstock.controller.MemberController;

import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.Post;
import com.innovation.stockstock.repository.MemberRepository;
import com.innovation.stockstock.repository.PostRepository;
import com.innovation.stockstock.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockStockApplicationTests {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void insertMember(){
        for(int i=0;i<10;i++) {
            Member member = Member.builder().id(Long.valueOf(i)).email("test"+i+"@test.test").nickname("nick"+i).build();
            memberRepository.save(member);
        }
    }

    @Test
    void contextLoads() {
        for(int i=0;i<10;i++) {
            Post post = Post.builder().id(Long.valueOf(i)).member(memberRepository.findByEmail("test"+i+"@test.test").get()).title("title="+i).content("content="+i).build();
            postRepository.save(post);
        }
    }

}
