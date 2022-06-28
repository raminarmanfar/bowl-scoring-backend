package com.armanfar.bowlscoring.repository;

import com.armanfar.bowlscoring.entity.BowlScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoringRepository extends JpaRepository<BowlScore, Long> {
}
