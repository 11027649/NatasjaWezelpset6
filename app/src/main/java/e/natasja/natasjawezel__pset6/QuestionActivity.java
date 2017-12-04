package e.natasja.natasjawezel__pset6;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {
    RequestQueue queue;
    private static final String TAG = "QuestionActivity";
    ArrayList<Question> questionsArray;
    Integer questionNumber;
    TextView questionTextView;
    RadioButton A, B, C, D;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // find textview and answer radiobuttons
        questionTextView = findViewById(R.id.question);
        A = findViewById(R.id.A);
        B = findViewById(R.id.B);
        C = findViewById(R.id.C);
        D = findViewById(R.id.D);

        // construct data source
        questionsArray = new ArrayList<>();
        questionNumber = 0;

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        // use a trivia api to get questions: it's not secure so you only need the url
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

                                Question newQuestion = new Question(question, correct_answer, "a", "b", "c");
                                questionsArray.add(newQuestion);

                                Log.d(TAG, "question is: " + question + " and the answer is: " + correct_answer + questionNumber + "size of array is: " + questionsArray.size());
                            }

                            // set the first question
                            setQuestion();

                        } catch (JSONException e) {

                            // show error (in case of an error) jsonobject to array
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // show error (in case of error) no response on request
                    }
                });

        // after this, we have an array with our questions, and the answers
        queue.add(jsObjRequest);
    }

    public void setQuestion() {
        Question currentQuestion = questionsArray.get(questionNumber);
        questionTextView.setText(currentQuestion.question);

        A.setText(currentQuestion.answer1);
        B.setText(currentQuestion.answer2);
        C.setText(currentQuestion.answer3);
        D.setText(currentQuestion.correctAnswer);

        questionNumber += 1;
    }

    public void nextQuestion(View view) {
        setQuestion();
    }

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
