package com.armanfar.bowlscoring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BowlScore {
    @Id
    private long id;

    @Column
    private short currentFrameIndex;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private StepEnum currentRound;

    @Column
    private short keypadValueThreshold;

    @Column
    private boolean gameOver;

    @Column
    private boolean lastFrameBlockVisible;

    @OneToMany(mappedBy = "bowlScore")
    private List<Frame> frames;

    public static BowlScore build() {
        BowlScore bowlScore = new BowlScore();
        bowlScore.id = 1;
        bowlScore.currentFrameIndex = 0;
        bowlScore.currentRound = StepEnum.FIRST;
        bowlScore.keypadValueThreshold = 10;
        bowlScore.gameOver = false;
        bowlScore.lastFrameBlockVisible = false;
        bowlScore.frames = new ArrayList(10);
        return bowlScore;
    }
}
