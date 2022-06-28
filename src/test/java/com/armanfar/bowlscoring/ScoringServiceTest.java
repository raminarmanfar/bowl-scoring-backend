package com.armanfar.bowlscoring;

import com.armanfar.bowlscoring.entity.BowlScore;
import com.armanfar.bowlscoring.exceptions.ScoreIsOutOfRangeException;
import com.armanfar.bowlscoring.service.ScoringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static com.armanfar.bowlscoring.entity.RoundStatusEnum.NULL;
import static com.armanfar.bowlscoring.entity.RoundStatusEnum.STRIKE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ScoringServiceTest {
    private int i = 0;
    private BowlScore bowlScore;

    @Autowired
    ScoringService scoringService;

    @Test
    @DisplayName("Test maximum possible grade (Max grade is 300).")
    @Transactional
    void testMaximumScore() {
        i = 0;
        do {
            bowlScore = scoringService.score(10);
            if (i < 10) {
                assertEquals(STRIKE, bowlScore.getFrameByIndex(i).getRoundStatus(), "Round status should be STRIKE");
            }
            i++;
        } while (!bowlScore.isGameOver());
        assertEquals(12, i, "Number of turns played should be 12");
        assertTrue(bowlScore.isLastFrameBlockVisible(), "Last frame should have 3 rounds since it is STRIKE");
        assertEquals(300, bowlScore.getFrameScoreByIndex(9), "Maximum score (300) should be gained");
    }

    @Test
    @DisplayName("Test minimum possible grade (Min grade is 0).")
    @Transactional
    void testMinimumScore() {
        i = 0;
        do {
            bowlScore = scoringService.score(0);
            if (i < 10) {
                assertEquals(NULL, bowlScore.getFrameByIndex(i).getRoundStatus(), "Round status should be NULL -> Zero");
            }
            i++;
        } while (!bowlScore.isGameOver());
        assertEquals(20, i, "Number of turns played should be 20");
        assertFalse(bowlScore.isLastFrameBlockVisible(), "Last frame should not have 3rd round since it is not STRIKE or SPARE");
        assertEquals(0, bowlScore.getFrameScoreByIndex(9), "Minimum score (0) should be gained");
    }

    @Test
    @DisplayName("Test Spare scores.")
    @Transactional
    void testSpareScore() {
        i = 0;
        do {
            bowlScore = scoringService.score(5);
            if (i < 10) {
                assertEquals(NULL, bowlScore.getFrameByIndex(i).getRoundStatus(), "Round status should be NULL");
            }
            i++;
        } while (!bowlScore.isGameOver());
        assertEquals(21, i, "Number of turns in all SPARE status played should be 21");
        assertTrue(bowlScore.isLastFrameBlockVisible(), "Last frame should have 3 rounds since it is SPARE");
        assertEquals(150, bowlScore.getFrameScoreByIndex(9), "Should gain 150 score on all SPARE with always scored 5");
    }

    @Test
    @DisplayName("Test non-SPARE and non-STRIKE status.")
    @Transactional
    void testNonSpareNonStrikeStatus() {
        i = 0;
        do {
            bowlScore = scoringService.score(4);
            if (i < 10) {
                assertEquals(NULL, bowlScore.getFrameByIndex(i).getRoundStatus(), "Round status should be NULL");
            }
            i++;
        } while (!bowlScore.isGameOver());
        assertEquals(20, i, "Number of turns in all SPARE status played should be 20");
        assertFalse(bowlScore.isLastFrameBlockVisible(), "Last frame should not have 3rd round since it is not STRIKE or SPARE");
        assertEquals(80, bowlScore.getFrameScoreByIndex(9), "Should gain score on all SPARE with always scored 4");
    }

    @Test
    @DisplayName("Score should not out of the range (0 - 10)")
    void scoreRangeTest() {
        ScoreIsOutOfRangeException scoreIsOutOfRangeException1 =
                assertThrows(ScoreIsOutOfRangeException.class, () -> scoringService.score(12));
        assertEquals("Score is out of range (0 - 10)!", scoreIsOutOfRangeException1.getMessage());

        ScoreIsOutOfRangeException scoreIsOutOfRangeException2 =
                assertThrows(ScoreIsOutOfRangeException.class, () -> scoringService.score(-3));
        assertEquals("Score is out of range (0 - 10)!", scoreIsOutOfRangeException2.getMessage());

        assertNull(bowlScore, "Object should be null since the score is grater than possible grade 10");
    }

    @Test
    @DisplayName("Should not possible to play game since it is over.")
    @Transactional
    void gameOverTest() {
        do {
            bowlScore = scoringService.score(4);
            if (i < 10) {
                assertEquals(NULL, bowlScore.getFrameByIndex(i).getRoundStatus(), "Round status should be NULL");
            }
        } while (!bowlScore.isGameOver());

        final int newScore = 5;
        bowlScore = scoringService.score(newScore);
        assertNotEquals(newScore, bowlScore.getFrameScoreByIndex(9),
                "The last play has not counted since the game is already over.");
        assertTrue(bowlScore.isGameOver(), "Game should be over");
    }
}
