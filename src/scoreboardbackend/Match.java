/*
 * Match.java
 * Contains logic for playing a match.
 * Copyright 2014 Ultra explorers. 
 */
package scoreboardbackend;

import java.io.Serializable;

/**
 *
 * @author Wickramaranga
 */
public class Match implements Serializable {

    public static Match create(int numOvers,
            Team teamL, int[] teamLBattinOrder, int teamLOpenningBowler,
            Team teamR, int[] teamRBattinOrder, int teamROpenningBowler,
            boolean tossLeft, boolean tossBat) {

        Match m = new Match(teamL, teamR, tossLeft, tossBat);

        if (tossLeft) {
            if (tossBat) {
                m.sessions[0] = new Session(numOvers, teamL, teamLBattinOrder,
                        teamR, teamROpenningBowler);
                m.sessions[1] = new Session(numOvers, teamR, teamRBattinOrder,
                        teamL, teamLOpenningBowler);
            } else {
                m.sessions[1] = new Session(numOvers, teamL, teamLBattinOrder,
                        teamR, teamROpenningBowler);
                m.sessions[0] = new Session(numOvers, teamR, teamRBattinOrder,
                        teamL, teamLOpenningBowler);
            }
        } else {
            if (tossBat) {
                m.sessions[1] = new Session(numOvers, teamL, teamLBattinOrder,
                        teamR, teamROpenningBowler);
                m.sessions[0] = new Session(numOvers, teamR, teamRBattinOrder,
                        teamL, teamLOpenningBowler);
            } else {
                m.sessions[0] = new Session(numOvers, teamL, teamLBattinOrder,
                        teamR, teamROpenningBowler);
                m.sessions[1] = new Session(numOvers, teamR, teamRBattinOrder,
                        teamL, teamLOpenningBowler);
            }
        }

        return m;
    }

    private final Team teamL;
    private final Team teamR;

    private final boolean tossLeft;
    private final boolean tossBat;

    private boolean completed = false;
    private boolean interval = false;

    private final Session[] sessions = new Session[2];
    private int currentSession = 0;

    public void play(Ball ball) {

        if (interval) {
            interval = false;
            currentSession = 1;
        }

        if (!completed) {
            sessions[currentSession].play(ball);                //MOST IMPORTANT
        }

        if (currentSession == 0 && sessions[0].isCompleted()) {
            interval = true;
        } else if (currentSession == 1 && sessions[1].isCompleted()) {
            completed = true;
        }

    }

    private Match(Team teamL, Team teamR, boolean tossLeft, boolean tossBat) {
        this.teamL = teamL;
        this.teamR = teamR;
        this.tossLeft = tossLeft;
        this.tossBat = tossBat;
    }

    public Session getCurrentSession() {
        return sessions[currentSession];
    }

    public Session getOtherSession() {
        return sessions[currentSession == 0 ? 1 : 0];
    }

    public int getSessionIndex() {
        return currentSession;
    }

    public boolean tossIsLeft() {
        return tossLeft;
    }

    public boolean tossBatsFirst() {
        return tossBat;
    }
}
