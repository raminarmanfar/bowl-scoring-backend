package com.armanfar.bowlscoring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Frame {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private int frameIndex;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RoundStatusEnum roundStatus;

    @Column
    private int firstRoundScore;

    @Column
    private int secondRoundScore;

    @Column
    private int thirdRoundScore;

    @Column
    private int frameScore;

    @Column
    private boolean isScored;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private BowlScore bowlScore;
}
