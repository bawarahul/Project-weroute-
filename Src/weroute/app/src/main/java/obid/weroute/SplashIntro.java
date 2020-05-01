package obid.weroute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashIntro extends AppCompatActivity {
     Timer timer=new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_intro);
        LottieAnimationView animationView=(LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("weroute_intro.json");
        animationView.loop(false);
        animationView.playAnimation();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                startActivity(new Intent(SplashIntro.this, MainActivity.class));
                finish();
            }
        },4000);
    }
}
