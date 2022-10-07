package com.innovation.stockstock.post.repository;

import com.innovation.stockstock.post.domain.DislikePost;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DislikeRepository extends JpaRepository<DislikePost, Long> {
    Optional<DislikePost> findByPostAndMember(Post post, Member member);
    boolean existsByMemberAndPost(Member member,Post post);

}
