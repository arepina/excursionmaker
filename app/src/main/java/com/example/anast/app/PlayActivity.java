package com.example.anast.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;

public class PlayActivity extends AppCompatActivity {

    private int obpos, pos;//коды экскурсии и объекта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toast.makeText(getApplicationContext(), "That's the final step", Toast.LENGTH_LONG).show();
        try {
            pos = Integer.parseInt(getIntent().getExtras().getString("position"));
        } catch (Exception e) {
            pos = 0;
        }
        try {
            obpos = Integer.parseInt(getIntent().getExtras().getString("obposition"));
        } catch (Exception e) {
            obpos = 0;
        }
        TextView text = (TextView) findViewById(R.id.finaltext);
        String str = "<font color='#1b9900'>Your object's name: </font>" + MainActivity.exlist.get(pos).objects.get(obpos).obname + "<br/>";
        String str1 = "<font color='#1b9900'>The number of photos you've chosen for your object is </font>" + MainActivity.exlist.get(pos).objects.get(obpos).obselectedphotos.size() + "<br/>";
        String str2 = "<font color='#1b9900'>The number of videos you've chosen for your object is </font>" + MainActivity.exlist.get(pos).objects.get(obpos).obselectedvideos.size() + "<br/>";
        String str3 = "<font color='#1b9900'>The number of audios you've chosen for your object is </font>" + MainActivity.exlist.get(pos).objects.get(obpos).obselectedaudios.size() + "<br/>";
        String str4 = "<font color='#1b9900'>The text you've written for your object is </font>" + MainActivity.exlist.get(pos).objects.get(obpos).obobjectText + "<br/>";
        String str5 = "<font color='#1b9900'>The geolocation you've chosen for your object is </font>" + String.format("%.7f", MainActivity.exlist.get(pos).objects.get(obpos).oblatitude) + " : " + String.format("%.7f", MainActivity.exlist.get(pos).objects.get(obpos).oblongitude) + "<br/><br/>";
        String str6 = "<font color='white'><b>If you want to save your object press the button below</b></font>" + "<br/>";
        String s = str + str1 + str2 + str3 + str4 + str5 + str6;
        text.setText(Html.fromHtml(s), TextView.BufferType.SPANNABLE);
        Button back = (Button) findViewById(R.id.backbutstep4);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                finishActivity(6);
                Intent intent = new Intent(PlayActivity.this, MapsActivity.class);
                intent.putExtra("position", Integer.toString(pos));
                intent.putExtra("obposition", Integer.toString(obpos));
                startActivity(intent);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        final Button selectBtn = (Button) findViewById(R.id.selectBtnstep4);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    LoadActivity.checkboxes.clear();
                    VideoActivity.checkboxes.clear();
                    VideoActivity.viewholders.clear();
                    AudioActivity.checkboxes.clear();
                    AudioActivity.viewholders.clear();
                    TextActivity.text = "";
                    MapsActivity.flag = false;
                    finish();
                    finishActivity(5);
                    ObjectActivity.mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Intent intent = new Intent(PlayActivity.this, ObjectActivity.class);
                            intent.putExtra("mode", "-2");
                            intent.putExtra("position", Integer.toString(pos));
                            startActivity(intent);
                            // set the animation to move once the button is clicked
                            overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
                        }
                    });
                    if (ObjectActivity.mInterstitialAd.isLoaded())
                        ObjectActivity.mInterstitialAd.show();
                    else {
                        Intent intent = new Intent(PlayActivity.this, ObjectActivity.class);
                        intent.putExtra("mode", "-2");
                        intent.putExtra("position", Integer.toString(pos));
                        startActivity(intent);
                        // set the animation to move once the button is clicked
                        overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //обработка нажатия на аппаратную кнопку назад
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            finishActivity(6);
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("position", Integer.toString(pos));
            intent.putExtra("obposition", Integer.toString(obpos));
            startActivity(intent);
            // set the animation to move once the button is clicked
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        }
        return false;
    }
}
