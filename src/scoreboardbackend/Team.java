/*
 * Team.java
 * Contains details for a team.
 * Copyright 2014 Ultra explorers. 
 */
package scoreboardbackend;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Wickrama
 */
public class Team implements Serializable {

    private final String name;
    private final District district;
    private String info;
    private ArrayList<Player> players;
    private int matchCount = 0;

    public Team(String name, District district, String info,
            ArrayList<Player> players) {
        this.name = name;
        this.district = district;
        this.info = info;
        this.players = players;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the district
     */
    public District getDistrict() {
        return district;
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
     * @return the players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * @param players the players to set
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    /**
     * @return the matchCount
     */
    public int getMatchCount() {
        return matchCount;
    }

    /**
     * @param matchCount the matchCount to set
     */
    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public static enum District {

        /**
         * Matara district
         */
        MATARA,
        /**
         * Galle District
         */
        GALLE,
        /**
         * Hambantota District
         */
        HAMBANTOTA,
        /**
         * Monaragala District
         */
        MONARAGALA,
        /**
         * Some other district
         */
        OTHER
    }
}
