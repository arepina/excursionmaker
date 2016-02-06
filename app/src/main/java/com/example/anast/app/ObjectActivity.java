package com.example.anast.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anast.app.SwipeListView.SwipeListViewCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

public class ObjectActivity extends AppCompatActivity implements SwipeListViewCallback {

    private EditText propname;//имя нового объекта
    private ListView list;//объекты на экране
    private MyAdapter m_Adapter;//адаптер списка всех объектов на экране, чтобы была возможность их удалить
    private String code;//код страницы, откуда пришли
    private int pos;//код экскурсии
    public static InterstitialAd mInterstitialAd;//рекламная переменная

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//горизонтальная ориентация экрана
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);
        code = getIntent().getExtras().getString("mode");
        try {
            pos = Integer.parseInt(getIntent().getExtras().getString("position"));
        } catch (Exception e) {
            pos = 0;
        }
        mInterstitialAd = new InterstitialAd(this);//создание рекламы
        mInterstitialAd.setAdUnitId("ca-app-pub-3197289332528502/5379248076");//номер рекламного полотна
        //AdRequest.Builder builder = new AdRequest.Builder();//создаем баннер
        //AdRequest ad = builder.build();//билдим баннер//TODO: CHANGE to normal
        AdRequest ad = new com.google.android.gms.ads.AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("96F89B24B609ED5DFE69922744B289BF").build();
        mInterstitialAd.loadAd(ad);//загружаем рекламы
        TextView exname = (TextView) findViewById(R.id.exName);
        if (code.equals("-1")) {//новая экскурсия, новые объекты
            exname.setText(MainActivity.exlist.get(pos).exname);
        } else {//старая экскурсия, новые объекты
            if (code.equals("-2")) {//пришли из PlayActivity
                exname.setText(MainActivity.exlist.get(pos).exname);
            } else { //пришли из MainActivity со старая экскурсия
                exname.setText(MainActivity.exlist.get(Integer.parseInt(code)).exname);
            }
        }
        list = (ListView) findViewById(R.id.objectslist1);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String str1 = "The number of photos you've chosen for your object is: " + MainActivity.exlist.get(pos).objects.get(position).obselectedphotos.size() + "\n";
                String str2 = "The number of videos you've chosen for your object is: " + MainActivity.exlist.get(pos).objects.get(position).obselectedvideos.size() + "\n";
                String str3 = "The number of audios you've chosen for your object is: " + MainActivity.exlist.get(pos).objects.get(position).obselectedaudios.size() + "\n";
                String str4 = "The geolocation you've chosen for your object is: " + MainActivity.exlist.get(pos).objects.get(position).oblatitude + " : " + MainActivity.exlist.get(pos).objects.get(position).oblongitude + "\n";
                String str5 = "The text you've written for your object is: " + MainActivity.exlist.get(pos).objects.get(position).obobjectText + "\n";
                String s = str1 + str2 + str3 + str4 + str5;
                AlertDialog.Builder adb = new AlertDialog.Builder(ObjectActivity.this);
                adb.setTitle(MainActivity.exlist.get(pos).objects.get(position).obname);
                adb.setMessage(s);
                adb.setPositiveButton("Ok", null);
                adb.show();
            }
        });
        SwipeListView l = new SwipeListView(this, this);
        l.exec();
        try {
            if (MainActivity.exlist.get(pos).objects.size() != 0) {
                m_Adapter = new MyAdapter();
                for (int i = 0; i < MainActivity.exlist.get(pos).objects.size(); i++) {
                    m_Adapter.addItem(MainActivity.exlist.get(pos).objects.get(i).obname);
                }
                list.setAdapter(m_Adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button del = (Button) findViewById(R.id.delobbut);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Tap on object you want to delete and then swipe from right to left", Toast.LENGTH_SHORT).show();
            }
        });
        propname = (EditText) findViewById(R.id.obName);
        Button new_ob = (Button) findViewById(R.id.newobbut);
        new_ob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Conn()) {
                    if (propname.getText().length() == 0)
                        Toast.makeText(getApplicationContext(), "Enter the object's name", Toast.LENGTH_SHORT).show();
                    else {
                        ExcursionObject eo = new ExcursionObject();
                        eo.obname = propname.getText().toString();
                        MainActivity.exlist.get(pos).objects.add(eo);
                        Intent intent = new Intent(ObjectActivity.this, PhotoActivity.class);
                        intent.putExtra("position", Integer.toString(pos));
                        intent.putExtra("obposition", Integer.toString(MainActivity.exlist.get(pos).objects.size() - 1));
                        startActivityForResult(intent, 22);
                        // set the animation to move once the button is clicked
                        overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
                    }
                }
            }
        });
        Button menu = (Button) findViewById(R.id.menubut);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                finishActivity(21);
                Intent intent = new Intent(ObjectActivity.this, MainActivity.class);
                startActivity(intent);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ob, menu);
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

    @Override
    public ListView getListView() {
        return list;
    }

    //свайп по элементу списка объектов
    @Override
    public void onSwipeItem(boolean isRight, int position) {
        m_Adapter.onSwipeItem(isRight, position);
    }

    //нажатие на элемент списка объектов
    @Override
    public void onItemClickListener(ListAdapter adapter, int position) {
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

    //класс, реализующий удаление элемента из списка по свайпу
    public class MyAdapter extends BaseAdapter {

        private final int invalid = -1;
        protected int delete_pos = -1;

        protected List<String> m_List;

        public MyAdapter() {
            m_List = new ArrayList<>();
        }

        public void addItem(String item) {
            m_List.add(item);
            notifyDataSetChanged();
        }

        public void onSwipeItem(boolean isRight, int position) {
            if (!isRight) {
                delete_pos = position;
            } else if (delete_pos == position) {
                delete_pos = invalid;
            }
            notifyDataSetChanged();
        }

        public void deleteItem(int posi) {
            m_List.remove(posi);
            MainActivity.exlist.get(pos).objects.remove(posi);
            delete_pos = invalid;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return m_List.size();
        }

        @Override
        public String getItem(int position) {
            return m_List.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ObjectActivity.this).inflate(R.layout.obitem, null);
            }
            TextView text = ViewHolderPattern.get(convertView, R.id.text);
            Button delete = ViewHolderPattern.get(convertView, R.id.delete);
            if (delete_pos == position) {
                delete.setVisibility(View.VISIBLE);
            } else
                delete.setVisibility(View.GONE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(position);
                }
            });
            text.setText(getItem(position));
            return convertView;
        }
    }

    //класс, свзанный с удалением элемента из списка из определенного holdera
    public static class ViewHolderPattern {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

    //анализатор нажатия на аппаратную кнопку назад
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            finishActivity(21);
            Intent intent = new Intent(ObjectActivity.this, MainActivity.class);
            startActivity(intent);
            // set the animation to move once the button is clicked
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        }
        return false;
    }
}

