/*
 * Session.java
 * Contains main logic for playing half-a-match (called a session here).
 * Copyright 2014 Ultra explorers. 
 */
package scoreboardbackend;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Wickramaranga
 */
public class Session implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Variable declarations">
    private boolean completed = false;

    private final ArrayList<ArrayList<Ball>> overs;   //Details per ball

    private int currentOver = 0;          //Currently playing over co.x
    private int currentOverMaxBalls = 6;  //Increases on wide, noball
    private int currentBall = 0;          //Currently playing ball x.cb
    private int noOrWidesOver = 0;        //No, wide per over
    private int totalPerOver = 0;         //Total score per over

    private int total = 0;      //Team total
    private int extras = 0;     //Team extras
    private int exNoballs = 0;  //Team no balls
    private int exWides = 0;    //Team wides
    private int exLegByes = 0;  //Team leg byes
    private int exByes = 0;     //Team byes
    private int wickets = 0;    //Team wickets

    private final Team batting;                       //Batting team
    private final int[] bOrder;                       //Batting order
    private final boolean[] bPlayed = new boolean[11];//Whether batsman played
    private final boolean[] bOut = new boolean[11];   //Whether batsman is out
    private final int[] bRuns = new int[11];          //Runs by batsman
    private final int[] b6s = new int[11];            //Sixes by batsman
    private final int[] b4s = new int[11];            //Fours by batsman
    private final int[] bBallsFaced = new int[11];    //Balls faced by batsman
    private int currentBatsman = 0;                   //Index of current batsman
    private int restingBatsman = 1;                   //Index of the op. side.

    private final Team fielding;                      //Fielding team
    private final int[] fWickets = new int[11];       //Wickets by fielder
    private final int[] fCatches = new int[11];       //Catches by fielder
    private final int[] fRunouts = new int[11];       //Run outs by fielder
    private final int[] fOvers = new int[11];         //Overs by each f.man
    private final int[] fNos = new int[11];           //Num. No balls by f.man
    private final int[] fWides = new int[11];         //Num. Wides b f.man
    private final int[] fScoreAgainst = new int[11];  //Score against bowlers
    private int currentBowler;                        //Index of current bowler
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public Session(int oversCount, Team batting,
            int[] battingOrder, Team fielding, int openningBowler) {

        this.overs = new ArrayList<>(oversCount);
        for (int i = 0; i < oversCount; i++) {
            overs.add(new ArrayList<Ball>());
        }
        this.batting = batting;
        this.bOrder = battingOrder;
        this.fielding = fielding;
        this.currentBowler = openningBowler;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Calling next batsman or bowler">
    private void arrayPushInsert(int[] array, int sourceIndex, int pushTo) {

        int temp = array[pushTo];
        int tmp0;
        for (int i = pushTo; i <= sourceIndex; i++) {
            tmp0 = array[i];
            array[i] = temp;
            temp = tmp0;
        }
    }

    private int firstFalse(boolean[] array) {
        for (int i = 0; i < 11; i++) {
            if (array[i] == false) {
                return i;
            }
        }
        return -1;
    }

    void callNextBatsman(int batsman) {
        if (wickets < 9) {
            if (batsman < 0 || bPlayed[batsman]) {//XXX
                currentBatsman = firstFalse(bPlayed);
            } else {
                arrayPushInsert(bOrder, batsman, firstFalse(bPlayed));
                currentBatsman = batsman;
            }
        }
    }

    void callNextBowler(int bowler) {
        currentBowler = bowler;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Play a ball - MOST IMPORTANT">
    /**
     *
     * @param ball The ball played
     * @return Whether succeeded playing
     */
    public boolean play(Ball ball) {

        if (ball != null) {

            //Played a ball
            overs.get(currentOver).add(ball);

            //Change current batsman played status.
            if (!bPlayed[currentBatsman]) {
                bPlayed[currentBatsman] = true;             //LINE A//
            }
            //Resting batsman should be considered played
            if (!bPlayed[restingBatsman]) {
                bPlayed[restingBatsman] = true;
            }
            bBallsFaced[currentBatsman]++;

            //Scored
            if (ball instanceof Score) {
                Score score = (Score) ball;

                //All about runs
                totalPerOver += score.getRuns();
                total += score.getRuns();
                bRuns[currentBatsman] += score.getRuns();
                fScoreAgainst[currentBowler] += score.getRuns();
                if (score.getRuns() == 6) {
                    b6s[currentBatsman]++;
                } else if (score.getRuns() == 4) {
                    b4s[currentBatsman]++;
                }
                //Swap players, if odd runs, n.b.: Fielders change effect is
                //seperately specified... See line B
                //FIXME byes and legbyes
                if (score.getRuns() % 2 != 0) {
                    int temp = restingBatsman;
                    restingBatsman = currentBatsman;
                    currentBatsman = temp;
                }

                //Wide, NoBall -- extra ball and extras
                if (score.isWide()) {
                    currentOverMaxBalls++;
                    noOrWidesOver += 1;
                    totalPerOver += 1;
                    total += 1;
                    extras += 1;
                    exWides += 1;
                    fWides[currentBowler] += 1;
                    fScoreAgainst[currentBowler] += 1;
                    if (score.getByes() > 0) {//Wide and byes
                        totalPerOver += score.getByes();
                        total += score.getByes();
                        extras += score.getByes();
                        exWides += score.getByes();
                        fScoreAgainst[currentBowler] += score.getByes();
                    }
                } else if (score.isNoball()) {
                    currentOverMaxBalls++;
                    noOrWidesOver += 1;
                    totalPerOver += 1;
                    total += 1;
                    extras += 1;
                    exNoballs += 1;
                    fNos[currentBowler] += 1;
                    fScoreAgainst[currentBowler] += 1;
                    if (score.getLegbyes() > 0) {//No and legbyes
                        totalPerOver += score.getLegbyes();
                        total += score.getLegbyes();
                        extras += score.getLegbyes();
                        exNoballs += score.getLegbyes();
                        fScoreAgainst[currentBowler] += score.getLegbyes();
                    } else if (score.getByes() > 0) {//no and byes
                        totalPerOver += score.getByes();
                        total += score.getByes();
                        extras += score.getByes();
                        exNoballs += score.getByes();
                        fScoreAgainst[currentBowler] += score.getByes();
                    }
                } else //Byes and leg byes -- just extras
                if (score.getByes() > 0) {
                    totalPerOver += score.getByes();
                    total += score.getByes();
                    extras += score.getByes();
                    exByes += score.getByes();
                    fScoreAgainst[currentBowler] += score.getByes();
                } else if (score.getLegbyes() > 0) {
                    totalPerOver += score.getLegbyes();
                    total += score.getLegbyes();
                    extras += score.getLegbyes();
                    exLegByes += score.getLegbyes();
                    fScoreAgainst[currentBowler] += score.getLegbyes();
                }

                //A dismissal
            } else if (ball instanceof Dismissal) {
                Dismissal dismiss = (Dismissal) ball;
                wickets++;

                //Handle dismiss cause
                //FIXME
                switch (dismiss.getCause()) {
                    case BOWLED:
                        fWickets[currentBowler]++;
                        bOut[currentBatsman] = true;
                        break;
                    case CAUGHT:
                        //FIXME Does bowler get the credit also?
                        fWickets[currentBowler]++;
                        fCatches[dismiss.getCaughtBy()]++;
                        bOut[currentBatsman] = true;
                        break;
                    case LBW:
                        fWickets[currentBowler]++;
                        bOut[currentBatsman] = true;
                        break;
                    case RUN_OUT:
                        //FIXME Who gets the credit?
                        fRunouts[dismiss.getCaughtBy()]++;
                        if (dismiss.isAWide()) {
                            total += 1;
                            currentOverMaxBalls++;
                        }
                        if (dismiss.isOtherBatsman()) {//Other batsman out
                            bOut[currentBatsman] = true;
                        } else {
                            bOut[currentBatsman] = true;
                        }
                        break;
                    case STUMPED:
                        //FIXME Who gets the credit?
                        fWickets[currentBowler]++;
                        bOut[currentBatsman] = true;
                        break;
                    case HIT_WICKET:
                        fWickets[currentBowler]++;
                        bOut[currentBatsman] = true;
                        break;
                    default:

                }

                //Call next batsman, but doesn't change batsmanPlayed status;
                //so another batsman can be sent instead... See LINE A
                callNextBatsman(-1);

            } else {
                return false;
            }

            //Overs related things
            if (currentOverMaxBalls > currentBall + 1) {
                currentBall++;
            } else {
                if (overs.size() > currentOver + 1) {

                    currentOver++;
                    currentOverMaxBalls = 6;
                    currentBall = 0;
                    noOrWidesOver = 0;
                    totalPerOver = 0;
                    //They ball only a full number of overs.
                    fOvers[currentBowler]++;
                    //Fieldsmen swap places. Equivalant to batsmen swap;
                    //but when an over is over. :-p                   //LINE B//
                    int temp = currentBatsman;
                    currentBatsman = restingBatsman;
                    restingBatsman = temp;
                } else {
                    //Session is over
                    completed = true;
                    return false;
                }
            }

        } else {
            return false;
        }

        return true;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters and misc.">
    /**
     * @return All balls...
     */
    public ArrayList<ArrayList<Ball>> getOvers() {
        return overs;
    }

    /**
     * @return Overall total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @return Overall wickets
     */
    public int getWickets() {
        return wickets;
    }

    /**
     * @return Whether the session has completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * @return Index of current over respective to the session
     */
    public int getCurrentOver() {
        return currentOver;
    }

    /**
     * @return Index of current ball respective to the over
     */
    public int getCurrentBall() {
        return currentBall;
    }

    /**
     * @return No or wide balls per over
     */
    public int getNoOrWidesOver() {
        return noOrWidesOver;
    }

    /**
     * @return Total score per over
     */
    public int getTotalPerOver() {
        return totalPerOver;
    }

    /**
     * @return Overall extras
     */
    public int getExtras() {
        return extras;
    }

    /**
     * @return Overall no balls
     */
    public int getExNoballs() {
        return exNoballs;
    }

    /**
     * @return Overall wide balls
     */
    public int getExWides() {
        return exWides;
    }

    /**
     * @return Overall leg byes
     */
    public int getExLegByes() {
        return exLegByes;
    }

    /**
     * @return Overall byes
     */
    public int getExByes() {
        return exByes;
    }

    /**
     * @return Batting team
     */
    public Team getBatting() {
        return batting;
    }

    /**
     * @return Current Batsman index
     */
    public int getCurrentBatsman() {
        return currentBatsman;
    }

    /**
     * @return Resting Batsman index
     */
    public int getRestingBatsman() {
        return restingBatsman;
    }

    /**
     * @return Fielding team
     */
    public Team getFielding() {
        return fielding;
    }

    /**
     * @return Current bowler index
     */
    public int getCurrentBowler() {
        return currentBowler;
    }

    /**
     * @return How many balls for current over
     */
    public int getCurrentOverMaxBalls() {
        return currentOverMaxBalls;
    }

    /**
     * @return Gets current batting order
     */
    public int[] getbOrder() {
        return bOrder;
    }

    /**
     * @param ix Index of Batsman
     * @return Whether the batsman is playing or has played
     */
    public boolean isbPlayed(int ix) {
        return bPlayed[ix];
    }

    /**
     * @param ix Index of Batsman
     * @return Whether the batsman is out
     */
    public boolean isbOut(int ix) {
        return bOut[ix];
    }

    /**
     * @param ix Index of Batsman
     * @return Number of runs scored by batsman
     */
    public int getbRuns(int ix) {
        return bRuns[ix];
    }

    /**
     * @param ix Index of Batsman
     * @return Number of sixes by batsman
     */
    public int getB6s(int ix) {
        return b6s[ix];
    }

    /**
     * @param ix Index of Batsman
     * @return Number of fours by batsman
     */
    public int getB4s(int ix) {
        return b4s[ix];
    }

    /**
     * @param ix Index of Batsman
     * @return Number of balls this batsman has faced.
     */
    public int getbBallsFaced(int ix) {
        return bBallsFaced[ix];
    }

    /**
     * @param ix Index of Fieldsman
     * @return Number of wickets taken by the fieldsman, Includes: Bowled, LBW,
     * Caught by some other person, Stumped, Hit wickets.
     */
    public int getfWickets(int ix) {
        return fWickets[ix];
    }

    /**
     * @param ix Index of Fieldsman
     * @return Number of catches taken by the fieldsman
     */
    public int getfCatches(int ix) {
        return fCatches[ix];
    }

    /**
     * @param ix Index of Fieldsman
     * @return Number of runouts taken by the fieldsman
     */
    public int getfRunouts(int ix) {
        return fRunouts[ix];
    }

    /**
     * @param ix Index of Fieldsman
     * @return Number of overs delivered by the fieldsman
     */
    public int getfOvers(int ix) {
        return fOvers[ix];
    }

    /**
     * @param ix Index of Fieldsman
     * @return Number of No balls by the fieldsman
     */
    public int getfNos(int ix) {
        return fNos[ix];
    }

    /**
     * @param ix Index of Fieldsman
     * @return Number of Wide balls by the fieldsman
     */
    public int getfWides(int ix) {
        return fWides[ix];
    }

    /**
     * @param ix Index of Fieldsman
     * @return Score against a fieldsman
     */
    public int getfScoreAgainst(int ix) {
        return fScoreAgainst[ix];
    }

    /**
     *
     * @param ix Index of the over
     * @return String representation of the over
     */
    public String getSimpleOverString(int ix) {
        StringBuilder temp = new StringBuilder();

        if (ix < overs.size()) {
            for (Ball b : overs.get(ix)) {
                if (b instanceof Dismissal) {
                    temp.append("W");
                    if (((Dismissal) b).isAWide()) {
                        temp.append("+1");
                    }
                } else if (b instanceof Score) {
                    Score s = (Score) b;

                    if (s.getRuns() > 0) {
                        temp.append(String.valueOf(s.getRuns()));
                    }
                    int tmpex = s.getByes() + s.getLegbyes()
                            + (s.isNoball() ? 1 : 0)
                            + (s.isWide() ? 1 : 0);
                    if (tmpex != 0) {
                        temp.append("+").append(tmpex);
                    }
                }
                temp.append(" ");
            }
        }

        return temp.toString();
    }
//</editor-fold>

}
