package com.example.anast.app;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//класс, отвечающий за загрузку медиафайлов в приложение
public class LoadActivity extends AppCompatActivity {

    public static List<DataClass> imagesList = new ArrayList<>();//список всех фото
    public static List<DataClass> videosList = new ArrayList<>();//список всех видео
    public static List<DataClass> audiosList = new ArrayList<>();//список всех аудио
    public static List<String> filepaths = new ArrayList<>();//все фото-пути
    public static List<PhotoActivity.PhotoNum> checkboxes = new ArrayList<>();//номера выбранных чекбоксов у фото
    private Cursor cursor1;//бегунок по списку на карте памяти
    private TextView info;//информация о загрузке
    private ProgressBar horizontalprogress;//прогресс загрузки(заполняющийся столбец)
    public Context context;//нужен для подгрузки аудио
    protected PowerManager.WakeLock mWakeLock;//экран всегда включен пока идет загрузка

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//горизонтальная ориентация экрана
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        AdView mAdView = (AdView) findViewById(R.id.adView);//создание рекламы
        context = this;
        /*AdRequest adRequest = new AdRequest.Builder().build();//TODO: CHANGE to normal
        mAdView.loadAd(adRequest);*/
        AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("96F89B24B609ED5DFE69922744B289BF").build();//запуск рекламы на утройстве
        mAdView.loadAd(adRequest);//подгрузка рекламы
        info = (TextView) findViewById(R.id.progressView);//информация о загрузке
        horizontalprogress = (ProgressBar) findViewById(R.id.progressload);//прогресс загрузки(заполняющийся столбец)
        horizontalprogress.setMax(100);//мксимальный процент загрузки
        LoadTask load = new LoadTask();
        load.execute();//запуск подгрузки
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_load, menu);
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


    class LoadTask extends AsyncTask<Void, String, Void> {

        boolean im = true;//были загружены фото
        boolean vid = true;//были загружены видео
        boolean aud = true;//были загуржены аудио

        //подготовка к загрузке
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            info.setText("Loading...Please wait, you have a lot of media files");
        }

        //загрузка всех необходимых файлов
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (imagesList.size() == 0) {
                    try {
                        PhotoFilePaths();//загружаем фото пути
                        PhotoInic();//загружаем сами фото
                        Log.d("LoadActivity", "loaded images");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (videosList.size() == 0) {
                    try {
                        VideoInic();//загружаем видео
                        Log.d("LoadActivity", "loaded videos");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (audiosList.size() == 0) {
                    try {
                        AudioInic();//загружаем аудио
                        Log.d("LoadActivity", "loaded audios");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isCancelled())//что-то пошло не так
                    return null;
                TimeUnit.SECONDS.sleep(1);// разъединяемся
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //загрузка фото путей
        void PhotoFilePaths() {
            if (filepaths.size() == 0) {
                String[] mediaColumns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.MIME_TYPE};
                final String orderBy = MediaStore.Images.Media._ID;
                cursor1 = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, orderBy);
                if (cursor1.moveToFirst()) {
                    do {
                        filepaths.add(cursor1.getString(cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                    } while (cursor1.moveToNext());
                }
            }
        }

        //загрузка непосредственно самих фото
        void PhotoInic() {
            if (imagesList.size() == 0) {
                final String[] columns = {MediaStore.Images.Thumbnails._ID};
                final String orderBy = MediaStore.Images.Media._ID;
                Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
                if (imagecursor != null) {
                    int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
                    int count = imagecursor.getCount();
                    for (int i = 0; i < count; i++) {
                        imagecursor.moveToPosition(i);
                        int id = imagecursor.getInt(image_column_index);
                        DataClass imageItem = new DataClass();
                        imageItem.id = id;
                        imageItem.filePath = filepaths.get(i);
                        PhotoMakeCheck(imageItem, i);
                        imagesList.add(imageItem);
                        Log.d("LoadActivity", "images added " + i);
                        publishProgress("Images loaded " + i + "/" + count);
                    }
                    imagecursor.close();
                    im = false;
                }
            }
        }

        //загурзка видео
        void VideoInic() {
            if (videosList.size() == 0) {
                String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
                String[] mediaColumns = {MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.MIME_TYPE};
                final String orderBy = MediaStore.Video.Media._ID;
                cursor1 = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, orderBy);
                int z = 0;
                int count = cursor1.getCount();
                if (cursor1.moveToFirst()) {
                    do {
                        DataClass newVVI = new DataClass();
                        int id = cursor1.getInt(cursor1.getColumnIndex(MediaStore.Video.Media._ID));
                        Cursor thumbCursor = managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
                        if (thumbCursor.moveToFirst()) {
                            newVVI.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                        }
                        newVVI.filePath = cursor1.getString(cursor1.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        newVVI.title = cursor1.getString(cursor1.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                        newVVI.mimeType = cursor1.getString(cursor1.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                        newVVI.id = id;
                        videosList.add(newVVI);
                        publishProgress("Videos loaded " + z + "/" + count);
                        Log.d("LoadActivity", "videos added");
                        z++;
                    } while (cursor1.moveToNext());
                    vid = false;
                }
            }
        }

        //загрузка аудио
        void AudioInic() {
            final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            final String[] cursor_cols = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ALBUM_ID};
            final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
            final Cursor cursor = context.getContentResolver().query(uri, cursor_cols, where, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0)
                    return;
            }
            int z = 1;
            int count = cursor.getCount();
            while (cursor.moveToNext()) {
                String track = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                DataClass audioListModel = new DataClass();
                audioListModel.coverPath = albumArtUri;
                audioListModel.title = track;
                audioListModel.filePath = data;
                audiosList.add(audioListModel);
                publishProgress("Audios loaded " + z++ + "/" + count);
                Log.d("LoadActivity", "music added");
            }
            aud = false;
        }

        //делаем фото отмеченными, если такие есть
        public void PhotoMakeCheck(DataClass imageItem, int i) {
            for (int q = 0; q < checkboxes.size(); q++)
                if (checkboxes.get(q).num == i)
                    imageItem.setSelected(true);
        }

        //обновляем процесс загрузки
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                info.setText("" + values[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!im) {
                horizontalprogress.setProgress(33);
                im = true;
            } else if (!vid) {
                horizontalprogress.setProgress(66);
                vid = true;
            } else if (!aud) {
                horizontalprogress.setProgress(100);
                aud = true;
            }
        }

        //заверщаем загрузку
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            info.setText("Finished");
            horizontalprogress.setProgress(0);
            Intent intent = new Intent(LoadActivity.this, MainActivity.class);
            startActivity(intent);
        }

        //что-то пошло не так
        @Override
        protected void onCancelled() {
            super.onCancelled();
            info.setText("Error!!!");
            horizontalprogress.setProgress(0);
        }
    }

    //анализатор нажатия на аппаратную кнопку назад
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity();
            return true;
        }
        return false;
    }
}

