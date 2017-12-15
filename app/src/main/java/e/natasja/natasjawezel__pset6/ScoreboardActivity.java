package e.natasja.natasjawezel__pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ScoreboardActivity extends AppCompatActivity {
    final static String TAG = "ScoreboardActivity";
    public FirebaseUser user;

    ListView myList;
    ArrayList<UserHighscore> scoreboardArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        // set up data source
        myList = findViewById(R.id.scoreboardListview);
        scoreboardArray = new ArrayList<>();

        listenToAuthState();

        populateScoreList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Couple actions to the menu items.
     * @param item
     * Takes the item that is clicked as input.
     * @return true
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
                Toast.makeText(this, "You're already at the scoreboard!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.mainMenu:
                Intent toMainMenu = new Intent(this, MainActivity.class);
                startActivity(toMainMenu);
                finish();
                break;
        }

        return true;
    }

    /**
     * Make sure users that aren't logged in can't reach this page.
     */
    public void listenToAuthState() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // check if user is really logged in
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged;signed_in" + user.getUid());

                } else {
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged;signed_out");
                    Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(goToLogin);
                    finish();
                }

            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Populate the scoreboard listview with users with highest highscore.
     */
    public void populateScoreList() {
        // find firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("users");

        // order by amount of points the users collected
        Query myTopSolvedQuery = databaseReference.orderByChild("solved").limitToLast(10);

        // add these users to an array, and set them in the listview of the scoreboard
        myTopSolvedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                scoreboardArray.clear();

                // iterate over the datasnapshot (which consists of 10 users)
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                Log.d(TAG, "Total Users : " + dataSnapshot.getChildrenCount());

                // give the ten best users a placement: the lowest, which is the first is number 10
                Integer i = 10;

                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    String name, solved;

                    // get values
                    name = item.child("username").getValue().toString();
                    solved = item.child("solved").getValue().toString();

                    // add userhighscore to array
                    UserHighscore newUserHighscore = new UserHighscore(i.toString(), name, solved);
                    Log.d(TAG, "New userhighscore: " + newUserHighscore.username + newUserHighscore.highscore + newUserHighscore.number);
                    scoreboardArray.add(newUserHighscore);

                    i -= 1;

                }

                // reverse scoreboardArray (because orderByValue orders in ascending order)
                Collections.reverse(scoreboardArray);

                // set adapter
                ListAdapter mAdapter = new ListAdapter(getApplicationContext(), scoreboardArray);
                myList.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }


}
