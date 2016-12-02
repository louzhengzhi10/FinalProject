package com.example.samuel.finalproject;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by mengxiongliu on 27/11/2016.
 */

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private List<Comment> comments;
    private int restaurant_id;

    public CommentListAdapter(Activity context, int resource, List<Comment> comments, int restaurant_id) {
        super(context, resource, comments);
        this.comments = comments;
        this.restaurant_id = restaurant_id;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.comment_list, parent, false);
        }

        final Comment comment = comments.get(position);

        TextView userText = (TextView) view.findViewById(R.id.comment_user);
        userText.setText(comment.getUser());
        TextView dateText = (TextView) view.findViewById(R.id.comment_date);
        dateText.setText(comment.getDate());
        TextView bodyText = (TextView) view.findViewById(R.id.comment_body);
        bodyText.setText(comment.getComment());

        ImageView deleteView = (ImageView) view.findViewById(R.id.delete_comment_icon);
        deleteView.setVisibility(View.VISIBLE);
        // event listener to remove comment
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = null;
                try {
                    // wait for response
                    response = new DeleteCommentTask().execute(comment.getComment()).get();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.equals("Comment deleted")) {
                    comments.remove(position);
                    notifyDataSetChanged();
                }
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT);
            }
        });

        return view;
    }

    /**
     * Asynchronous task to delete comments
     */
    private class DeleteCommentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/remove_comment?user=" + HomeActivity.getUser() + "&restaurant=" + restaurant_id + "&comment=" + params[0].replace(" ", "+");
            String message = Utils.sendHTTPRequest(request, "POST");
            return Utils.parseMessage(message);
        }
    }
}
