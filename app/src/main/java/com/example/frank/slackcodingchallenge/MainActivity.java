package com.example.frank.slackcodingchallenge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    public static final String NODE_MEMBERS = "members";
    public static final String NODE_ID = "id";
    public static final String NODE_NAME = "name";
    public static final String NODE_DELETED = "deleted";
    public static final String NODE_COLOR = "color";
    public static final String NODE_PROFILE = "profile";
    public static final String NODE_PROFILE_FIRST_NAME = "first_name";
    public static final String NODE_PROFILE_LAST_NAME = "last_name";
    public static final String NODE_PROFILE_REAL_NAME = "real_name";
    public static final String NODE_PROFILE_TITLE = "title";
    public static final String NODE_PROFILE_EMAIL = "email";
    public static final String NODE_PROFILE_SKYPE = "skype";
    public static final String NODE_PROFILE_PHONE = "phone";
    public static final String NODE_PROFILE_IMAGE_24 = "image_24";
    public static final String NODE_PROFILE_IMAGE_32 = "image_32";
    public static final String NODE_PROFILE_IMAGE_48 = "image_48";
    public static final String NODE_PROFILE_IMAGE_72 = "image_72";
    public static final String NODE_PROFILE_IMAGE_192 = "image_192";


    // View that will display all users
    private ListView mUserListView;
    // List that holds each users info
    private ArrayList<HashMap<String,String>> mUsersList;

    private JSONArray mUsers;
    private String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializations
        mUsersList = new ArrayList<HashMap<String, String>>();
        mUserListView = (ListView) findViewById(R.id.usersList);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        mUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                // Get the corresponding userinfo
                HashMap<String,String> info = mUsersList.get(position);
                intent.putExtra(NODE_PROFILE_REAL_NAME, info.get(NODE_PROFILE_REAL_NAME));
                intent.putExtra(NODE_PROFILE_EMAIL, info.get(NODE_PROFILE_EMAIL));
                intent.putExtra(NODE_PROFILE_SKYPE, info.get(NODE_PROFILE_SKYPE));
                intent.putExtra(NODE_PROFILE_PHONE, info.get(NODE_PROFILE_PHONE));
                intent.putExtra(NODE_PROFILE_TITLE, info.get(NODE_PROFILE_TITLE));
                intent.putExtra(NODE_NAME, info.get(NODE_NAME));
                intent.putExtra(NODE_PROFILE_IMAGE_24, info.get(NODE_PROFILE_IMAGE_24));
                intent.putExtra(NODE_PROFILE_IMAGE_32, info.get(NODE_PROFILE_IMAGE_32));
                intent.putExtra(NODE_PROFILE_IMAGE_48, info.get(NODE_PROFILE_IMAGE_48));
                intent.putExtra(NODE_PROFILE_IMAGE_72, info.get(NODE_PROFILE_IMAGE_72));
                intent.putExtra(NODE_PROFILE_IMAGE_192, info.get(NODE_PROFILE_IMAGE_192));
                startActivity(intent);
            }
        });

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Slack Test Team User List");
        setSupportActionBar(toolbar);

        // Check for network
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        boolean isConnected = network != null && network.isConnectedOrConnecting();
        String cache = getPreferences(0).getString("cache", null);

        if(!isConnected){
            // If first time launch (i.e. cachedData file does not exist)
            if(cache == null){
                Toast.makeText(this, "No Internet connection detected...", Toast.LENGTH_SHORT).show();
            }
            // Use cached data
            else{
                readCachedData(cache);
            }
        }
        // If there IS internet connection BUT no cached data, make a web service query
        else if(cache == null){
            new GetUsersList().execute();
        }
        else{
            readCachedData(cache);
        }
    }

    /***
     * Read from the cache data file to pull user list data
     * @param cacheFileDir - String representation of the cache file directory
     */
    private void readCachedData(String cacheFileDir){
        try {
            ObjectInputStream oin = new ObjectInputStream(new FileInputStream(cacheFileDir));
            mUsersList = (ArrayList<HashMap<String,String>>) oin.readObject();
            oin.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ListAdapter adapter = new SimpleAdapter(getApplicationContext(), mUsersList, R.layout.list_item,
                new String[]{NODE_PROFILE_REAL_NAME,NODE_PROFILE_TITLE},
                new int[]{R.id.name,R.id.title});
        mUserListView.setAdapter(adapter);
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
            mJsonString = restHandler.makeCall(url, RestHandler.GET, params);

            if(mJsonString != null){
                try {
                    JSONObject jsonObject = new JSONObject(mJsonString);
                    mUsers = jsonObject.getJSONArray(NODE_MEMBERS);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                HashMap<String,String> userInfo = null;

                try{
                    // loop through all users
                    for(int i=0; i<mUsers.length(); i++){
                        userInfo = null;
                        JSONObject user = mUsers.getJSONObject(i);

                        // Create a hashmap to save name,value pair of user info
                        userInfo = new HashMap<String, String>();

                        String id = user.getString(NODE_ID);
                        String name = user.getString(NODE_NAME);
                        userInfo.put(NODE_NAME, name);

                        JSONObject profile = user.getJSONObject(NODE_PROFILE);
                        String realName = profile.getString(NODE_PROFILE_REAL_NAME);
                        userInfo.put(NODE_PROFILE_REAL_NAME, realName);

                        String title = profile.getString(NODE_PROFILE_TITLE);
                        userInfo.put(NODE_PROFILE_TITLE, title);

                        String email = profile.getString(NODE_PROFILE_EMAIL);
                        userInfo.put(NODE_PROFILE_EMAIL, email);

                        String skype = profile.getString(NODE_PROFILE_SKYPE);
                        userInfo.put(NODE_PROFILE_SKYPE, skype);

                        String phone = profile.getString(NODE_PROFILE_PHONE);
                        // Make all phone numbers in the format xxx-xxx-xxxx
                        // ASSUMES ALL PHONE NUMBERS PROVIDED ARE 7 DIGITS
                        phone = phone.replace("-","");
                        String areacode = phone.substring(0,3).concat("-");
                        String part1 = phone.substring(3,6).concat("-");
                        String part2 = phone.substring(6, 10);
                        phone = areacode + part1 + part2;
                        userInfo.put(NODE_PROFILE_PHONE, phone);

                        String imageURL24 = profile.getString(NODE_PROFILE_IMAGE_24);
                        userInfo.put(NODE_PROFILE_IMAGE_24, imageURL24);
                        String imageURL32 = profile.getString(NODE_PROFILE_IMAGE_32);
                        userInfo.put(NODE_PROFILE_IMAGE_32, imageURL32);
                        String imageURL48 = profile.getString(NODE_PROFILE_IMAGE_48);
                        userInfo.put(NODE_PROFILE_IMAGE_48, imageURL48);
                        String imageURL72 = profile.getString(NODE_PROFILE_IMAGE_72);
                        userInfo.put(NODE_PROFILE_IMAGE_72, imageURL72);
                        String imageURL192 = profile.getString(NODE_PROFILE_IMAGE_192);
                        userInfo.put(NODE_PROFILE_IMAGE_192, imageURL192);

                        mUsersList.add(userInfo);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                finally{
                    // Add user info - ensures that if a JSONException occurrs due to
                    // missing JSON response fields, that userinfo is still addded
                    mUsersList.add(userInfo);
                }
            }
            // Cache user info to local file
            File cacheDir = getCacheDir();
            File cachedData = new File(cacheDir, "userData");
            try {
                FileOutputStream fos = new FileOutputStream(cachedData);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(mUsersList);
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Keep a record of cached data file name
            SharedPreferences prefs = getPreferences(0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("cache", cachedData.getAbsolutePath());
            editor.commit();

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            // Update usersList ListView with new parsed JSON data
            ListAdapter adapter = new SimpleAdapter(getApplicationContext(), mUsersList, R.layout.list_item,
                    new String[]{NODE_PROFILE_REAL_NAME,NODE_PROFILE_TITLE},
                    new int[]{R.id.name,R.id.title});
            mUserListView.setAdapter(adapter);
        }
    }
}
