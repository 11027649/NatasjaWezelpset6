package e.natasja.natasjawezel__pset6;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class QuestionActivity extends AppCompatActivity {
    String apiKey;
    RequestQueue queue;
    private static final String TAG = "QuestionActivity";
    ArrayList<String> questionsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        // use a trivia api to do this: it's not secure so you only need the url
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
                                Log.d(TAG, "question is: " + question);

                                // construct data source
                                questionsArray = new ArrayList<>();

                            }
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

        queue.add(jsObjRequest);
    }


}
