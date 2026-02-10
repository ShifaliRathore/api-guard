package com.apiguard.api_guard.ai;


public class AbuseScoreResult {

    private int score;
    private AbuseLevel level;

    public AbuseScoreResult(int score, AbuseLevel level) {
        this.score = score;
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public AbuseLevel getLevel() {
        return level;
    }
}
