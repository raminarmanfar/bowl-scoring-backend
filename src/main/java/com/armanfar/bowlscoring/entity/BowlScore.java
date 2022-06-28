package com.armanfar.bowlscoring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

import static com.armanfar.bowlscoring.entity.StepEnum.FIRST;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder(toBuilder = true)
public class BowlScore {
    @Id
    private Integer id;

    @Column
    private int currentFrameIndex = 0;

    @Column
    @Enumerated(EnumType.STRING)
    private StepEnum currentRound = FIRST;

    @Column
    private int keypadValueThreshold = 10;

    @Column
    private boolean gameOver = false;

    @Column
    private boolean lastFrameBlockVisible = false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bowlScore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Frame> frames;

    public BowlScore(int id) {
        this.id = id;
    }

    @JsonIgnore
    public Frame getCurrentFrame() {
        return frames.get(currentFrameIndex);
    }

    public Frame getFrameByIndex(int index) {
        return index >= 0 && index < 10 ? frames.get(index) : null;
    }

    @JsonIgnore
    public Frame getPreviousFrame() {
        return getFrameByIndex(currentFrameIndex - 1);
    }

    public int getFrameScoreByIndex(int frameIndex) {
        Frame frame = getFrameByIndex(frameIndex);
        return frame != null ? frame.getFrameScore() : 0;
    }

    public int moveNextFrame() {
        return ++currentFrameIndex;
    }
}
