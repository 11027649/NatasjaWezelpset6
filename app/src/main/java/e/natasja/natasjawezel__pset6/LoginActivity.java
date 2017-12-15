package e.natasja.natasjawezel__pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "LoginActivity";

    Button signInButton;
    EditText emailEditText, passwordEditText, verifyPasswordEditText;
    TextView registerButton;
    String email, password, verifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // find layout views
        emailEditText= findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        verifyPasswordEditText = findViewById(R.id.verifyPassword);
        verifyPasswordEditText.setVisibility(View.INVISIBLE);
        signInButton = findViewById(R.id.signinButton);
        registerButton = findViewById(R.id.registerButton);

        // listen for user login, like in the explanation video
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged;signed_in" + user.getUid());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged;signed_out");
                }

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Log in existing users, or create account for users that want to register.
     */
    public void signIn(View view) {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        // the user tries to register
        if (signInButton.getText().equals("Register")) {
            // check if password length is long enough for FireBase
            if (password.length() < 7) {
                Toast.makeText(this, "Password must 7 characters or longer.",
                        Toast.LENGTH_SHORT).show();
            } else {
                verifyPassword = verifyPasswordEditText.getText().toString();

                if (verifyPassword.equals(password)) {
                    createUser();
                    logIn(view);
                } else {
                    Toast.makeText(LoginActivity.this, "Passwords are not equal",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        // users tries to log in
        else {
            Log.d(TAG, "Logging in: email: " + email + " and password: " + password);
            logIn(view);
        }
    }

    /**
     * If a user is going to register, change the page layout accordingly.
     */
    public void register(View view) {
        verifyPasswordEditText.setVisibility(View.VISIBLE);
        verifyPasswordEditText.setHint("Verify password");
        registerButton.setVisibility(View.INVISIBLE);
        signInButton.setText(R.string.register);
    }

    /**
     * If a user registers, create the user in the database.
     */
    public void createUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Created user: "
                                    + email, Toast.LENGTH_SHORT).show();

                        } else {

                            // if sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure");
                            Toast.makeText(LoginActivity.this, "You're email isn't right," +
                                    " or you've already registered", Toast.LENGTH_SHORT).show();
                            Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(goToLogin);
                        }
                    }
                });
    }

    /**
     * Log In existing users.
     */
    public void logIn(View view) {

        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, "Please fill in your password and email adress.", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

}
