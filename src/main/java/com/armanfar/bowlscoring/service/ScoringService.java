package com.armanfar.bowlscoring.service;

import com.armanfar.bowlscoring.entity.BowlScore;
import com.armanfar.bowlscoring.repository.ScoringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {
    @Autowired
    private ScoringRepository scoringRepository;

    public BowlScore createNewGame() {
        scoringRepository.deleteAll();
        return scoringRepository.save(BowlScore.build());
    }

    public BowlScore getGameData() {
        return scoringRepository.findById(1L).orElse(null);
    }

    public BowlScore score(short score) {
        return null;
    }
}
