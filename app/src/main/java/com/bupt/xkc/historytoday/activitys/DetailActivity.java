package com.bupt.xkc.historytoday.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.xkc.historytoday.R;
import com.bupt.xkc.historytoday.config.APIs;
import com.bupt.xkc.historytoday.models.HintMessage;
import com.bupt.xkc.historytoday.utils.HttpUtil;
import com.bupt.xkc.historytoday.utils.MyAsyncTask;
import com.bupt.xkc.historytoday.utils.SysApplicationManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.Bind;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class DetailActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private TextView summary_tv;
    private ImageView imageView;
    private TextView content_tv;

    private Intent intent;
    private String e_id;

    private Toolbar toolbar;
    private ImageView collapse_iv;
    private CollapsingToolbarLayout toolbarLayout;
    private AppBarLayout appBarLayout;
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        SysApplicationManager.getInstance().addActivity(this);

        initOperation();

        showDetail(e_id);

        doClick();

    }

    private void doClick() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("imageUrl", imageView.getTag().toString());
//                Log.i(LOG_TAG,"====>onClick...imageUrl="+imageView.getTag().toString());
                Intent intent = new Intent(DetailActivity.this,BigImageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(toolbarLayout.getHeight() + verticalOffset
                        < 2 * ViewCompat.getMinimumHeight(toolbarLayout)){
                    //toolbar折叠起来时，显示title为事件标题
                    toolbarLayout.setTitle(summary_tv.getText());
                }else {
                    toolbarLayout.setTitle("历史上的今天");
                }

            }
        });


    }

    private void showDetail(String e_id) {
        if (e_id != null && !e_id.equalsIgnoreCase("")) {
            Uri builtUri = Uri.parse(APIs.REQUEST_URL.QUERY_DETAIL).buildUpon()
                    .appendQueryParameter("key", APIs.REQUEST_URL.API_KEY)
                    .appendQueryParameter("e_id", e_id)
                    .build();

            MyAsyncTask task = new MyAsyncTask() {
                @Override
                protected void onPostExecute(Object o) {
                    String jsonString = (String) o;
                    if (jsonString != null && !jsonString.equalsIgnoreCase("")) {
                        HashMap<String, String> map =
                                HttpUtil.getEventDetailFromJson(jsonString);
                        fillDetailLayout(map);
                    }else {
                        summary_tv.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        content_tv.setText(HintMessage.NO_EVENT_DETAIL);
                        content_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);

                    }

                    super.onPostExecute(o);
                }
            };
            task.execute(builtUri);

        }
    }

    //填充布局中的各部分内容
    private void fillDetailLayout(HashMap<String,String> map){
        if (map != null) {
            if (map.containsKey("title")) {
                summary_tv.setVisibility(View.VISIBLE);
                summary_tv.setText(map.get("title"));
            }
            if (map.containsKey("url")) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setTag(map.get("url"));
                Picasso.with(DetailActivity.this)
                        .load(map.get("url"))
                        .into(imageView);

                Picasso.with(DetailActivity.this)
                        .load(map.get("url"))
                        .into(collapse_iv);
            }
            if (map.containsKey("content")) {
                content_tv.setText(map.get("content"));
            }

        } else {
            Log.e(LOG_TAG, "====>map is null!");
        }
    }

    private void initOperation() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        summary_tv = (TextView) findViewById(R.id.summary_detail);
        imageView = (ImageView) findViewById(R.id.image_detail);
        content_tv = (TextView) findViewById(R.id.content_detail);

        collapse_iv = (ImageView) findViewById(R.id.ivImage);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);


        intent = getIntent();
        if (intent.hasExtra("e_id")) {
            e_id = intent.getStringExtra("e_id");
        }

    }
}
