package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.LikeStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeStockRepository extends JpaRepository<LikeStock, Long> {
    List<LikeStock> findByMemberId(Long id);

    void deleteByMemberIdAndStockId(Long memberId, String stockCode);
}
