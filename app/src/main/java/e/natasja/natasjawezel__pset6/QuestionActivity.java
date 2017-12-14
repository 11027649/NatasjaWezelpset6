package e.natasja.natasjawezel__pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    RequestQueue queue;

    private static final String TAG = "QuestionActivity";

    ArrayList<Question> questionsArray;
    List<RadioButton> buttons;

    Integer questionNumber;
    Integer right_answers;

    TextView questionTextView;
    RadioButton A, B, C, D;
    String correctAnswerButton;
    boolean answered;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        listenToAuthState();

        // find textview and answer radiobuttons
        questionTextView = findViewById(R.id.question);
        A = findViewById(R.id.A);
        B = findViewById(R.id.B);
        C = findViewById(R.id.C);
        D = findViewById(R.id.D);

        // construct data source
        questionsArray = new ArrayList<>();
        questionNumber = 0;
        right_answers = 0;

        request();
    }

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
     * This is a request to a trivia api, that returns questions an answers as a json object.
     */
    public void request() {
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        // use a trivia api to get questions: it's not secured so you only need the url
        String url = "https://opentdb.com/api.php?amount=10&category=17&difficulty=medium&type=multiple";

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray group = response.getJSONArray("results");

                            // loop over JsonArray group and put the words in categoriesArray
                            for (int i = 0; i < group.length(); i++) {

                                JSONObject object = group.getJSONObject(i);

                                String question = object.getString("question");
                                String correct_answer = object.getString("correct_answer");
                                JSONArray incorrect = object.getJSONArray("incorrect_answers");

                                // set back escape characters
                                question = escape(question);
                                correct_answer = escape(correct_answer);
                                String incorrect1 = escape(incorrect.getString(0));
                                String incorrect2 = escape(incorrect.getString(1));
                                String incorrect3 = escape(incorrect.getString(2));

                                // make a newQuestion class
                                Question newQuestion = new Question(question, correct_answer,
                                        incorrect1,
                                        incorrect2,
                                        incorrect3);

                                // add the class instance to the datasource
                                questionsArray.add(newQuestion);

                                Log.d(TAG, "question is: " + question + " and the answer is: "
                                        + correct_answer + questionNumber + "size of array is: " + questionsArray.size());
                            }

                            // set the first question
                            setQuestion();

                        } catch (JSONException e) {

                            // show error (in case of an error) jsonobject to array
                            Log.w(TAG, "JsonObject to Json Array didn't go right.");
                            Toast.makeText(getApplicationContext(), "No response on request.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // show error (in case of error) no response on request
                        Toast.makeText(getApplicationContext(), "No response on request, you " +
                                "probably don't have an internet connection right now.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "No response on request.");
                    }
                });

        // after this, we have an array with our questions, and the answers
        queue.add(jsObjRequest);
    }

    /**
     * Replace the escape characters from the api to the actual characters.
     * I know this is a little ugly, but Julia told me to do it like this :-).
     * @param toReplace
     * @return
     */
    public String escape(String toReplace) {
        toReplace = toReplace.replace("&quot;", "\"");
        toReplace = toReplace.replace("&apos;", "\'");
        toReplace = toReplace.replace("&#039;", "\'");
        toReplace = toReplace.replace("&lt;", "<");
        toReplace = toReplace.replace("&gt;", ">");
        toReplace = toReplace.replace("&amp;", "&");
        toReplace = toReplace.replace("&deg;", "°");
        toReplace = toReplace.replace("&ouml;", "ö");

        return toReplace;
    }

    /**
     * Set the next question.
     */
    public void setQuestion() {

        // get the question at the current position
        Question currentQuestion = questionsArray.get(questionNumber);
        questionTextView.setText(currentQuestion.question);

        buttons = new ArrayList<>();
        buttons.add(A);
        buttons.add(B);
        buttons.add(C);
        buttons.add(D);

        // shuffle the buttons to make sure the right answers is not always the same button
        Collections.shuffle(buttons);

        // set current question's answers text to the buttons
        buttons.get(0).setText(currentQuestion.answer1);
        buttons.get(1).setText(currentQuestion.answer2);
        buttons.get(2).setText(currentQuestion.answer3);
        buttons.get(3).setText(currentQuestion.correctAnswer);

        // save correct answer button, to check if user answered correctly
        correctAnswerButton = buttons.get(3).getText().toString();

        Log.d(TAG, "Right answer button: " + correctAnswerButton);

        questionNumber += 1;
    }

    /**
     * Check answer and call the set Question function to set the next Question.
     * @param view
     */
    public void nextQuestion(View view) {
        Log.d(TAG, "Amount of right answers: " + right_answers);
        buttons.clear();
        answered = false;

        // check buttons for right answer, and if there's answered at all
        control(A);
        control(B);
        control(C);
        control(D);

        if (answered == true) {
            // if not 10th question yet, set next question
            if (questionNumber < 10) {
                setQuestion();
            } else {
                // update solved amount in database
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                Log.d(TAG, "updating solved amount to: " + right_answers);
                mDatabase.child("users").child(user.getUid()).child("solved").setValue(right_answers);

                Intent intent = new Intent(this, ScoreboardActivity.class);
                startActivity(intent);
                finish();
            }

        }
        // if answered is still false, no radiobutton was checked
        else {
            Toast.makeText(this, "You have to select an answer.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A function to check if the radiobutton with the right answer was checked.
     * @param button
     */
    public void control(RadioButton button) {
        if (button.isChecked()){
            answered = true;

            if (button.getText().toString() == correctAnswerButton) {
                Toast.makeText(this, "You've answered right!", Toast.LENGTH_SHORT).show();
                right_answers += 1;
            } else {
                Toast.makeText(this, "This answer was wrong...", Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * A question class to construct the data source for the questions.
     */
    public class Question {
        String question;
        String correctAnswer;
        String answer1;
        String answer2;
        String answer3;

        public Question(String aQuestion, String aCorrectAnswer, String anAnswer1, String anAnswer2, String anAnswer3) {
            this.question = aQuestion;
            this.correctAnswer = aCorrectAnswer;
            this.answer1 = anAnswer1;
            this.answer2 = anAnswer2;
            this.answer3 = anAnswer3;
        }


    }

}
