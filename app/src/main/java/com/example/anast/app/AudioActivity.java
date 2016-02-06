package com.example.anast.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btnPlay;//кнопка плей
    private TextView SongsName;//имя трека
    private ImageView Cover;//art album
    private MediaPlayer mp;//аудиоплеер
    private Handler mHandler = new Handler();//работа с аудио на плеере
    private Utilities utils;//утилиты
    private int currentSongIndex;//номер текщей песни
    public static List<AudioNum> checkboxes = new ArrayList<>();//номера выбранных чекбоксов у видео
    public static List<ViewHolder> viewholders = new ArrayList<>();//список всех видео-хранилищ
    private int obpos, pos;//коды экскурсии и объекта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        Toast.makeText(getApplicationContext(), "Choose audio files now", Toast.LENGTH_LONG).show();
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
        currentSongIndex = 0;
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        SongsName = (TextView) findViewById(R.id.SongsName);
        SongsName.setText("Title");
        Cover = (ImageView) findViewById(R.id.trackcover);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adele);
        Cover.setImageBitmap(bitmap);
        mp = new MediaPlayer();
        utils = new Utilities();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (MainActivity.exlist.get(pos).objects.get(obpos).obselectedaudios.size() == 0)
                    return;
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        btnPlay.setImageResource(R.drawable.play);
                    }
                } else {
                    if (mp != null) {
                        playSong(currentSongIndex);
                        btnPlay.setImageResource(R.drawable.stop);
                    }
                }
            }
        });
        ListView listView = (ListView) this.findViewById(R.id.list);
        listView.setAdapter(new AudioGalleryAdapter());
        Button back = (Button) findViewById(R.id.backbutstep3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.pause();
                btnPlay.setImageResource(R.drawable.play);
                MainActivity.exlist.get(pos).objects.get(obpos).obselectedaudios.clear();
                checkboxes.clear();
                viewholders.clear();
                finish();
                finishActivity(3);
                Intent intent = new Intent(AudioActivity.this, VideoActivity.class);
                intent.putExtra("position", Integer.toString(pos));
                intent.putExtra("obposition", Integer.toString(obpos));
                startActivity(intent);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        Button selectBtn = (Button) findViewById(R.id.selectBtnstep3);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                    btnPlay.setImageResource(R.drawable.stop);
                }
                Intent in = new Intent(AudioActivity.this, TextActivity.class);
                in.putExtra("position", Integer.toString(pos));
                in.putExtra("obposition", Integer.toString(obpos));
                startActivityForResult(in, 4);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
            }
        });
    }

    //класс нужный для добавления номера конкретного аудио в список выбранных пользователем
    private class AudioNum {
        int num;
    }

    //класс, отвечающий за отображения списка аудио на gridview, нажатие на элементы, возврат исходных значений при возврате из последующей активности
    class AudioGalleryAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public AudioGalleryAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return LoadActivity.audiosList.size();
        }

        public Object getItem(int position) {
            return LoadActivity.audiosList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        //инициализация, загрузка аудио с карты памяти
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
            DataClass vid = LoadActivity.audiosList.get(position);
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
                    ImageButton pl = (ImageButton) findViewById(R.id.btnPlay);
                    pl.setEnabled(true);
                    CheckBox cb = (CheckBox) v;
                    DataClass w = (DataClass) cb.getTag();
                    int id = 0;
                    for (int i = 0; i < LoadActivity.audiosList.size(); i++) {
                        if (LoadActivity.audiosList.get(i).filePath.equals(w.filePath)) {
                            id = i;
                            break;
                        }
                    }
                    currentSongIndex = LoadActivity.audiosList.indexOf(w);
                    if (LoadActivity.audiosList.get(id).isSelected()) {
                        if (mp.isPlaying()) {
                            if (mp != null) {
                                mp.pause();
                                btnPlay.setImageResource(R.drawable.play);
                            }
                        }
                        for (int k = 0; k < MainActivity.exlist.get(pos).objects.get(obpos).obselectedaudios.size(); k++)
                            if (MainActivity.exlist.get(pos).objects.get(obpos).obselectedaudios.get(k).id == LoadActivity.audiosList.get(id).id) {
                                MainActivity.exlist.get(pos).objects.get(obpos).obselectedaudios.remove(k);
                                break;
                            }
                        cb.setChecked(false);
                        LoadActivity.audiosList.get(id).setSelected(false);
                        for (int q = 0; q < checkboxes.size(); q++)
                            if (checkboxes.get(q).num == id) {
                                checkboxes.remove(q);
                                q--;
                            }
                        //playSong(currentSongIndex);
                    } else {
                        cb.setChecked(true);
                        LoadActivity.audiosList.get(id).setSelected(true);
                        MainActivity.exlist.get(pos).objects.get(obpos).obselectedaudios.add(LoadActivity.audiosList.get(id));
                        AudioNum e = new AudioNum();
                        e.num = id;
                        checkboxes.add(e);
                        playSong(currentSongIndex);
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

    //Получение индекса песни из списка воспроизведения и ее воспроизведение
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            //играем выбранную песню
            playSong(currentSongIndex);
        }
    }

    //воcпроизводим аудио
    public void playSong(int songIndex) {
        try {
            mp.reset();
            mp.setDataSource(LoadActivity.audiosList.get(songIndex).filePath);
            mp.prepare();
            mp.start();
            SongsName.setText(LoadActivity.audiosList.get(songIndex).title);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), LoadActivity.audiosList.get(songIndex).coverPath);
                bitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, true);
            } catch (FileNotFoundException exception) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adele);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Cover.setImageBitmap(bitmap);
            btnPlay.setImageResource(R.drawable.stop);
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
        mp.seekTo(currentPosition);
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        playSong(currentSongIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_audio, menu);
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
            mp.pause();
            btnPlay.setImageResource(R.drawable.play);
            MainActivity.exlist.get(pos).objects.get(obpos).obselectedaudios.clear();
            checkboxes.clear();
            viewholders.clear();
            finish();
            finishActivity(3);
            Intent intent = new Intent(this, VideoActivity.class);
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