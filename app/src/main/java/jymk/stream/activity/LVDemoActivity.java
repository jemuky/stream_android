package jymk.stream.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jymk.stream.R;
import jymk.stream.adapter.LVDemoAdapter;
import jymk.stream.entity.LVDemoItem;
import jymk.stream.listener.OnDataUpdateListener;
import jymk.stream.tools.Utils;

public class LVDemoActivity extends ComponentActivity implements OnDataUpdateListener {

    private ListView mLv;
    private TextView mPageTV;
    private Button mAdd, mNextPage, mLastPage;
    private int mCurPage = 1, mTotalPage = 0;
    private LVDemoAdapter mAdapter;
    List<LVDemoItem> mItems;

    // 每页数量
    private static final int sizeEveryPage = 5;
    private final static String TAG = "LVDemoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lv);

        init();

        // 初始化数据
        initData();
        mAdapter.setOnDataUpdateListener(this);

        // 更新数据
        update();
    }

    void init() {
        mLv = findViewById(R.id.lv_lv);
        mPageTV = findViewById(R.id.lv_page);
        mAdd = findViewById(R.id.lv_add);
        mNextPage = findViewById(R.id.lv_btn_next_page);
        mLastPage = findViewById(R.id.lv_btn_last_page);

        mNextPage.setOnClickListener(v -> {
            changePage(mCurPage + 1);
            initAdapter();
        });
        mLastPage.setOnClickListener(v -> {
            changePage(mCurPage - 1);
            initAdapter();
        });
    }

    // 初始化数据
    void initData() {
        mItems = new ArrayList<>();
        mItems.add(new LVDemoItem("111", "aaa"));
        mItems.add(new LVDemoItem("222", "bbb"));
        mItems.add(new LVDemoItem("333", "ccc"));
        mItems.add(new LVDemoItem("444", "ccc"));
        mItems.add(new LVDemoItem("555", "ccc"));
        mItems.add(new LVDemoItem("666", "ccc"));
        mItems.add(new LVDemoItem("777", "ccc"));
        mItems.add(new LVDemoItem("888", "ccc"));
        mItems.add(new LVDemoItem("999", "ccc"));
        mItems.add(new LVDemoItem("000", "ccc"));
        mItems.add(new LVDemoItem("123", "ccc"));

        changePage(1);
        initAdapter();
    }

    // 更新数据
    void update() {
        mAdd.setOnClickListener(v -> {
            // dialog，用于新增，更新数据
            final Dialog dialog = new Dialog(LVDemoActivity.this);
            dialog.setContentView(R.layout.lv_dialog);
            final EditText left = dialog.findViewById(R.id.lv_dl_left);
            final EditText right = dialog.findViewById(R.id.lv_dl_right);
            final Button ok = dialog.findViewById(R.id.lv_dl_ok);

            // dialog点击后数据增加到items中
            ok.setOnClickListener(vv -> {
                mItems.add(new LVDemoItem(left.getText().toString(), right.getText().toString()));
                changePage(mCurPage);
                initAdapter();

                dialog.dismiss();
            });

            dialog.show();
        });
    }

    void initAdapter() {
        int startIndex = (mCurPage - 1) * sizeEveryPage, endIndex = mCurPage * sizeEveryPage;
        if (mCurPage < 1) {
            startIndex = 0;
            endIndex = Math.min(mItems.size(), sizeEveryPage);
        }
        if (mCurPage >= mTotalPage) {
            startIndex = (mTotalPage - 1) * sizeEveryPage;
            endIndex = mItems.size();
        }
        mAdapter = new LVDemoAdapter(this, new ArrayList<>(mItems.subList(startIndex, endIndex)), mCurPage, sizeEveryPage);
        mLv.setAdapter(mAdapter);
    }

    void changePage(int page) {
        updateTotalPage();
        if (page > mTotalPage) {
            Utils.sendToastMsg(LVDemoActivity.this, "已经是最后一页了");
            return;
        }
        if (page < 1) {
            Utils.sendToastMsg(LVDemoActivity.this, "已经是第一页了");
            return;
        }

        mCurPage = page;

        updatePageText();
    }

    void updateTotalPage() {
        int tmpPage = mItems.size() / sizeEveryPage;
        if (mItems.size() % sizeEveryPage > 0) {
            mTotalPage = tmpPage + 1;
        } else {
            mTotalPage = tmpPage;
        }
    }

    void updatePageText() {
        mPageTV.setText(String.format(Locale.CHINA, "%d/%d 页", mCurPage, mTotalPage));
    }

    @Override
    public void onDataDeleted(int pos) {
        Log.e(TAG, "before curpage=" + mCurPage + ", totalpage=" + mTotalPage + ", pos=" + pos);
        mItems.remove((mCurPage - 1) * sizeEveryPage + pos);
//        changePage(mCurPage);
//        initAdapter();
        mAdapter.notifyDataSetChanged();
        updateTotalPage();
        updatePageText();
        Log.e(TAG, "after curpage=" + mCurPage + ", totalpage=" + mTotalPage);
    }
}
