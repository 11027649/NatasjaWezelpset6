package e.natasja.natasjawezel__pset6;

/**
 * Created by Natasja on 15-12-2017.
 * Creates user highscore objects to put in the scoreboard listview.
 */

public class UserHighscore {
    // properties of the class
    public String number;
    public String username;
    public String highscore;

    // constructor of the class
    public UserHighscore(String aNumber, String anUsername, String aHighscore) {
        this.username = anUsername;
        this.highscore = aHighscore;
        this.number = aNumber;
    }
}
