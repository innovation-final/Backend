package com.innovation.stockstock.post.repository;

import com.innovation.stockstock.post.domain.LikePost;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikePost,Long> {


    Optional<LikePost> findByPostAndMember(Post post, Member member);
    boolean existsByMemberAndPost(Member member,Post post);
}
