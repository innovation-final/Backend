package com.innovation.stockstock.stock.like;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeStockRepository extends JpaRepository<LikeStock, Long> {
    List<LikeStock> findByMemberId(Long id);

    void deleteByMemberIdAndStockId(Long memberId, String stockCode);
}
