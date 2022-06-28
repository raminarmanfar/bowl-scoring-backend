package com.armanfar.bowlscoring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
public class Frame {
    @Id
    private Integer id;

    @Column
    @Enumerated(EnumType.STRING)
    private RoundStatusEnum roundStatus = RoundStatusEnum.NULL;

    @Column
    private int firstRoundScore = -1;

    @Column
    private int secondRoundScore = -1;

    @Column
    private int thirdRoundScore = -1;

    @Column
    private int frameScore = 0;

    @Column
    private boolean isScored = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BowlScore bowlScore;

    public Frame(int id, BowlScore bowlScore) {
        this.id = id;
        this.bowlScore = bowlScore;
    }

    public static List<Frame> buildFrames(BowlScore bowlScore, int frameCount) {
        ArrayList<Frame> frames = new ArrayList();
        for (int i = 0; i < frameCount; i++) {
            frames.add(new Frame(i, bowlScore));
        }
        return frames;
    }
}
