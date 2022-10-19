package com.innovation.stockstock.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/api/rank")
    public ResponseEntity<?> getReturnRank() {
        return ResponseEntity.ok().body(rankingService.getReturnRank());
    }
}
