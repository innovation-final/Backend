package com.innovation.stockstock.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/api/rank/return")
    public ResponseEntity<?> getReturnRank() {
        return ResponseEntity.ok().body(rankingService.getReturnRank());
    }

    @GetMapping("/api/rank/like")
    public ResponseEntity<?> getLikeRank() {
        return ResponseEntity.ok().body(rankingService.getLikeRank());
    }
}
