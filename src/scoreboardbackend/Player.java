/*
 * Player.java
 * Contains details for a player.
 * Copyright 2014 Ultra explorers. 
 */
package scoreboardbackend;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author Wickrama
 */
public class Player implements Serializable {

    private final String name;
    private int age;
    transient private BufferedImage photo;//FIXME, change to byte array
    private String info;
    private int runs;
    private int sixes;
    private int fours;
    private int ballsFaced;
    private int wickets;
    private int runOuts;
    private int catches;
    private int overs;
    private int noBalls;
    private int wides;
    private int scoreAgainst;

    public Player(String name, int age, BufferedImage photo, String info,
            int runs, int sixes, int fours, int ballsFaced,
            int wickets, int runOuts, int catches, int overs,
            int noBalls, int wides, int scoreAgainst) {

        this.name = name;
        this.age = age;
        this.photo = photo;
        this.info = info;
        this.runs = runs;
        this.sixes = sixes;
        this.fours = fours;
        this.ballsFaced = ballsFaced;
        this.wickets = wickets;
        this.runOuts = runOuts;
        this.catches = catches;
        this.overs = overs;
        this.noBalls = noBalls;
        this.wides = wides;
        this.scoreAgainst = scoreAgainst;

    }

    public Player(String name, int age, BufferedImage photo, String info) {
        this(name, age, photo, info, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the photo
     */
    public BufferedImage getPhoto() {
        return photo;
    }

    /**
     * @param photo the photo to set
     */
    public void setPhoto(BufferedImage photo) {
        this.photo = photo;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the runs
     */
    public int getRuns() {
        return runs;
    }

    /**
     * @param runs the runs to set
     */
    public void setRuns(int runs) {
        this.runs = runs;
    }

    /**
     * @return the sixes
     */
    public int getSixes() {
        return sixes;
    }

    /**
     * @param sixes the sixes to set
     */
    public void setSixes(int sixes) {
        this.sixes = sixes;
    }

    /**
     * @return the fours
     */
    public int getFours() {
        return fours;
    }

    /**
     * @param fours the fours to set
     */
    public void setFours(int fours) {
        this.fours = fours;
    }

    /**
     * @return the ballsFaced
     */
    public int getBallsFaced() {
        return ballsFaced;
    }

    /**
     * @param ballsFaced the ballsFaced to set
     */
    public void setBallsFaced(int ballsFaced) {
        this.ballsFaced = ballsFaced;
    }

    /**
     * @return the wickets
     */
    public int getWickets() {
        return wickets;
    }

    /**
     * @param wickets the wickets to set
     */
    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    /**
     * @return the runOuts
     */
    public int getRunOuts() {
        return runOuts;
    }

    /**
     * @param runOuts the runOuts to set
     */
    public void setRunOuts(int runOuts) {
        this.runOuts = runOuts;
    }

    /**
     * @return the catches
     */
    public int getCatches() {
        return catches;
    }

    /**
     * @param catches the catches to set
     */
    public void setCatches(int catches) {
        this.catches = catches;
    }

    /**
     * @return the overs
     */
    public int getOvers() {
        return overs;
    }

    /**
     * @param overs the overs to set
     */
    public void setOvers(int overs) {
        this.overs = overs;
    }

    /**
     * @return the noBalls
     */
    public int getNoBalls() {
        return noBalls;
    }

    /**
     * @param noBalls the noBalls to set
     */
    public void setNoBalls(int noBalls) {
        this.noBalls = noBalls;
    }

    /**
     * @return the wides
     */
    public int getWides() {
        return wides;
    }

    /**
     * @param wides the wides to set
     */
    public void setWides(int wides) {
        this.wides = wides;
    }

    /**
     * @return the scoreAgainst
     */
    public int getScoreAgainst() {
        return scoreAgainst;
    }

    /**
     * @param scoreAgainst the scoreAgainst to set
     */
    public void setScoreAgainst(int scoreAgainst) {
        this.scoreAgainst = scoreAgainst;
    }

}
