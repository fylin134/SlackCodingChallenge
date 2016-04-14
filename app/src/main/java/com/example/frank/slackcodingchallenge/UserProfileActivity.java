package com.example.frank.slackcodingchallenge;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

/**
 * Created by Frank on 4/13/2016.
 */
public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        Intent intent = getIntent();
        String real_name = intent.getStringExtra(MainActivity.NODE_PROFILE_REAL_NAME);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(""+real_name);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        setSupportActionBar(toolbar);

        // Populate user profile picture
        ImageView pic = (ImageView) findViewById(R.id.image_profile);
        String imagePath = intent.getStringExtra(MainActivity.TAG_IMAGE);
        if(imagePath != null){
            pic.setBackground(Drawable.createFromPath(imagePath));
        }
        else{
          pic.setBackgroundResource(R.drawable.ic_slack_icon);
        }

        // Populate profile info
        TextView username = (TextView) findViewById(R.id.text_username);
        username.setText("@"+intent.getStringExtra(MainActivity.NODE_NAME));
        TextView title = (TextView) findViewById(R.id.text_title);
        title.setText(intent.getStringExtra(MainActivity.NODE_PROFILE_TITLE));
        TextView email = (TextView) findViewById(R.id.text_email);
        email.setText(intent.getStringExtra(MainActivity.NODE_PROFILE_EMAIL));
        TextView skype = (TextView) findViewById(R.id.text_skype);
        skype.setText(intent.getStringExtra(MainActivity.NODE_PROFILE_SKYPE));
        TextView phone = (TextView) findViewById(R.id.text_phone);
        phone.setText(intent.getStringExtra(MainActivity.NODE_PROFILE_PHONE));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}