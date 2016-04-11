package com.bupt.xkc.historytoday.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.xkc.historytoday.R;
import com.bupt.xkc.historytoday.adapters.EventListAdapter;
import com.bupt.xkc.historytoday.config.APIs;
import com.bupt.xkc.historytoday.models.HintMessage;
import com.bupt.xkc.historytoday.models.ListModel;
import com.bupt.xkc.historytoday.services.LoadEventListService;
import com.bupt.xkc.historytoday.utils.DBManager;
import com.bupt.xkc.historytoday.utils.HttpUtil;
import com.bupt.xkc.historytoday.utils.SysApplicationManager;
import com.bupt.xkc.historytoday.utils.TodayHelper;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.no_network_tv)
    TextView no_network_tv;
    @Bind(R.id.no_network_cv)
    CardView no_network_cv;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private LinearLayoutManager layoutManager;

    private DBManager dbManager;
    private Cursor cursor;
    private String todayDate;

    private EventListAdapter adapter;
    private ArrayList<ListModel> listModels;

    private EventListReceiver receiver;
    private long exit_time = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SysApplicationManager.getInstance().addActivity(this);


        initOperation();

        showList();

        doRefresh();


    }


    private void showList() {
        //停止刷新
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.destroyDrawingCache();
            refreshLayout.clearAnimation();
        }

        if (listModels != null) {
            listModels.clear();
        }
        listModels = getEventListData();
        adapter = new EventListAdapter(this, listModels);
        recyclerView.setAdapter(adapter);

        if (listModels == null || listModels.size() <= 0) {
            doBackgroundService();
        } else {
            recyclerView.setAdapter(adapter);
        }

        doClick();

    }

    private void doClick() {
        adapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ListModel event = listModels.get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("e_id", event.getE_id());
                intent.putExtra("title", event.getTitle());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO:报错/分享？
            }
        });


    }

    private void doRefresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showList();
            }
        });
    }

    private void initOperation() {
        dbManager = new DBManager(MainActivity.this);
        todayDate = getTodayDate();

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        listModels = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
    }

    private void doBackgroundService() {
//        Log.i(LOG_TAG, "====>doBackgroundService...");
        Intent intent = new Intent(this, LoadEventListService.class);
        intent.putExtra("today", todayDate);
        startService(intent);
    }

    private String getTodayDate() {
        int[] date = TodayHelper.getTodayDate();
        return date[1] + "/" + date[2];
    }

    private ArrayList<ListModel> getEventListData() {
//        Log.i(LOG_TAG, "====>getEventListData...");
        ArrayList<ListModel> listModels = new ArrayList<>();
        cursor = dbManager.queryEventList(todayDate);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String day = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String e_id = cursor.getString(cursor.getColumnIndexOrThrow("e_id"));

                ListModel event = new ListModel(e_id, day, date, title);
                listModels.add(event);

            } while (cursor.moveToNext());

        }

        return listModels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new EventListReceiver();
        IntentFilter filter = new IntentFilter(HintMessage.INTENT_FILTER_MAIN);
        registerReceiver(receiver, filter);

        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();

        MobclickAgent.onPause(this);
    }


    class EventListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (! intent.hasExtra("error")) {
                no_network_cv.setVisibility(View.GONE);
                showList();
            }else {
                if (intent.getStringExtra("error").equalsIgnoreCase(HintMessage.NO_NETWORK)){
                    no_network_cv.setVisibility(View.VISIBLE);
                    no_network_tv.setText(HintMessage.NO_NETWORK);


                }
            }
        }
    }


    /**
     * 检测返回键，连按两次返回近啊，退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exit_time > 2000) {
                Toast.makeText(MainActivity.this, HintMessage.READY_EXIT, Toast.LENGTH_LONG).show();
                exit_time = System.currentTimeMillis();

            } else {
                MobclickAgent.onKillProcess(this);
                SysApplicationManager.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        dbManager.close();
        super.onDestroy();
    }
}
