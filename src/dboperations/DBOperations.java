/*
 * DBOperations.java
 *
 * Contains logic for handling 'cricket' (SQL) database.
 * Make sure you have a proper SQL server running and
 * the SQL connector installed.
 *
 * Copyright 2014 Ultra explorers. 
 */
package dboperations;

import java.awt.image.BufferedImage;
import java.util.*;
import scoreboardbackend.Team;
import scoreboardbackend.Player;

/**
 *
 * @author Wickrama
 */
public class DBOperations {

    public static Team getTeam(String team) {
        return null;
    }

    public static Team getRandomTeam() {
        Random r = new Random();
        ArrayList<Player> p = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            p.add(new Player("player" + String.valueOf(r.nextInt()),
                    r.nextInt(50) + 10,
                    new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB), "")
            );
        }

        Team t = new Team("team" + String.valueOf(r.nextInt()),
                Team.District.values()[r.nextInt(5)],
                "in memmory random team", p);
        return t;
    }
}
