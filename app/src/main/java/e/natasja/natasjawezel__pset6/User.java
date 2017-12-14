package e.natasja.natasjawezel__pset6;

/**
 * Created by Natasja on 4-12-2017.
 * This is a class for users that register to this app.
 * They will be added to firebase in this format.
 */

public class User {
    //properties of the class
    public String username;
    public Integer solved;

    // default constructor is important for firebase
    public User() {}

    // 'normal' constructor of the class
    public User(String username, Integer solved) {
        this.username = username;
        this.solved = solved;
    }
}
