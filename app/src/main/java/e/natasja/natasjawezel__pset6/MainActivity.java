package e.natasja.natasjawezel__pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // control if the user is really logged in
        setAuthListener();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Set auth state listener to make sure a user that is not logged in, can't reach this activity.
     */
    public void setAuthListener() {
        // get instance of the auth firebase
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged;signed_in" + user.getUid());

                    // check if user has logged in for the first time, if so, add him to db
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if(snapshot.child("users").hasChild(user.getUid())) {
                                // it's not the first time
                                Toast.makeText(getApplicationContext(), "Welcome back!", Toast.LENGTH_SHORT).show();

                                String username = user.getEmail().split("@")[0];
                                TextView welcome = findViewById(R.id.welcome);
                                welcome.setText("You are logged in as: " + username);
                            } else {
                                addToDb();
                                Toast.makeText(getApplicationContext(), "Welcome to this application!", Toast.LENGTH_SHORT).show();

                                String username = user.getEmail().split("@")[0];
                                TextView welcome = findViewById(R.id.welcome);
                                welcome.setText("You are logged in as: " + username);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // user is signed out, go to login activity
                    Log.d(TAG, "onAuthStateChanged;signed_out");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     *  Add user with a solved amount of 0 to the database.
     */
    public void addToDb() {

        // auto generate a username
        String username = user.getEmail().split("@")[0];
        Log.d(TAG, username);

        // make a new user and find user id
        User aUser = new User(username, 0);
        String userId = user.getUid();

        // add user to database
        mDatabase.child("users").child(userId).setValue(aUser);
    }

    /**
     * If the quiz is started, go to the questions activity.
     */
    public void startQuiz(View view) {
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Create the menu with options.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        // inflate the menu resource xml file
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Couple actions to the options in the menu.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                Intent logout = new Intent(this, LoginActivity.class);
                startActivity(logout);
                finish();
                break;

            case R.id.scoreboard:
                Intent toScoreboard = new Intent(this, ScoreboardActivity.class);
                startActivity(toScoreboard);
                finish();
                break;

            case R.id.mainMenu:
                Intent toMainMenu = new Intent(this, MainActivity.class);
                startActivity(toMainMenu);
                finish();
                break;
        }

        return true;
    }
}
