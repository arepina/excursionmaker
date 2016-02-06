package com.example.anast.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap googleMap;//карта
    public static boolean flag;//было ли нажатие на карту
    private int obpos, pos;//коды экскурсии и объекта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        Toast.makeText(getApplicationContext(), "Tap on the map to choose the location", Toast.LENGTH_LONG).show();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = supportMapFragment.getMap();//создаем карту
        if (flag) {
            LatLng latLng = new LatLng(MainActivity.exlist.get(pos).objects.get(obpos).oblatitude, MainActivity.exlist.get(pos).objects.get(obpos).oblongitude);//метка уже была размещена
            MarkerOptions markerOptions = new MarkerOptions();//создаем маркер
            markerOptions.position(latLng);//задаем позицию
            markerOptions.title(MainActivity.exlist.get(pos).objects.get(obpos).obname + " " + latLng.latitude + " : " + latLng.longitude);//задаем название
            googleMap.clear();//очищаем позицию, которая была до
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));//анимируем переход к новой метке
            googleMap.addMarker(markerOptions);//помещаем метку на карту
        } else
            flag = false;
        Button back = (Button) findViewById(R.id.backbutstep6);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.exlist.get(pos).objects.get(obpos).oblatitude = 0.0;
                MainActivity.exlist.get(pos).objects.get(obpos).oblongitude = 0.0;
                flag = false;
                finish();
                finishActivity(5);
                Intent intent = new Intent(MapsActivity.this, TextActivity.class);
                intent.putExtra("position", Integer.toString(pos));
                intent.putExtra("obposition", Integer.toString(obpos));
                startActivity(intent);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        final Button selectBtn = (Button) findViewById(R.id.selectBtnstep6);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!flag) {
                    Toast.makeText(getApplicationContext(), "Please choose the location", Toast.LENGTH_LONG).show();
                } else {
                    Intent in = new Intent(MapsActivity.this, PlayActivity.class);
                    in.putExtra("position", Integer.toString(pos));
                    in.putExtra("obposition", Integer.toString(obpos));
                    startActivityForResult(in, 6);
                    // set the animation to move once the button is clicked
                    overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
                }
            }
        });
        //нажатие на карту
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                flag = true;
                MarkerOptions markerOptions = new MarkerOptions();//создаем маркер
                markerOptions.position(latLng);//задаем позицию
                markerOptions.title(MainActivity.exlist.get(pos).objects.get(obpos).obname + " " + latLng.latitude + " : " + latLng.longitude);//задаем название
                MainActivity.exlist.get(pos).objects.get(obpos).oblatitude = latLng.latitude;//широта
                MainActivity.exlist.get(pos).objects.get(obpos).oblongitude = latLng.longitude;//долгота
                googleMap.clear();//очищаем позицию, которая была до
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));//анимируем переход к новой метке
                googleMap.addMarker(markerOptions);//помещаем метку на карту
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    //обработка нажатия на аппаратную кнопку назад
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            flag = false;
            MainActivity.exlist.get(pos).objects.get(obpos).oblatitude = 0.0;
            MainActivity.exlist.get(pos).objects.get(obpos).oblongitude = 0.0;
            finish();
            finishActivity(5);
            Intent intent = new Intent(this, TextActivity.class);
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