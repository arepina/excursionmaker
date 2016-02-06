package com.example.anast.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TextActivity extends AppCompatActivity {

    private EditText ed;//текст на экране
    public static String text = "";//хранение введенного пользователем текста
    private int obpos, pos;//коды экскурсии и объекта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Toast.makeText(getApplicationContext(), "Write text now", Toast.LENGTH_LONG).show();
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
        ed = (EditText) findViewById(R.id.editText);
        if (text.length() != 0) {
            ed.setText(text);
            ed.setSelection(ed.getText().length());
        }
        Button back = (Button) findViewById(R.id.backbutstep5);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.exlist.get(pos).objects.get(obpos).obobjectText = "";
                text = "";
                finish();
                finishActivity(4);
                Intent intent = new Intent(TextActivity.this, AudioActivity.class);
                intent.putExtra("position", Integer.toString(pos));
                intent.putExtra("obposition", Integer.toString(obpos));
                startActivity(intent);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        final Button selectBtn = (Button) findViewById(R.id.selectBtnstep5);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Conn()) {
                    text = ed.getText().toString();
                    if (text.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please write at least one word", Toast.LENGTH_LONG).show();
                    } else {
                        MainActivity.exlist.get(pos).objects.get(obpos).obobjectText = text;
                        Intent in = new Intent(TextActivity.this, MapsActivity.class);
                        in.putExtra("position", Integer.toString(pos));
                        in.putExtra("obposition", Integer.toString(obpos));
                        startActivityForResult(in, 5);
                        // set the animation to move once the button is clicked
                        overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
                    }
                }
            }
        });
    }

    //проверка наличия соединения с интернетом
    public boolean Conn() {
        //Создаем пример класса connection detector:
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        //Получаем статус Интернет
        boolean isInternetPresent = cd.ConnectingToInternet();
        //Проверяем Интернет статус:
        if (isInternetPresent) {
            return true;
        } else {
            //Интернет соединения нет просим пользователя подключить Интернет:
            Toast.makeText(getApplicationContext(), "You need to turn on the Internet connection!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_text, menu);
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
            MainActivity.exlist.get(pos).objects.get(obpos).obobjectText = "";
            text = "";
            finish();
            finishActivity(4);
            Intent intent = new Intent(this, AudioActivity.class);
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
