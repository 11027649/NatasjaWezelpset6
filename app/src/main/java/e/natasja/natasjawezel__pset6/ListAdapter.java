package e.natasja.natasjawezel__pset6;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;

/**
 * Created by Natasja on 11-12-2017.
 */

public class ListAdapter extends ArrayAdapter {
    public ListAdapter(Context context, ArrayList<ScoreboardActivity.UserHighscore> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // get the data item for this position
        ScoreboardActivity.UserHighscore userHighscore = (ScoreboardActivity.UserHighscore) getItem(position);

        // check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.row_score, parent, false);
        }

        // lookup view for data population
        TextView score = view.findViewById(R.id.highscore);
        TextView number = view.findViewById(R.id.number);
        TextView username = view.findViewById(R.id.username);

        // populate the data into template view using highscore object
        score.setText(userHighscore.highscore);
        username.setText(userHighscore.username);
        number.setText(userHighscore.number);

        // return the completed view to render on screen
        return view;
    }
}
