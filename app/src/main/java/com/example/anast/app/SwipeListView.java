package com.example.anast.app;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeListView {

    private ListView list;
    private int REL_SWIPE_MIN_DISTANCE;
    private int REL_SWIPE_MAX_OFF_PATH;
    private int REL_SWIPE_THRESHOLD_VELOCITY;
    private Context m_Context;
    private SwipeListViewCallback m_Callback;

    //свайп-интерфейс для листа объектов
    public interface SwipeListViewCallback {
        ListView getListView();

        void onSwipeItem(boolean isRight, int position);//свайп отдельного пункта списка

        void onItemClickListener(ListAdapter adapter, int position);//единичный клик
    }

    //вызываем инициализацию объектов
    public SwipeListView(Context mContext, SwipeListViewCallback callback) {
        if (callback == null) {
            try {
                throw new Exception("Activity must be implement SwipeListViewCallback");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        init(mContext, callback);
    }

    //вызываем инициализацию объектов
    public SwipeListView(Context mContext) throws Exception {
        if (!(mContext instanceof SwipeListViewCallback)) {
            throw new Exception("Activity must be implement SwipeListViewCallback");
        }
        init(mContext, (SwipeListViewCallback) mContext);
    }

    //инициализация
    protected void init(Context mContext, SwipeListViewCallback mCallback) {
        m_Context = mContext;
        m_Callback = mCallback;
    }

    //устанавливаем в качестве обработчика нажатий и свайпов наш созданный класс GestureDetector
    public void exec() {
        DisplayMetrics dm = m_Context.getResources().getDisplayMetrics();
        REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);
        list = m_Callback.getListView();
        @SuppressWarnings("deprecation")
        final GestureDetector gestureDetector = new GestureDetector(new MyGestureDetector());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        list.setOnTouchListener(gestureListener);
    }

    //нажатие на объект
    private void myOnItemClick(int position) {
        if (position < 0)
            return;
        m_Callback.onItemClickListener(list.getAdapter(), position);

    }

    //класс-обработчик нажатий и свайпов на объект
    class MyGestureDetector extends SimpleOnGestureListener {

        private int temp_position = -1;

        //единичное нажатие на объект
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int pos = list.pointToPosition((int) e.getX(), (int) e.getY());
            myOnItemClick(pos);
            return true;
        }

        //палец опущен на объект
        @Override
        public boolean onDown(MotionEvent e) {
            temp_position = list.pointToPosition((int) e.getX(), (int) e.getY());
            return super.onDown(e);
        }

        //произведен свайп по объекту
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
                return false;
            if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                int pos = list.pointToPosition((int) e1.getX(), (int) e2.getY());
                if (pos >= 0 && temp_position == pos)
                    m_Callback.onSwipeItem(false, pos);
            } else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                int pos = list.pointToPosition((int) e1.getX(), (int) e2.getY());
                if (pos >= 0 && temp_position == pos)
                    m_Callback.onSwipeItem(true, pos);
            }
            return false;
        }
    }
}