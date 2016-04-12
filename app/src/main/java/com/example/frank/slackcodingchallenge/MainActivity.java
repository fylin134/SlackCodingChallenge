package com.example.frank.slackcodingchallenge;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // URL for all Slack APIs
    private static String url = "http://slack.com/api/users.list";
    // Slack test team auth token
    private static String AUTHTOKEN = "xoxp-5048173296-5048487710-19045732087-b5427e3b46";

    // JSON Node Names
    private static final String NODE_MEMBERS = "members";
    private static final String NODE_ID = "id";
    private static final String NODE_NAME = "name";
    private static final String NODE_DELETED = "deleted";
    private static final String NODE_COLOR = "color";
    private static final String NODE_PROFILE = "profile";
    private static final String NODE_PROFILE_FIRST_NAME = "first_name";
    private static final String NODE_PROFILE_LAST_NAME = "last_name";
    private static final String NODE_PROFILE_REAL_NAME = "real_name";
    private static final String NODE_PROFILE_EMAIL = "email";
    private static final String NODE_PROFILE_SKYPE = "skype";
    private static final String NODE_PROFILE_PHONE = "phone";


    // View that will display all users
    private ListView mUserListView;
    // List that holds each users info
    private ArrayList<HashMap<String,String>> mUsersList;


    private JSONArray mUsers;
    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializations
        mUsersList = new ArrayList<HashMap<String, String>>();
        mUserListView = (ListView) findViewById(R.id.usersList);

        mUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
            //TODO- Start new activity for individual user's profile page
        });


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

            // Make HTTP call with params
            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("token", AUTHTOKEN));
            jsonString = restHandler.makeCall(url, RestHandler.GET, params);

            if(jsonString != null){
                try{
                    JSONObject jsonObject = new JSONObject(jsonString);

                    mUsers = jsonObject.getJSONArray(NODE_MEMBERS);

                    // loop through all users
                    for(int i=0; i<mUsers.length(); i++){
                        JSONObject user = mUsers.getJSONObject(i);

                        String id = user.getString(NODE_ID);
                        String name = user.getString(NODE_NAME);

                        JSONObject profile = user.getJSONObject(NODE_PROFILE);
                        String firstName = profile.getString(NODE_PROFILE_FIRST_NAME);
                        String lastName = profile.getString(NODE_PROFILE_LAST_NAME);
                        String realName = profile.getString(NODE_PROFILE_REAL_NAME);
                        String email = profile.getString(NODE_PROFILE_EMAIL);
                        String phone = profile.getString(NODE_PROFILE_PHONE);

                        // Create a hashmap to save name,value pair of user info
                        HashMap<String, String> userInfo = new HashMap<String, String>();
                        userInfo.put(NODE_PROFILE_REAL_NAME, realName);
                        userInfo.put(NODE_PROFILE_PHONE, phone);
                        userInfo.put(NODE_PROFILE_EMAIL, email);

                        // Add user info to master list
                        mUsersList.add(userInfo);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            // Update usersList ListView with new parsed JSON data
            ListAdapter adapter = new SimpleAdapter(getApplicationContext(), mUsersList, R.layout.list_item,
                    new String[]{NODE_PROFILE_REAL_NAME, NODE_PROFILE_PHONE, NODE_PROFILE_EMAIL},
                    new int[]{R.id.name, R.id.mobile, R.id.email});
            mUserListView.setAdapter(adapter);
        }
    }
}
