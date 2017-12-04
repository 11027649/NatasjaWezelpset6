package e.natasja.natasjawezel__pset6;

/**
 * Created by Natasja on 4-12-2017.
 */

public class User {
    public String username;
    public Integer solved;
    public Float time_to_solve;

    // default constructor is important for firebase
    public User() {}

    public User(String username, Integer solved, Float time_to_solve) {
        this.username = username;
        this.solved = solved;
        this.time_to_solve = time_to_solve;
    }
}
