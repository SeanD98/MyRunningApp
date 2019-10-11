package com.example.myrunningapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myrunningapp.R;

public class HomeActivity extends AppCompatActivity {

    //Declarations
    public Button LogInButton;
    public Button SignUpButton;

    public ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Animations
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        Animation zoom_in = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);

        LogInButton = findViewById(R.id.bttn_log_in);
        SignUpButton = findViewById(R.id.bttn_sign_up);

        logo = findViewById(R.id.imageview_logo);

        LogInButton.startAnimation(slide_up);
        SignUpButton.startAnimation(slide_up);
        logo.startAnimation(zoom_in);

        final Intent goToLogin = new Intent(this, LoginActivity.class);
        final Intent goToSignUp = new Intent(this, SignUpActivity.class);

        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToLogin);
            }
        });

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToSignUp);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home_mode_icon) {
            //Flip the colours
            enableDarkMode();
        }
        return true;
    }

    public void enableDarkMode()
    {
        getWindow().setBackgroundDrawableResource(R.color.dark_mode_bg);

        LogInButton.setBackgroundResource(R.drawable.rounded_button_2);
        SignUpButton.setBackgroundResource(R.drawable.rounded_button_2);

    }

}
