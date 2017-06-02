/*
 * Match.java
 * Contains logic for handling a dismissal.
 * Copyright 2014 Ultra explorers. 
 */
package scoreboardbackend;

/**
 *
 * @author Wickramaranga
 */
public class Dismissal implements Ball {

    private final DismissMethod cause;   //Why out?
    private final int bowledBy;          //bowled, caught, stumped, hitwkt, lbw
    private final int caughtBy;          //caught, runout
    private final boolean wide;          //is a wide, only for runouts
    private final boolean otherBatsman;  //for runouts

    public Dismissal(DismissMethod cause, int bowledBy, int caughtBy,
            boolean isAWide, boolean otherBatsman) {

        this.cause = cause;
        this.bowledBy = bowledBy;
        this.caughtBy = caughtBy;
        this.wide = isAWide;
        this.otherBatsman = otherBatsman;
    }

    /**
     * @return the cause
     */
    public DismissMethod getCause() {
        return cause;
    }

    /**
     * @return the bowledBy
     */
    public int getBowledBy() {
        return bowledBy;
    }

    /**
     * @return the caughtBy
     */
    public int getCaughtBy() {
        return caughtBy;
    }

    /**
     * @return the wide
     */
    public boolean isAWide() {
        return wide;
    }

    /**
     * @return the otherBatsman
     */
    public boolean isOtherBatsman() {
        return otherBatsman;
    }

    public static enum DismissMethod {

        BOWLED,
        CAUGHT,
        LBW,
        RUN_OUT,
        STUMPED,
        HIT_WICKET,
//      HIT_THE_BALL_TWICE,
//      OBSTRUCTING_THE_FIELD,
//      HANDLED_THE_BALL,
//      TIMED_OUT,
//      RETIRED_OUT

    }

}
