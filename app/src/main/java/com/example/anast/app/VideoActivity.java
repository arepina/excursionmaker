package com.example.anast.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends FragmentActivity {

    private VideoView player;//видеоплеер
    private boolean bVideoIsBeingTouched = false;//нажал ли пользователь на видеоплеер
    private Handler mHandler = new Handler();//работа с видео на плеере
    private int length;//время, когда видео было остановлено
    public static List<VideoNum> checkboxes = new ArrayList<>();//номера выбранных чекбоксов у видео
    public static List<ViewHolder> viewholders = new ArrayList<>();//список всех видео-хранилищ
    private int obpos, pos;//коды экскурсии и объекта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toast.makeText(getApplicationContext(), "Choose video files now", Toast.LENGTH_LONG).show();
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
        player = (VideoView) findViewById(R.id.videofromlist);
        player.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!bVideoIsBeingTouched) {
                    bVideoIsBeingTouched = true;
                    if (player.isPlaying()) {
                        player.pause();
                        length = player.getCurrentPosition();
                    } else {
                        player.seekTo(length);
                        player.start();
                    }
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            bVideoIsBeingTouched = false;
                        }
                    }, 100);
                }
                return true;
            }
        });
        Button back = (Button) findViewById(R.id.backbutstep2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.exlist.get(pos).objects.get(obpos).obselectedvideos.clear();
                checkboxes.clear();
                viewholders.clear();
                finish();
                finishActivity(2);
                Intent intent = new Intent(VideoActivity.this, PhotoActivity.class);
                intent.putExtra("position", Integer.toString(pos));
                intent.putExtra("obposition", Integer.toString(obpos));
                startActivity(intent);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        ListView listView = (ListView) this.findViewById(R.id.videolist);
        listView.setAdapter(new VideoGalleryAdapter());
        final Button selectBtn = (Button) findViewById(R.id.selectBtnstep2);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(VideoActivity.this, AudioActivity.class);
                in.putExtra("position", Integer.toString(pos));
                in.putExtra("obposition", Integer.toString(obpos));
                startActivityForResult(in, 3);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
            }
        });
    }

    //класс нужный для добавления номера конкретного видео в список выбранных пользователем
    class VideoNum {
        int num;
    }

    //класс, отвечающий за отображения списка видео на gridview, нажатие на элементы, возврат исходных значений при возврате из последующей активности
    class VideoGalleryAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public VideoGalleryAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return LoadActivity.videosList.size();
        }

        public Object getItem(int position) {
            return LoadActivity.videosList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        //инициализация, загрузка видео с карты памяти
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.video_info, null);
                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            DataClass vid = LoadActivity.videosList.get(position);
            boolean flag = false;
            for (int q = 0; q < checkboxes.size(); q++)
                if (checkboxes.get(q).num == position) {
                    vid.setSelected(true);
                    flag = true;
                }
            if (!flag)
                vid.setSelected(false);
            holder.name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    DataClass w = (DataClass) cb.getTag();
                    int id = 0;
                    for (int i = 0; i < LoadActivity.videosList.size(); i++) {
                        if (LoadActivity.videosList.get(i).filePath.equals(w.filePath)) {
                            id = i;
                            break;
                        }
                    }
                    if (LoadActivity.videosList.get(id).isSelected()) {
                        player.pause();
                        length = player.getCurrentPosition();
                        for (int k = 0; k < MainActivity.exlist.get(pos).objects.get(obpos).obselectedvideos.size(); k++)
                            if (MainActivity.exlist.get(pos).objects.get(obpos).obselectedvideos.get(k).id == LoadActivity.videosList.get(id).id) {
                                MainActivity.exlist.get(pos).objects.get(obpos).obselectedvideos.remove(k);
                                break;
                            }
                        cb.setChecked(false);
                        LoadActivity.videosList.get(id).setSelected(false);
                        for (int q = 0; q < checkboxes.size(); q++)
                            if (checkboxes.get(q).num == id) {
                                checkboxes.remove(q);
                                q--;
                            }
                    } else {
                        cb.setChecked(true);
                        player.setVideoPath(w.filePath);
                        try {
                            player.start();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Sorry, it is impossible to play your video", Toast.LENGTH_LONG).show();
                        }
                        LoadActivity.videosList.get(id).setSelected(true);
                        MainActivity.exlist.get(pos).objects.get(obpos).obselectedvideos.add(LoadActivity.videosList.get(id));
                        VideoNum e = new VideoNum();
                        e.num = id;
                        checkboxes.add(e);
                    }
                }
            });
            holder.code.setText(vid.title);
            holder.name.setChecked(vid.isSelected());
            holder.name.setTag(vid);
            viewholders.add(holder);
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video, menu);
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
            MainActivity.exlist.get(pos).objects.get(obpos).obselectedvideos.clear();
            checkboxes.clear();
            viewholders.clear();
            finish();
            finishActivity(2);
            Intent intent = new Intent(this, PhotoActivity.class);
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
