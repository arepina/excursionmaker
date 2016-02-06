package com.example.anast.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HAA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ha);
        TextView text = (TextView) findViewById(R.id.connection);
        TextView text1 = (TextView) findViewById(R.id.newob);
        TextView text2 = (TextView) findViewById(R.id.delob);
        TextView text3 = (TextView) findViewById(R.id.droptext);
        TextView text4 = (TextView) findViewById(R.id.sharetext);
        TextView text5 = (TextView) findViewById(R.id.tick);
        TextView text6 = (TextView) findViewById(R.id.obinfo);
        TextView text7 = (TextView) findViewById(R.id.feedbacktext);
        TextView text8 = (TextView) findViewById(R.id.googleplay);
        String str, str1, str2, str3, str4, str5, str6, str7, str8;
        // Узнаем размеры экрана из ресурсов
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        if (displaymetrics.widthPixels >= 2000)//планшеты
        {
            str = "<font color='#1b9900'>Please don't turn off the Internet connection the app won't work without the Internet</font>";
            str1 = "<font color='#1b9900'>To create a new object/excursion click on plus button</font>";
            str2 = "<font color='#1b9900'>To delete object/excursion click on delete button</font>";
            str3 = "<font color='#1b9900'>To login/logout to the Dropbox click on the Dropbox button</font>";
            str4 = "<font color='#1b9900'>To upload the data to the Dropbox click on share button</font>";
            str5 = "<font color='#1b9900'>Press the tick if you want to download the excursion to the Dropbox</font>";
            str6 = "<font color='#1b9900'>To find out all the object's info click on the object twice</font>";
            str7 = "<font color='#1b9900'>To send the letter to the developer press on the envelope on the left</font>";
            str8 = "<font color='#1b9900'>To rate the app in PlayMarket press on its bage on the left</font>";
        } else if (displaymetrics.widthPixels >= 720)//Galazy Nexus и больше
        {
            str = "<font color='#1b9900'>Please don't turn off the Internet connection<br/>the app won't work without the Internet</font>";
            str1 = "<font color='#1b9900'>To create a new object/excursion <br/>click on plus button</font>";
            str2 = "<font color='#1b9900'>To delete object/excursion click on<br/> delete button</font>";
            str3 = "<font color='#1b9900'>To login/logout to the Dropbox<br/> click on the Dropbox button</font>";
            str4 = "<font color='#1b9900'>To upload the data to the Dropbox <br/>click on share button</font>";
            str5 = "<font color='#1b9900'>Press the tick if you want to download<br/> the excursion to the Dropbox</font>";
            str6 = "<font color='#1b9900'>To find out all the object's info<br/> click on the object twice</font>";
            str7 = "<font color='#1b9900'>To send the letter to the developer press on<br/> the envelope on the left</font>";
            str8 = "<font color='#1b9900'>To rate the app in PlayMarket press <br/>on its bage on the left</font>";
        } else {
            str = "<font color='#1b9900'>Please don't turn off the <br/>Internet connection the app <br/>won't work without the Internet</font>";
            str1 = "<font color='#1b9900'>To create a new object/excursion<br/> click on plus button</font>";
            str2 = "<font color='#1b9900'>To delete object/excursion <br/>click on delete button</font>";
            str3 = "<font color='#1b9900'>To login/logout to the Dropbox<br/> click on  the Dropbox button</font>";
            str4 = "<font color='#1b9900'>To upload the data to the Dropbox <br/>click on share button</font>";
            str5 = "<font color='#1b9900'>Press the tick if you<br/> want to download<br/> the excursion to the Dropbox</font>";
            str6 = "<font color='#1b9900'>To find out all the object's <br/>info click on the object twice</font>";
            str7 = "<font color='#1b9900'>To send the letter to the<br/> developer press on<br/> the envelope on the left</font>";
            str8 = "<font color='#1b9900'>To rate the app<br/> in PlayMarket press <br/>on its bage on the left</font>";
        }
        text.setText(Html.fromHtml(str), TextView.BufferType.SPANNABLE);
        text1.setText(Html.fromHtml(str1), TextView.BufferType.SPANNABLE);
        text2.setText(Html.fromHtml(str2), TextView.BufferType.SPANNABLE);
        text3.setText(Html.fromHtml(str3), TextView.BufferType.SPANNABLE);
        text4.setText(Html.fromHtml(str4), TextView.BufferType.SPANNABLE);
        text5.setText(Html.fromHtml(str5), TextView.BufferType.SPANNABLE);
        text6.setText(Html.fromHtml(str6), TextView.BufferType.SPANNABLE);
        text7.setText(Html.fromHtml(str7), TextView.BufferType.SPANNABLE);
        text8.setText(Html.fromHtml(str8), TextView.BufferType.SPANNABLE);
        Button feedback = (Button) findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "ExcursionMaker");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.setData(Uri.parse("mailto:prostorepa@yandex.ru"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        Button rate = (Button) findViewById(R.id.ratebut);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "The link to the app's page in Google Play will be added soon", Toast.LENGTH_SHORT).show();
                //Try Google play
               /* Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.cubeactive.qnotelistfree"));
                if (!MyStartActivity(intent)) {
                    //Market (Google play) app seems not installed, let's try to open a webbrowser
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.cubeactive.qnotelistfree"));
                    if (!MyStartActivity(intent)) {
                        //Well if this also fails, we have run out of options, inform the user.
                        Toast.makeText(getApplicationContext(), "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                    }
                }*/
                //TODO: add link to google play
            }
        });
        Button back = (Button) findViewById(R.id.backhaa);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                finishActivity(10);
                Intent intent = new Intent(HAA.this, MainActivity.class);
                startActivity(intent);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ha, menu);
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
            finishActivity(10);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            // set the animation to move once the button is clicked
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        }
        return false;
    }
}