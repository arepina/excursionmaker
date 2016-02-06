package com.example.anast.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.example.anast.app.SwipeListView.SwipeListViewCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeListViewCallback, API_Listener {

    private EditText propname;//имя нового объекта
    private ListView list;//объекты на экране
    private Button upload;//загрузка имеющихся объектов в dropbox
    private MyAdapter m_Adapter;//адаптер списка всех объектов на экране, чтобы была возможность их удалить
    public static List<Excursion> exlist;//все экскурсии
    //Переменные связанные с dropbox
    private DropboxAPI<AndroidAuthSession> mApi;//api dropbox extra
    private boolean mLoggedIn;//пользователь залогинился или нет
    private DropboxAPI dropboxApi;//api dropbox
    private boolean isUserLoggedIn;//пользователь залогинился или нет

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//горизонтальная ориентация экрана
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean q = Constants.FIRST_TIME_FLAG;
        if (!q) {
            if (exlist == null) {
                exlist = new ArrayList<>();
            }
            Constants.FIRST_TIME_FLAG = true;
        }
        propname = (EditText) findViewById(R.id.propname);
        list = (ListView) findViewById(R.id.objectslist);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                for (int i = 0; i < exlist.size(); i++) {
                    exlist.get(i).selected = false;
                }
                int index = 0;
                for (int i = 0; i < exlist.size(); i++) {
                    if (exlist.get(i).exname.equals(exlist.get(position).exname)) {
                        index = i;
                        break;
                    }
                }
                Intent intent = new Intent(MainActivity.this, ObjectActivity.class);
                intent.putExtra("mode", Integer.toString(position));
                intent.putExtra("position", Integer.toString(index));
                startActivityForResult(intent, 21);
                // set the animation to move once the button is clicked
                overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
            }
        });
        SwipeListView l = new SwipeListView(this, this);
        l.exec();
        try {
            if (exlist.size() != 0) {
                m_Adapter = new MyAdapter();
                for (int i = 0; i < exlist.size(); i++) {
                    m_Adapter.addItem(exlist.get(i).exname);
                }
                list.setAdapter(m_Adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button del = (Button) findViewById(R.id.delbut);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Tap on excursion you want to delete and then swipe from right to left", Toast.LENGTH_SHORT).show();
            }
        });
        Button new_ex = (Button) findViewById(R.id.in_button);
        Button haa = (Button) findViewById(R.id.haabut);
        new_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Conn()) {
                    Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                } else {
                    Excursion ex = new Excursion();
                    if (propname.getText().length() == 0)
                        Toast.makeText(getApplicationContext(), "Enter the excursion's name", Toast.LENGTH_SHORT).show();
                    else {
                        boolean flag = false;
                        ex.exname = propname.getText().toString();
                        for (int i = 0; i < exlist.size(); i++) {
                            if (exlist.get(i).exname.equals(ex.exname)) {
                                Toast.makeText(getApplicationContext(), "You already have an excursion with this name", Toast.LENGTH_SHORT).show();
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            for (int i = 0; i < exlist.size(); i++) {
                                exlist.get(i).selected = false;
                            }
                            exlist.add(ex);
                            Intent intent = new Intent(MainActivity.this, ObjectActivity.class);
                            intent.putExtra("mode", "-1");
                            intent.putExtra("position", Integer.toString(exlist.size() - 1));
                            startActivityForResult(intent, 21);
                            // set the animation to move once the button is clicked
                            overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
                        }
                    }
                }
            }
        });
        haa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Conn()) {
                    Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < exlist.size(); i++) {
                        exlist.get(i).selected = false;
                    }
                    Intent intent = new Intent(MainActivity.this, HAA.class);
                    startActivityForResult(intent, 10);
                    // set the animation to move once the button is clicked
                    overridePendingTransition(R.anim.slide_forw, R.anim.slide_back);
                }
            }
        });
        Button logindropbox = (Button) findViewById(R.id.conuploadbut);
        logindropbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserLoggedIn) {
                    dropboxApi.getSession().unlink();
                    loggedIn(false);
                    Toast.makeText(MainActivity.this, "You have just logged out from the Dropbox", Toast.LENGTH_LONG).show();
                } else {
                    ((AndroidAuthSession) dropboxApi.getSession()).startAuthentication(MainActivity.this);
                }
            }
        });
        upload = (Button) findViewById(R.id.uploadbut);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLoggedIn)
                    Toast.makeText(MainActivity.this, "This app wasn't authenticated properly. Please link with the Dropbox to upload your excursions", Toast.LENGTH_LONG).show();
                if (mLoggedIn) {
                    if (!Conn()) {
                        Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean sel = false;
                        boolean size_ob = false;
                        for (int i = 0; i < exlist.size(); i++) {
                            if (exlist.get(i).selected) {//экскурсия выбрана для загрузки
                                sel = true;
                                if (exlist.get(i).objects.size() == 0)//но объектов нет
                                {
                                    size_ob = true;
                                }
                            }
                        }
                        if (!sel)
                            Toast.makeText(MainActivity.this, "You should choose at least one excursion to upload", Toast.LENGTH_LONG).show();
                        else {
                           /* if (size_ob) {
                                Toast.makeText(MainActivity.this, "All the excursions you want to upload must have at least 1 object", Toast.LENGTH_LONG).show();
                            } else {*/
                                Upload upload = new Upload(Constants.UPLOAD_CODE, MainActivity.this, dropboxApi, mApi, DropboxData.DROPBOX_FILE_DIR);
                                upload.execute();
                            //}
                        }
                    }
                }
            }
        });
        loggedIn(false);
        AppKeyPair appKeyPair = new AppKeyPair(DropboxData.ACCESS_KEY, DropboxData.ACCESS_SECRET);
        AndroidAuthSession session;
        SharedPreferences prefs = getSharedPreferences(DropboxData.DROPBOX_NAME, 0);
        String key = prefs.getString(DropboxData.ACCESS_KEY, null);
        String secret = prefs.getString(DropboxData.ACCESS_SECRET, null);
        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(appKeyPair, DropboxData.ACCESS_TYPE, token);
        } else {
            session = new AndroidAuthSession(appKeyPair, DropboxData.ACCESS_TYPE);
        }
        mApi = new DropboxAPI<>(session);
        dropboxApi = new DropboxAPI(session);
    }

    //logging in to dropbox
    public void loggedIn(boolean userLoggedIn) {
        mLoggedIn = userLoggedIn;
        isUserLoggedIn = userLoggedIn;
    }

    //файлы успешно загружены
    @Override
    public void onSuccess(int requestnumber, Object obj) {
        Toast.makeText(MainActivity.this, "All excursions were successfully uploaded", Toast.LENGTH_LONG).show();
    }

    //в процессе загрузки были ошибки
    @Override
    public void onFail(String errormessage) {
        Toast.makeText(MainActivity.this, "Sorry, we have some problems:(", Toast.LENGTH_LONG).show();
    }

    //при повторном запуске не требуется заново логиниться, т.к данные сохранены в shared prefs
    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = (AndroidAuthSession) dropboxApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DropboxData.DROPBOX_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(DropboxData.ACCESS_KEY, tokens.key);
                editor.putString(DropboxData.ACCESS_SECRET, tokens.secret);
                editor.commit();
                loggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox auth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //проверка наличия соединения с интернетом
    public boolean Conn() {
        //Создаем пример класса connection detector:
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        //Получаем статус Интернет
        Boolean isInternetPresent = cd.ConnectingToInternet();
        //Проверяем Интернет статус:
        if (isInternetPresent) {
            return true;
        } else {
            //Интернет соединения нет просим пользователя подключить Интернет:
            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        public void deleteItem(int pos) {
            m_List.remove(pos);
            exlist.remove(pos);
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
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, null);
            }
            TextView text = ViewHolderPattern.get(convertView, R.id.text);
            Button delete = ViewHolderPattern.get(convertView, R.id.delete);
            CheckBox check = ViewHolderPattern.get(convertView, R.id.checkBox11);
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
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exlist.get(position).selected = !exlist.get(position).selected;
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
            for (int i = 0; i < exlist.size(); i++)
                exlist.get(i).selected = false;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Are you sure you want to exit the ExcurionMaker?");
            alertDialogBuilder.setMessage("We highly recommend to upload all your excursions to DropBox. Otherwise you will lost all your data");
            alertDialogBuilder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {// set positive button: Yes message
                public void onClick(DialogInterface dialog, int id) {
                    finishAffinity();
                }
            });
            alertDialogBuilder.setNegativeButton("No, let me save my data", new DialogInterface.OnClickListener() {// set negative button: No message
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        return false;
    }
}

