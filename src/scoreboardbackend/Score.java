/*
 * Match.java
 * Contains logic for handling runs, extras etc.
 * Copyright 2014 Ultra explorers. 
 */
package scoreboardbackend;

/**
 *
 * @author Wickramaranga
 */
public class Score implements Ball {

    private final int byes;
    private final int legbyes;
    private final boolean noball;
    private final boolean wide;
    private final int runs;

    public Score(int byes, int legbyes,
            boolean noball, boolean wide, int runs) {
        this.byes = byes;
        this.legbyes = legbyes;
        this.noball = noball;
        this.wide = wide;
        this.runs = runs;
    }

    /**
     * @return the byes
     */
    public int getByes() {
        return byes;
    }

    /**
     * @return the legbyes
     */
    public int getLegbyes() {
        return legbyes;
    }

    /**
     * @return the noball
     */
    public boolean isNoball() {
        return noball;
    }

    /**
     * @return the wide
     */
    public boolean isWide() {
        return wide;
    }

    /**
     * @return the runs
     */
    public int getRuns() {
        return runs;
    }

}
