package jymk.stream.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import jymk.stream.Constants;
import jymk.stream.R;
import jymk.stream.tools.BaseTransformHTTP;
import jymk.stream.tools.Utils;


public class MainActivity extends ComponentActivity {
    private SharedPreferences mSpBaseUrl, mSpBwgConf, mSpAmap;
    AutoCompleteTextView mBaseUrl, mVeid, mVpsApiKey, mAmapKey;
    TextView mBaseUrlDisplay, mVeidDisplay, mVpsApiKeyDisplay, mAmapDisplay;
    Button mClearBaseUrl, mClearVeid, mClearVpsApiKey, mClearAmapKey;
    Button mWeather, mLVDemo, mTransformTextWS, mTransformFile, mTransformText, mMyVps;

    private final static String TAG = "MainActivity";

    private static final String mSpBaseUrlKey = "sp_base_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        setContentView(R.layout.main);

        mWeather = findViewById(R.id.main_weather_text);
        mTransformTextWS = findViewById(R.id.main_transform_text_ws);
        mTransformText = findViewById(R.id.main_transform_text);
        mTransformFile = findViewById(R.id.main_transform_file);
        mMyVps = findViewById(R.id.main_my_vps);
        // trans base url
        mBaseUrl = findViewById(R.id.main_base_url_et);
        mBaseUrlDisplay = findViewById(R.id.main_base_url_display);
        mBaseUrlDisplay.setText(completeUrl(mBaseUrl.getText().toString()));
        mClearBaseUrl = findViewById(R.id.main_clear_base_url_btn);
        mSpBaseUrl = getSharedPreferences(mSpBaseUrlKey, MODE_PRIVATE);
        // weather
        mAmapKey = findViewById(R.id.main_amap_key_et);
        mAmapDisplay = findViewById(R.id.main_amap_key_display);
        mClearAmapKey = findViewById(R.id.main_clear_amap_key_btn);
        mSpAmap = getSharedPreferences(WeatherActivity.mSpWeather, MODE_PRIVATE);
        // bwg conf
        mVeid = findViewById(R.id.main_veid_et);
        mVpsApiKey = findViewById(R.id.main_api_key_et);
        mVeidDisplay = findViewById(R.id.main_veid_display);
        mVpsApiKeyDisplay = findViewById(R.id.main_vps_api_key_display);
        mClearVeid = findViewById(R.id.main_clear_veid_btn);
        mClearVpsApiKey = findViewById(R.id.main_clear_api_key_btn);
        mSpBwgConf = getSharedPreferences(MyVPSActivity.mSpBwgConfKey, MODE_PRIVATE);

        // demo
        mLVDemo = findViewById(R.id.main_lv_demo);

        // 输入框文本变化时
        setTextChange();
        // 初始化shared preference中内容
        initSp();
        // 注册清除按钮的方法
        clearText();
        // 注册点击事件
        clickEvent();
    }

    // 用shared preference初始化文本框数据
    void initSp() {
        mVeid.setText(mSpBwgConf.getString(MyVPSActivity.mSpBwgConfVeidKey, ""));
        mVpsApiKey.setText(mSpBwgConf.getString(MyVPSActivity.mSpBwgConfApiKeyKey, ""));
        mAmapKey.setText(mSpAmap.getString(WeatherActivity.mSharedAmapKey, ""));
        // baseurl历史记录
        mBaseUrl.setAdapter(getBaseUrlAdapter());
        if (mBaseUrl.getText().length() == 0) {
            mBaseUrlDisplay.setText(R.string.trans_base_url_hint);
        }
    }

    // 设置清理按钮事件
    void clearText() {
        mClearBaseUrl.setOnClickListener(v -> {
            mBaseUrl.setText("");
            mBaseUrlDisplay.setText(R.string.trans_base_url_hint);
            Constants.TRANS_BASE_URL = "http://localhost:4000/";
        });
        mClearVeid.setOnClickListener(v -> {
            mVeid.setText("");
            mVeidDisplay.setText(R.string.veid_prefix);
        });
        mClearVpsApiKey.setOnClickListener(v -> {
            mVpsApiKey.setText("");
            mVpsApiKeyDisplay.setText(R.string.vps_api_key_prefix);
        });
        mClearAmapKey.setOnClickListener(v -> {
            mAmapKey.setText("");
            mAmapDisplay.setText(R.string.weather_key_prefix);
        });
    }

    // 注册点击事件
    void clickEvent() {
        mLVDemo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LVDemoActivity.class);
            startActivity(intent);
        });
        mWeather.setOnClickListener(v -> {
            SharedPreferences.Editor sp = mSpAmap.edit();
            sp.putString(WeatherActivity.mSharedAmapKey, mAmapKey.getText().toString().trim());
            sp.apply();
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
        });
        mTransformTextWS.setOnClickListener(v -> {
            String baseUrl = mBaseUrl.getText().toString();
            baseUrl = completeUrl(baseUrl);
            if (!pingUrl(baseUrl)) {
                Utils.t(MainActivity.this, "init url failed, url=" + baseUrl);
                return;
            }
            addBaseUrlPref(baseUrl);
            Constants.TRANS_BASE_URL = baseUrl;
            Intent intent = new Intent(MainActivity.this, TransformTextWSActivity.class);
            startActivity(intent);
        });
        mTransformFile.setOnClickListener(v -> {
            String baseUrl = mBaseUrl.getText().toString();
            baseUrl = completeUrl(baseUrl);
            if (!pingUrl(baseUrl)) {
                Utils.t(MainActivity.this, "init url failed, url=" + baseUrl);
                return;
            }
            addBaseUrlPref(baseUrl);
            Constants.TRANS_BASE_URL = baseUrl;
            Intent intent = new Intent(MainActivity.this, TransformFileActivity.class);
            startActivity(intent);
        });
        mTransformText.setOnClickListener(v -> {
            String baseUrl = mBaseUrl.getText().toString();
            baseUrl = completeUrl(baseUrl);
            if (!pingUrl(baseUrl)) {
                Utils.t(MainActivity.this, "init url failed, url=" + baseUrl);
                return;
            }
            addBaseUrlPref(baseUrl);
            Constants.TRANS_BASE_URL = baseUrl;
            Intent intent = new Intent(MainActivity.this, TransformTextActivity.class);
            startActivity(intent);
        });

        mMyVps.setOnClickListener(v -> {
            SharedPreferences.Editor sp = mSpBwgConf.edit();
            sp.putString(MyVPSActivity.mSpBwgConfVeidKey, mVeid.getText().toString().trim());
            sp.putString(MyVPSActivity.mSpBwgConfApiKeyKey, mVpsApiKey.getText().toString().trim());
            sp.apply();
            Intent intent = new Intent(MainActivity.this, MyVPSActivity.class);
            startActivity(intent);
        });
    }

    // 设置文本框改变事件
    void setTextChange() {
        // 编辑url时更新url显示
        mBaseUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBaseUrlDisplay.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mVeid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mVeidDisplay.setText(String.format("%s%s", getResources().getText(R.string.veid_prefix), s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mVpsApiKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mVpsApiKeyDisplay.setText(String.format("%s%s", getResources().getText(R.string.vps_api_key_prefix), s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mAmapKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAmapDisplay.setText(String.format("%s%s", getResources().getText(R.string.weather_key_prefix), s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    String completeUrl(String url) {
        url = url.trim();
        if (url.length() == 0) return "http://localhost:4000/";
        if (url.startsWith("http") && url.endsWith("/")) return url;
        if (url.endsWith("/")) return "http://" + url;
        if (url.startsWith("http")) return url + "/";
        return "http://" + url + "/";
    }

    boolean pingUrl(String url) {
        if (Utils.isInvalidUrl(url)) {
            Log.e(TAG, "invalid base url, url=" + url);
            Utils.t(this, "invalid base url, url=" + url);
            return false;
        }
        BaseTransformHTTP.get(this, url + Constants.TRANS_PING);
//        return rsp != null;
        return true;
    }

    ArrayAdapter<String> getBaseUrlAdapter() {
        List<String> baseUrlSet = new ArrayList<>(mSpBaseUrl.getStringSet(mSpBaseUrlKey, new LinkedHashSet<>()));
        String[] baseUrlArr = new String[baseUrlSet.size()];
        mBaseUrl.setText(baseUrlSet.size() > 0 ? baseUrlSet.get(baseUrlSet.size() - 1) : "");
        for (int i = 0; i < baseUrlSet.size(); ++i) {
            baseUrlArr[i] = baseUrlSet.get(i);
        }

        return new ArrayAdapter<>(this, R.layout.trans_base_url_item, baseUrlArr);
    }

    void addBaseUrlPref(String url) {
        SharedPreferences.Editor spEdit = mSpBaseUrl.edit();
        List<String> baseUrlSet = new ArrayList<>(mSpBaseUrl.getStringSet(mSpBaseUrlKey, new LinkedHashSet<>()));
        if (baseUrlSet.size() >= 10) {
            baseUrlSet.remove(0);
        }
        baseUrlSet.add(url);
        spEdit.putStringSet(mSpBaseUrlKey, new HashSet<>(baseUrlSet));
        spEdit.apply();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return exit();
        }
        return super.onKeyDown(keyCode, event);
    }

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    private boolean exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            finish();
            System.exit(0);
            return true;
        }
    }
}
