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

        // control if user is really logged in
        mAuth = FirebaseAuth.getInstance();
        setListener();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void setListener() {
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
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged;signed_out");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "I'm listening for signed in users.");

    }

    public void addToDb() {
        String username = user.getEmail().split("@")[0];
        Log.d(TAG, username);

        // add user to db
        User aUser = new User(username, 0);
        String userId = user.getUid();

        mDatabase.child("users").child(userId).setValue(aUser);


    }

    public void startQuiz(View view) {
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        return true;
    }
}
