/*
 * MatchSerailizer.java
 * Contains logic needed to handle undo functionality.
 * Copyright 2014 Ultra explorers. 
 */
package undoprovider;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import scoreboardbackend.Match;

/**
 *
 * @author Wickrama
 */
public class MatchSerializer {

    public void reset() {
        saveTimes = 0;
    }

    public static int saveTimes = 0;

    public static void backUp(Match match) {
        try {

            FileOutputStream fos
                    = new FileOutputStream("match.raw-" + ++saveTimes);
            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(match);
                oos.flush();
            }

        } catch (IOException ex) {
            Logger.getLogger(MatchSerializer.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    public static Match loadMatch(int i) {
        Match m = null;

        if (i > saveTimes) {
            return m;
        }

        try {

            FileInputStream fis
                    = new FileInputStream("match.raw-" + (saveTimes + 1 - i));
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                m = (Match) ois.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MatchSerializer.class.getName())
                        .log(Level.SEVERE, null, ex);
            }

            for (int k = saveTimes; k > (saveTimes - i); k--) {
                File f = new File("match.raw-" + k);
                f.delete();
            }

            saveTimes -= i;

        } catch (IOException ex) {
            Logger.getLogger(MatchSerializer.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        return m;
    }

}
