package andremacnamara.com.macnamaraweather;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class splashScreen extends AppCompatActivity {

    private static int spalshScreenTime = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.splash_screen );
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                Intent MainActivityIntent = new Intent(splashScreen.this, TodaysWeather.class);
                startActivity(MainActivityIntent);
                finish();
            }
        },spalshScreenTime);

        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        TextView TagLineText = (TextView) findViewById(R.id.taglineText);
        TagLineText.startAnimation(animation);
    }

}

