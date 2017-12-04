package e.natasja.natasjawezel__pset6;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "Fire base test";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // control if user is really logged in

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void addToDb(View view) {
        // put highscore and finished in
    }


}
