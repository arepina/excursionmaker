package com.example.anast.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    public ImageView showphoto;//большое фото
    private int obpos, pos;//коды экскурсии и объекта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_photo);
        Toast.makeText(getApplicationContext(), "Choose photo files now", Toast.LENGTH_SHORT).show();
        showphoto = (ImageView) findViewById(R.id.imageViewlook);
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
        GridView gridview = (GridView) findViewById(R.id.PhoneImageGrid);
        gridview.setAdapter(new ImageAdapter(this));
        Button back = (Button) findViewById(R.id.backbut);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.exlist.get(pos).objects.remove(obpos);
                LoadActivity.checkboxes.clear();
                finish();
                finishActivity(22);
                Intent intent = new Intent(PhotoActivity.this, ObjectActivity.class);
                intent.putExtra("mode", "-2");
                intent.putExtra("position", Integer.toString(pos));
                startActivity(intent);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        final Button selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PhotoActivity.this, VideoActivity.class);
                intent.putExtra("position", Integer.toString(pos));
                intent.putExtra("obposition", Integer.toString(obpos));
                startActivityForResult(intent, 2);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
            }
        });
    }

    //класс - номер фото
    class PhotoNum {
        int num;
    }

    //кастомный адаптер
    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public ImageAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return LoadActivity.imagesList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.galeryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DataClass item = LoadActivity.imagesList.get(position);
            boolean flag = false;
            for (int q = 0; q < LoadActivity.checkboxes.size(); q++)
                if (LoadActivity.checkboxes.get(q).num == position) {
                    item.setSelected(true);
                    flag = true;
                }
            if (!flag) {
                item.setSelected(false);
            }
            Uri uri = Uri.fromFile(new File(LoadActivity.imagesList.get(position).filePath));
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (LoadActivity.imagesList.get(id).isSelected()) {
                        for (int k = 0; k < MainActivity.exlist.get(pos).objects.get(obpos).obselectedphotos.size(); k++)
                            if (MainActivity.exlist.get(pos).objects.get(obpos).obselectedphotos.get(k).id == LoadActivity.imagesList.get(id).id) {
                                MainActivity.exlist.get(pos).objects.get(obpos).obselectedphotos.remove(k);
                                break;
                            }
                        cb.setChecked(false);
                        LoadActivity.imagesList.get(id).setSelected(false);
                        for (int q = 0; q < LoadActivity.checkboxes.size(); q++)
                            if (LoadActivity.checkboxes.get(q).num == id) {
                                LoadActivity.checkboxes.remove(q);
                                q--;
                            }
                    } else {
                        cb.setChecked(true);
                        LoadActivity.imagesList.get(id).setSelected(true);
                        MainActivity.exlist.get(pos).objects.get(obpos).obselectedphotos.add(LoadActivity.imagesList.get(id));
                        PhotoNum e = new PhotoNum();
                        e.num = id;
                        LoadActivity.checkboxes.add(e);
                    }
                    Bitmap myBitmap = BitmapFactory.decodeFile(LoadActivity.imagesList.get(id).filePath);
                    showphoto.setImageBitmap(myBitmap);
                }
            });
            Picasso.with(PhotoActivity.this)
                    .load(uri)
                    .resize(120, 120)
                    .centerCrop()
                    .into(holder.imageview);
            holder.checkbox.setChecked(item.isSelected());
            return convertView;
        }
    }

    //конкретное фото с его checkboxом на экране
    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
    }

    //обработка нажатия на аппаратную кнопку назад
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainActivity.exlist.get(pos).objects.remove(obpos);
            LoadActivity.checkboxes.clear();
            finish();
            finishActivity(22);
            Intent intent = new Intent(this, ObjectActivity.class);
            intent.putExtra("mode", "-2");
            intent.putExtra("position", Integer.toString(pos));
            startActivity(intent);
            // set the animation to move once the button is clicked
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        }
        return false;
    }
}
