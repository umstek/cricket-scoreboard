/*
 * SB.java
 * Simulates the functionality of a score board.
 * Copyright 2014 Ultra explorers.
 */
package scoreboardbackend;

import java.awt.image.BufferedImage;

/**
 * @author Wickramaranga
 */
public final class SB {

    private static Match match;

    /*
     * Simulates the functionality of a score card or a board... :-p
     *
     * User interacts with a 1-based world.
     * This is the bridge between programmatic 0-based indexing and
     * real world 1-based indexing.
     * e.g.: 1st bowler aka openning bowler, not 0th bowler!
     */
    /**
     * @param args the command line arguments
     */
    public static final void main(String[] args) {
        /*Comment following line*/
        MatchSimulator.main(args);
    }

    /**
     *
     * @param teamLeft A team
     * @param teamRight The other team
     * @param oversPerSide e.g.: 20 for a t-20
     * @param tossLeft Whether 1st team won the toss
     * @param tossBat Whether the team who won the toss is batting first
     *
     * <br />
     * Warning: an empty team string will produce a random, in memory team!
     */
    public static final void startNewMatch(String teamLeft, String teamRight,
            int oversPerSide, boolean tossLeft, boolean tossBat) {

        Team teamL = teamLeft.isEmpty()
                ? dboperations.DBOperations.getRandomTeam()
                : dboperations.DBOperations.getTeam(teamLeft);
        Team teamR = teamRight.isEmpty()
                ? dboperations.DBOperations.getRandomTeam()
                : dboperations.DBOperations.getTeam(teamRight);

        match = Match.create(oversPerSide,
                teamL, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 0 /*bowlr*/,
                teamR, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 0 /*bowlr*/,
                tossLeft, tossBat);

        undoprovider.MatchSerializer.saveTimes = 0;
    }

    /**
     * If anything occurs, call this. :-)
     *
     * @param playCommand Command to play... <br />
     * Concatenate following characters in given order and send as playCommand.
     * <br />
     * <pre><code>
     * s = score
     *      x = extra
     *          w = wide
     *          n = no ball
     *          l = leg bye; followed by score
     *          b = bye; followed by score
     *      r = runs; followed by score
     * d = dismissal
     *      b = bowled, direct by bowler
     *      c = caught, followed by fieldsman index
     *      s = stumped
     *      l = LBW
     *      r = run out followed by fielder index;
     *          followed by w if also a wide, o for other batsman is out.
     *      h = hit wicket
     *
     * -empty string- = dot ball (= sr0)
     * </code></pre>
     * <br />
     * Any other character combination will NOT DO ANYTHING. <br />
     * <pre><code>
     * e.g. 1 : <i>a dot ball =</i> "" or "sr0"
     * e.g. 2 : <i>a single run =</i> "sr1"
     * e.g. 3 : <i>other batsman run out for a wide =</i> "drwo"
     * e.g. 4 : <i>for runs for a no ball =</i> "sxnr4"
     * e.g. 5 : <i>2 extras from a leg bye =</i> "sxl2"
     * </code></pre>
     *
     */
    public static final void play(String playCommand) {
        /*FIXME May contain logical errors*/
        Ball ball = null;

        if (playCommand == null || playCommand.equals("")) {
            playCommand = "sr0";
        } else {
            playCommand = playCommand.toLowerCase();
        }

        if (playCommand.charAt(0) == 's') {
            ball = getScoreBall(playCommand.substring(1));
        } else if (playCommand.charAt(0) == 'd') {
            ball = getDismissalBall(playCommand.substring(1));
        }

        if (ball != null) {
            match.play(ball);

            undoprovider.MatchSerializer.backUp(match);
        }
    }

    private static Score getScoreBall(String scoreStr) {
        if (scoreStr.isEmpty()) {
            return new Score(0, 0, false, false, 0);
        }

        /*
         * xn, xw, xb?, xl?, xwb?, xnb?, xnl?
         * r?
         */
        int runs = 0;
        if (scoreStr.contains("r")) {
            if (scoreStr.indexOf('r') < scoreStr.length() - 1) {
                runs = Integer.parseInt(
                        scoreStr.substring(scoreStr.indexOf('r') + 1,
                                scoreStr.indexOf('r') + 2));
            }
        }

        //blnw r
        if (scoreStr.contains("x")) {                           //Extras

            if (scoreStr.contains("n")) {                       //No ball+

                if (scoreStr.contains("l")) {                   //Leg bye
                    int l = Integer.parseInt(
                            scoreStr.substring(scoreStr.indexOf('l') + 1,
                                    scoreStr.indexOf('l') + 2));
                    return new Score(l, 0, true, false, 0);

                } else if (scoreStr.contains("b")) {            //Bye
                    int b = Integer.parseInt(
                            scoreStr.substring(scoreStr.indexOf('b') + 1,
                                    scoreStr.indexOf('b') + 2));
                    return new Score(0, b, true, false, 0);

                } else if (runs != 0) {                         //Runs
                    return new Score(0, 0, true, false, runs);
                } else {                                        //No ball
                    return new Score(0, 0, true, false, 0);
                }

            } else if (scoreStr.contains("w")) {                //Wide+

                if (scoreStr.contains("b")) {                   //Bye
                    int b = Integer.parseInt(
                            scoreStr.substring(scoreStr.indexOf('b') + 1,
                                    scoreStr.indexOf('b') + 2));
                    return new Score(0, b, false, true, 0);

                } else {                                        //Wide
                    return new Score(0, 0, false, true, 0);
                }

            } else if (scoreStr.contains("l")) {                //Leg bye

                int l = Integer.parseInt(
                        scoreStr.substring(scoreStr.indexOf('l') + 1,
                                scoreStr.indexOf('l') + 2));
                return new Score(l, 0, false, false, 0);

            } else if (scoreStr.contains("b")) {                //Bye

                int b = Integer.parseInt(
                        scoreStr.substring(scoreStr.indexOf('b') + 1,
                                scoreStr.indexOf('b') + 2));
                return new Score(0, b, false, false, 0);

            } else {                                            //Ignore extras
                return new Score(0, 0, false, false, runs);
            }

        } else {                                                //Runs only
            return new Score(0, 0, false, false, runs);
        }

    }

    private static Dismissal getDismissalBall(String dismissalStr) {
        if (dismissalStr.length() < 1) {
            return new Dismissal(Dismissal.DismissMethod.BOWLED,
                    match.getCurrentSession().getCurrentBowler(),
                    -1, false, false);
        }

        switch (dismissalStr.charAt(0)) {//bcslrh : nothing means bowled out
            case 'b':
                return new Dismissal(Dismissal.DismissMethod.BOWLED,
                        match.getCurrentSession().getCurrentBowler(),
                        -1, false, false);
            case 'c':
                return new Dismissal(Dismissal.DismissMethod.CAUGHT,
                        match.getCurrentSession().getCurrentBowler(),
                        Integer.parseInt(dismissalStr.substring(1)),
                        false, false);
            case 's':
                return new Dismissal(Dismissal.DismissMethod.STUMPED,
                        match.getCurrentSession().getCurrentBowler(),
                        -1, false, false);
            case 'l':
                return new Dismissal(Dismissal.DismissMethod.LBW,
                        match.getCurrentSession().getCurrentBowler(),
                        -1, false, false);
            case 'r':
                return new Dismissal(Dismissal.DismissMethod.RUN_OUT,
                        match.getCurrentSession().getCurrentBowler(),
                        Integer.parseInt(
                                dismissalStr.substring(1,
                                        dismissalStr.length()
                                        - (dismissalStr.contains("w") ? 1 : 0)
                                        + (dismissalStr.contains("o") ? 1 : 0))
                        ),
                        dismissalStr.contains("w"),
                        dismissalStr.contains("o"));
            case 'h':
                return new Dismissal(Dismissal.DismissMethod.HIT_WICKET,
                        match.getCurrentSession().getCurrentBowler(),
                        -1, false, false);
            default:
                return new Dismissal(Dismissal.DismissMethod.BOWLED,
                        match.getCurrentSession().getCurrentBowler(),
                        -1, false, false);
        }
    }

    /**
     *
     * @param index1based
     */
    public static final void changeBatsman(int index1based) {
        if (index1based > 0 && index1based <= 11) {
            match.getCurrentSession().callNextBatsman(index1based - 1);
        }
    }

    /**
     *
     * @param index1based
     */
    public static final void changeBowler(int index1based) {
        if (index1based > 0 && index1based <= 11) {
            match.getCurrentSession().callNextBowler(index1based - 1);
        }
    }

    /**
     *
     * @param times
     */
    public static final void undo(int times) {
        match = undoprovider.MatchSerializer.loadMatch(times);
    }

    /**
     *
     */
    public static final void endMatchAndPushToDB() {
    }

    private static boolean isValidPlayerIndex(int ix) {
        return ix <= 11 && ix > 0;
    }

    /**
     *
     */
    public static final class MatchDetails {

        /**
         *
         * @return
         */
        public static final int total() {
            return match.getCurrentSession().getTotal();
        }

        /**
         *
         * @return
         */
        public static final int extras() {
            return match.getCurrentSession().getExtras();
        }

        /**
         *
         * @return
         */
        public static final int wides() {
            return match.getCurrentSession().getExWides();
        }

        /**
         *
         * @return
         */
        public static final int noBalls() {
            return match.getCurrentSession().getExNoballs();
        }

        /**
         *
         * @return
         */
        public static final int legByes() {
            return match.getCurrentSession().getExLegByes();
        }

        /**
         *
         * @return
         */
        public static final int byes() {
            return match.getCurrentSession().getExByes();
        }

        /**
         *
         * @return
         */
        public static final int currentOverIndex() {
            return match.getCurrentSession().getCurrentOver() + 1;
        }

        /**
         *
         * @return
         */
        public static final int noBallsOrWidesForCurrentOver() {
            return match.getCurrentSession().getNoOrWidesOver();
        }

        /**
         *
         * @return
         */
        public static final int currentBatsmanIndex() {
            return match.getCurrentSession().getCurrentBatsman() + 1;
        }

        /**
         *
         * @return
         */
        public static final int restingBatsmanIndex() {
            return match.getCurrentSession().getRestingBatsman() + 1;
        }

        /**
         *
         * @return
         */
        public static final int currentBowlerIndex() {
            return match.getCurrentSession().getCurrentBowler() + 1;
        }

        /**
         *
         * @param overIndex
         * @return
         */
        public static final String overScoreView(int overIndex) {
            if ((overIndex > 0)
                    && (overIndex
                    <= match.getCurrentSession().getOvers().size())) {

                return match.getCurrentSession()
                        .getSimpleOverString(overIndex - 1);
            } else {
                return null;
            }
        }

        /**
         *
         * @return
         */
        public static final String currentOverScoreView() {
            return overScoreView(currentOverIndex());
        }

        /**
         *
         * @return
         */
        public static final int target() {
            if (match.getSessionIndex() == 1) {
                return match.getOtherSession().getTotal();
            } else {
                return 0;
            }
        }

        public static final boolean tossIsLeft() {
            return match.tossIsLeft();
        }

        public static final boolean tossBatsFirst() {
            return match.tossBatsFirst();
        }

        public static final int session() {
            return match.getSessionIndex() + 1;
        }

        public static final int currentBallIndex() {
            return match.getCurrentSession().getCurrentBall();
        }

        public static final int wickets() {
            return match.getCurrentSession().getWickets();
        }

    }

    /**
     *
     */
    public static final class BatsmanDetails {

        public final static String teamName() {
            return match.getCurrentSession().getBatting().getName();
        }

        /**
         *
         * @param ix
         * @return
         */
        public final static String name(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getBatting().getPlayers()
                        .get(ix - 1).getName();
            } else {
                return null;
            }
        }

        /**
         *
         * @param ix
         * @return
         */
        public static final BufferedImage getPhoto(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getBatting().getPlayers()
                        .get(ix - 1).getPhoto();
            } else {
                return null;
            }
        }

        /**
         *
         * @param ix
         * @return
         */
        public static final int age(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getBatting().getPlayers()
                        .get(ix - 1).getAge();
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @return
         */
        public static final String info(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getBatting().getPlayers()
                        .get(ix - 1).getInfo();
            } else {
                return null;
            }
        }

        /**
         *
         * @param ix
         * @return
         */
        public static final int matches(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getBatting().getMatchCount();
            } else {
                return -1;
            }
        }

        /*Match specific*/
        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int facedBalls(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getBatting().getPlayers()
                            .get(ix - 1).getBallsFaced();
                } else {
                    return match.getCurrentSession().getbBallsFaced(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int totalRuns(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getBatting().getPlayers()
                            .get(ix - 1).getRuns();
                } else {
                    return match.getCurrentSession().getbRuns(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int sixes(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getBatting().getPlayers()
                            .get(ix - 1).getSixes();
                } else {
                    return match.getCurrentSession().getB6s(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int fours(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getBatting().getPlayers()
                            .get(ix - 1).getFours();
                } else {
                    return match.getCurrentSession().getB4s(ix - 1);
                }
            } else {
                return -1;
            }
        }

    }

    /**
     *
     */
    public static final class FieldsmanDetails {

        public final static String teamName() {
            return match.getCurrentSession().getFielding().getName();
        }

        /**
         *
         * @param ix
         * @return
         */
        public final static String name(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getFielding().getPlayers()
                        .get(ix - 1).getName();
            } else {
                return null;
            }
        }

        /**
         *
         * @param ix
         * @return
         */
        public static final BufferedImage getPhoto(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getFielding().getPlayers()
                        .get(ix - 1).getPhoto();
            } else {
                return null;
            }
        }

        /**
         *
         * @param ix
         * @return
         */
        public static final int age(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getFielding().getPlayers()
                        .get(ix - 1).getAge();
            } else {
                return 0;
            }
        }

        /**
         *
         * @param ix
         * @return
         */
        public static final String info(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getFielding().getPlayers()
                        .get(ix - 1).getInfo();
            } else {
                return null;
            }
        }

        /**
         *
         * @param ix
         * @return
         */
        public static final int matches(int ix) {
            if (isValidPlayerIndex(ix)) {
                return match.getCurrentSession().getFielding().getMatchCount();
            } else {
                return -1;
            }
        }

        /*Match specific*/
        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int deliveredOvers(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getFielding().getPlayers()
                            .get(ix - 1).getOvers();
                } else {
                    return match.getCurrentSession().getfOvers(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int scoreAgainst(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getFielding().getPlayers()
                            .get(ix - 1).getScoreAgainst();
                } else {
                    return match.getCurrentSession().getfScoreAgainst(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int noBalls(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getFielding().getPlayers()
                            .get(ix - 1).getNoBalls();
                } else {
                    return match.getCurrentSession().getfNos(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int wideBalls(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getFielding().getPlayers()
                            .get(ix - 1).getWides();
                } else {
                    return match.getCurrentSession().getfWides(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int wickets(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getFielding().getPlayers()
                            .get(ix - 1).getWickets();
                } else {
                    return match.getCurrentSession().getfWickets(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int catches(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getFielding().getPlayers()
                            .get(ix - 1).getCatches();
                } else {
                    return match.getCurrentSession().getfCatches(ix - 1);
                }
            } else {
                return -1;
            }
        }

        /**
         *
         * @param ix
         * @param previous
         * @return
         */
        public static final int runOuts(int ix, boolean previous) {
            if (isValidPlayerIndex(ix)) {
                if (previous) {
                    return match.getCurrentSession().getFielding().getPlayers()
                            .get(ix - 1).getRunOuts();
                } else {
                    return match.getCurrentSession().getfRunouts(ix - 1);
                }
            } else {
                return -1;
            }
        }

    }

}
