package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.DislikePost;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DislikeRepository extends JpaRepository<DislikePost, Long> {
    Optional<DislikePost> findByPostAndMember(Post post, Member member);
    boolean existsByMemberAndPost(Member member,Post post);

}
