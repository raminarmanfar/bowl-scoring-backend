package com.armanfar.bowlscoring.service;

import com.armanfar.bowlscoring.entity.BowlScore;
import com.armanfar.bowlscoring.entity.Frame;
import com.armanfar.bowlscoring.exceptions.ScoreIsOutOfRangeException;
import com.armanfar.bowlscoring.repository.ScoringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.armanfar.bowlscoring.entity.RoundStatusEnum.*;
import static com.armanfar.bowlscoring.entity.StepEnum.*;

@Service
public class ScoringService {
    private final int MAX_SCORE = 10;
    public final int LAST_FRAME_INDEX = 9;
    public final int FRAME_COUNT = 10;

    @Autowired
    private ScoringRepository scoringRepository;

    public BowlScore createNewGame() {
        scoringRepository.deleteAll();
        BowlScore bowlScore = new BowlScore(1);
        List<Frame> frames = Frame.buildFrames(bowlScore, FRAME_COUNT);
        bowlScore.setFrames(frames);
        return scoringRepository.save(bowlScore);
    }

    public BowlScore getGameData() {
        return scoringRepository.findById(1).orElse(null);
    }

    public BowlScore score(int score) {
        if (score < 0 || score > MAX_SCORE) {
            throw new ScoreIsOutOfRangeException("Score is out of range (0 - 10)!");
        }
        BowlScore bowlScore = getGameData();
        if (bowlScore == null) {
            // throw new GameIsNotCreatedException("Game has not been created.");
            bowlScore = createNewGame();
        }

        if (bowlScore.isGameOver()) {
            return bowlScore;
        }

        Frame currentFrame = bowlScore.getCurrentFrame();
        Frame previousFrame = bowlScore.getPreviousFrame();

        if (previousFrame != null && !previousFrame.isScored() && previousFrame.getRoundStatus() == SPARE) {
            previousFrame.setScored(true);
            int frameScore = previousFrame.getFrameScore() + MAX_SCORE + score +
                    bowlScore.getFrameScoreByIndex(previousFrame.getId() - 1);
            previousFrame.setFrameScore(frameScore);
        }

        if (bowlScore.getCurrentFrameIndex() == LAST_FRAME_INDEX) {
            scoreLastFrame(score, bowlScore, currentFrame);
        } else if (score == MAX_SCORE) {
            currentFrame.setRoundStatus(bowlScore.getCurrentRound() == FIRST ? STRIKE : SPARE);
            bowlScore.setKeypadValueThreshold(MAX_SCORE);
            bowlScore.moveNextFrame();
            bowlScore.setGameOver(bowlScore.getCurrentFrameIndex() == MAX_SCORE);
            bowlScore.setCurrentRound(FIRST);
        } else {
            switch (bowlScore.getCurrentRound()) {
                case FIRST -> {
                    bowlScore.setKeypadValueThreshold(MAX_SCORE - score);
                    currentFrame.setFirstRoundScore(score);
                }
                case SECOND -> {
                    bowlScore.setKeypadValueThreshold(MAX_SCORE);
                    if (score + currentFrame.getFirstRoundScore() == MAX_SCORE) {
                        currentFrame.setRoundStatus(SPARE);
                    } else {
                        currentFrame.setSecondRoundScore(score);
                        this.scoreStrikes(bowlScore);
                        currentFrame.setFrameScore(score + currentFrame.getFirstRoundScore() +
                                bowlScore.getFrameScoreByIndex(bowlScore.getCurrentFrameIndex() - 1));
                        currentFrame.setScored(true);
                    }
                    bowlScore.setGameOver(bowlScore.moveNextFrame() == FRAME_COUNT);
                }
            }
            bowlScore.setCurrentRound(bowlScore.getCurrentRound() == FIRST ? SECOND : FIRST);
        }

        this.scoreStrikes(bowlScore);
        return scoringRepository.save(bowlScore);
    }

    private void scoreStrikes(BowlScore bowlScore) {
        bowlScore.getFrames().forEach(frame -> {
            if (!frame.isScored() && frame.getRoundStatus() == STRIKE) {
                final int previousFrameScore = bowlScore.getFrameScoreByIndex(frame.getId() - 1);
                final Frame nextFrame = bowlScore.getFrameByIndex(frame.getId() + 1);
                if (frame.getId() == LAST_FRAME_INDEX - 1 && nextFrame != null &&
                        nextFrame.getFirstRoundScore() > -1 && nextFrame.getSecondRoundScore() > -1) {
                    frame.setScored(true);
                    frame.setFrameScore(MAX_SCORE + previousFrameScore + nextFrame.getFirstRoundScore() + nextFrame.getSecondRoundScore());
                } else {
                    frame.setFrameScore(MAX_SCORE + previousFrameScore);
                    if (nextFrame != null) {
                        switch (nextFrame.getRoundStatus()) {
                            case STRIKE:
                                final Frame secondNextFrame = bowlScore.getFrameByIndex(nextFrame.getId() + 1);
                                if (secondNextFrame != null) {
                                    if (secondNextFrame.getRoundStatus() == STRIKE) {
                                        frame.setFrameScore(frame.getFrameScore() + 2 * MAX_SCORE);
                                        frame.setScored(true);
                                    } else if (secondNextFrame.getFirstRoundScore() > -1) {
                                        frame.setFrameScore(frame.getFrameScore() + MAX_SCORE + secondNextFrame.getFirstRoundScore());
                                        frame.setScored(true);
                                    }
                                }
                                break;
                            case SPARE:
                                frame.setScored(true);
                                frame.setFrameScore(frame.getFrameScore() + MAX_SCORE);
                                break;
                            default:
                                if (nextFrame.getFirstRoundScore() > -1 && nextFrame.getSecondRoundScore() > -1) {
                                    frame.setScored(true);
                                    frame.setFrameScore(MAX_SCORE + nextFrame.getFirstRoundScore() + nextFrame.getSecondRoundScore() + previousFrameScore);
                                }
                        }
                    }
                }
            }
        });
    }

    private void scoreLastFrame(int score, BowlScore bowlScore, Frame currentFrame) {
        switch (bowlScore.getCurrentRound()) {
            case FIRST -> {
                if (score == MAX_SCORE) {
                    bowlScore.setLastFrameBlockVisible(true);
                    currentFrame.setRoundStatus(STRIKE);
                } else {
                    bowlScore.setKeypadValueThreshold(MAX_SCORE - score);
                }
                currentFrame.setFirstRoundScore(score);
                bowlScore.setCurrentRound(SECOND);
            }
            case SECOND -> {
                currentFrame.setSecondRoundScore(score);
                scoreStrikes(bowlScore);
                final boolean gameOver = currentFrame.getRoundStatus() == NULL && score + currentFrame.getFirstRoundScore() < MAX_SCORE;
                bowlScore.setGameOver(gameOver);
                if (gameOver) {
                    currentFrame.setScored(true);
                    final int frameScore = score + currentFrame.getFirstRoundScore() + bowlScore.getFrameScoreByIndex(currentFrame.getId() - 1);
                    currentFrame.setFrameScore(frameScore);
                }
                if (score == MAX_SCORE || score + currentFrame.getFirstRoundScore() == MAX_SCORE) {
                    bowlScore.setLastFrameBlockVisible(true);
                    currentFrame.setRoundStatus(SPARE);
                    bowlScore.setKeypadValueThreshold(MAX_SCORE);
                } else {
                    bowlScore.setKeypadValueThreshold(MAX_SCORE - score);
                }
                bowlScore.setCurrentRound(THIRD);
            }
            case THIRD -> {
                bowlScore.setKeypadValueThreshold(MAX_SCORE);
                currentFrame.setThirdRoundScore(score);
                currentFrame.setScored(true);
                final int frameScore = bowlScore.getFrameScoreByIndex(currentFrame.getId() - 1) +
                        currentFrame.getFirstRoundScore() + currentFrame.getSecondRoundScore() + currentFrame.getThirdRoundScore();
                currentFrame.setFrameScore(frameScore);
                bowlScore.setGameOver(true);
            }
        }
    }
}
