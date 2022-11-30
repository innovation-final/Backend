package com.innovation.stockstock.post.repository;

import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.comment.service.CommentService;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.member.repository.MemberRepository;
import com.innovation.stockstock.post.domain.LikePost;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.post.dto.PostRequestDto;
import com.innovation.stockstock.post.service.LikeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Optional;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;

    @Test
    @Commit
    public void updateBeforeMemberDelete() {
        Member member = new Member("1", "1");
        em.persist(member);

        Post post = new Post(new PostRequestDto("", "", ""), null);
        em.persist(post);

        em.persist(Comment.builder()
                .content("")
                .post(post)
                .member(member)
                .build());
        post.updateCommentNum(true);

        em.persist(LikePost.builder()
                .post(post)
                .member(member)
                .build());
        post.updateLikes(true);

        // 벌크 연산
        postRepository.updateCommentNum(member);
        postRepository.updateLikeNum(member);

        // 초기화
        em.flush();
        em.clear();

        Optional<Post> findPost = postRepository.findById(post.getId());

        Assertions.assertThat(findPost.get().getCommentNum()).isEqualTo(0);
        Assertions.assertThat(findPost.get().getLikes()).isEqualTo(0);
    }
}