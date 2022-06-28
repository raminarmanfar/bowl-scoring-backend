package com.armanfar.bowlscoring.controller;

import com.armanfar.bowlscoring.entity.BowlScore;
import com.armanfar.bowlscoring.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bowl")
public class ScoreingController {

    @Autowired
    private ScoringService scoringService;

    @GetMapping("/create-new-game")
    public ResponseEntity<String> startNewGame() {
        BowlScore bowlScore = scoringService.createNewGame();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("New Game has been created successfully.\n");
    }

    @GetMapping("/get-game-data")
    public BowlScore getGameData() {
        return scoringService.getGameData();
    }

    @GetMapping("/score/{score}")
    public BowlScore score(@PathVariable("score") short score) {
        return scoringService.score(score);
    }
}
