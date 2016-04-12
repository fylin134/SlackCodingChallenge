package com.example.frank.slackcodingchallenge;

import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // URL for all Slack APIs
    private static String url = "http://slack.com/api/users.list";
    // Slack test team auth token
    private static String AUTHTOKEN = "xoxp-5048173296-5048487710-19045732087-b5427e3b46";
    // List of available Slack APIs
    String[] apiList = new String[]{"users.list"};

    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetUsersList().execute();
    }

    /***
     * Async task to make Slack users.list HTTP call
     */
    private class GetUsersList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //TODO: Cool loading graphic
        }

        @Override
        protected Void doInBackground(Void... args) {
            RestHandler restHandler = new RestHandler();

            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("token", AUTHTOKEN));
            jsonString = restHandler.makeCall(url, params);

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            TextView tv = (TextView) findViewById(R.id.text1);
            tv.setText(jsonString);
        }
    }
}
