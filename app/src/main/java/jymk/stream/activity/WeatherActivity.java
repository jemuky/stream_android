package jymk.stream.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jymk.stream.MyApplication;
import jymk.stream.entity.WeatherInfo;
import jymk.stream.receiver.TimeBroadCastReceiver;
import jymk.stream.R;
import jymk.stream.tools.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends ComponentActivity {
    private LinearLayout mFutureWeather;

    private TextView mWeatherProvince;
    private TextView mWeatherCity;
    private TextView mWeatherRegion;
    private TextView mWeatherNowDesc;
    private TextView mWeatherNowTemp;
    private TextView mWeatherNowWindDirect;
    private TextView mWeatherNowWindPower;
    private TextView mWeatherNowHumidity;
    private TextView mWeatherNowReportTime;
    // 选择区域
    private Spinner mArea;

    private String mCurAdcode = null;
    private String mCurRegion = null;

    private final static String TAG = "WeatherActivity";
    private final static String mSharedAreaKey = "area";
    public final static String mSharedAmapKey = "appkey";
    public final static String mSpWeather = "sp_weather";
    private SharedPreferences mSpAmap;

    Handler weatherInfoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            WeatherInfo info = null;
            try {
                info = new Gson().fromJson((String) msg.obj, WeatherInfo.class);
            } catch (Exception e) {
                Utils.t(WeatherActivity.this, String.format("gson parse weather info err, e=%s", e));
                WeatherActivity.this.finish();
                return;
            }
            if (!info.status.equals("1") || !info.infocode.equals("10000")) {
                Log.d(TAG, String.format("请求失败, status=%s, infocode=%s, info=%s", info.status, info.infocode, info.info));
                Utils.t(WeatherActivity.this, String.format("请求失败, status=%s, infocode=%s, info=%s", info.status, info.infocode, info.info));
                return;
            }

            switch (msg.what) {
                case 1:
                    WeatherInfo.Lives live = info.lives.get(0);
                    mWeatherProvince.setText(live.province);
                    mWeatherCity.setText(live.city);
                    mWeatherRegion.setText(mCurRegion);

                    mWeatherNowDesc.setText(live.weather);
                    mWeatherNowTemp.setText(live.temperature_float);
                    mWeatherNowWindDirect.setText(live.winddirection);
                    mWeatherNowWindPower.setText(live.windpower);
                    mWeatherNowHumidity.setText(live.humidity_float);
                    mWeatherNowReportTime.setText(live.reporttime);

                    Log.d(TAG, "send intent");
                    Intent intent = new Intent();
                    intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                    intent.putExtra("update_weather", true);
                    intent.putExtra("temp", live.temperature);
                    intent.putExtra("weather", live.weather);
                    intent.putExtra("region", live.province + live.city + mCurRegion);
                    sendBroadcast(intent);
                    break;
                case 2:
                    WeatherInfo.Forecasts forecast = info.forecasts.get(0);
                    mWeatherProvince.setText(forecast.province);
                    mWeatherCity.setText(forecast.city);
                    mWeatherRegion.setText(mCurRegion);

                    for (WeatherInfo.Casts cast : forecast.casts) {
                        LinearLayout ll = new LinearLayout(WeatherActivity.this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        ll.setLayoutParams(lp);
                        ll.setGravity(Gravity.CENTER_HORIZONTAL);
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.setPadding(2, 2, 2, 2);
                        // date
                        ll.addView(createTV(cast.date));
                        String thisWeek = null;
                        switch (cast.week) {
                            case "1":
                                thisWeek = "星期一";
                                break;
                            case "2":
                                thisWeek = "星期二";
                                break;
                            case "3":
                                thisWeek = "星期三";
                                break;
                            case "4":
                                thisWeek = "星期四";
                                break;
                            case "5":
                                thisWeek = "星期五";
                                break;
                            case "6":
                                thisWeek = "星期六";
                                break;
                            case "7":
                                thisWeek = "星期日";
                                break;
                        }
                        if (thisWeek != null) ll.addView(createTV(thisWeek));
                        ll.addView(createTV(String.format("白天: \n%s\n温度: %s ℃\n风向: %s\n风力: %s\n", cast.dayweather, cast.daytemp_float, cast.daywind, cast.daypower)));
                        ll.addView(createTV(String.format("夜晚: \n%s\n温度: %s ℃\n风向: %s\n风力: %s\n", cast.nightweather, cast.nighttemp_float, cast.nightwind, cast.nightpower)));

                        mFutureWeather.addView(ll);
                        mFutureWeather.addView(createVDivLine());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    // TextView
    TextView createTV(String text) {
        TextView tv = new TextView(WeatherActivity.this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tv.setText(text);
        return tv;
    }

    // 纵向分割线
    View createVDivLine() {
        View v = new View(WeatherActivity.this);
        v.setLayoutParams(new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.WRAP_CONTENT));
        v.setBackgroundColor(getResources().getColor(R.color.black, null));
        return v;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        // 注册系统时间广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(new TimeBroadCastReceiver(), filter);
    }

    enum ForecastType {
        Now,
        Future,
    }

    public void getWeather(ForecastType type, String adcode) {
        // 实况天气
        String ft = "base";
        // 预报天气
        if (type == ForecastType.Future) {
            ft = "all";
        }

        String reqUrl = String.format("https://restapi.amap.com/v3/weather/weatherInfo?key=%s&city=%s&extensions=%s&output=JSON", mSpAmap.getString(mSharedAmapKey, ""), adcode, ft);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(reqUrl).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, String.format("request fail, reqUrl=%s, e=%s", reqUrl, e.getMessage()));
                Utils.t(WeatherActivity.this, "request weather info fail");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String res = response.body().string();
                    Log.d(TAG, "doGetAsync: " + res);
                    Message msg = new Message();

                    if (type == ForecastType.Now) {
                        msg.what = 1;
                    } else if (type == ForecastType.Future) {
                        msg.what = 2;
                    }
                    msg.obj = res;
                    weatherInfoHandler.sendMessage(msg);
                }
            }
        });
    }

    void init() {
        setContentView(R.layout.weather);
        mFutureWeather = findViewById(R.id.weather_future_weather);

        mWeatherProvince = findViewById(R.id.weather_weather_province);
        mWeatherCity = findViewById(R.id.weather_weather_city);
        mWeatherRegion = findViewById(R.id.weather_weather_region);
        mWeatherNowDesc = findViewById(R.id.weather_weather_desc);
        mWeatherNowTemp = findViewById(R.id.weather_weather_temp);
        mWeatherNowWindDirect = findViewById(R.id.weather_weather_wind_direct);
        mWeatherNowWindPower = findViewById(R.id.weather_weather_wind_power);
        mWeatherNowHumidity = findViewById(R.id.weather_weather_humidity);
        mWeatherNowReportTime = findViewById(R.id.weather_weather_report_time);
        // 搜索框
        // 搜索框
        EditText mAreaSearch = findViewById(R.id.weather_area_search);
        // 选择区域
        mArea = findViewById(R.id.weather_area);

        // 绑定事件
        mAreaSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> options = new ArrayList<>();
                for (String key : MyApplication.CityCodeMapKeys) {
                    if (key.contains(s)) {
                        options.add(key);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(WeatherActivity.this, android.R.layout.simple_spinner_item, options);
                mArea.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.area_item, MyApplication.CityCodeMapKeys);
        mArea.setAdapter(adapter);
        mArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.setFocusable(true);
            }
        });
        mSpAmap = getSharedPreferences(mSpWeather, Context.MODE_PRIVATE);
        mArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> v, View view, int pos, long l) {
                String selected = (String) v.getItemAtPosition(pos);
                Log.d(TAG, String.format("选择%s", selected));
                String adcode = MyApplication.CityCodeMap.get(selected);
                if (adcode == null || adcode.length() == 0) {
                    Log.d(TAG, String.format("选中项无效, selected=%s", selected));
                    return;
                }
                SharedPreferences.Editor edit = mSpAmap.edit();
                edit.putString(mSharedAreaKey, adcode);
                edit.apply();
                mCurRegion = selected;
                mCurAdcode = adcode;

                // 选择后请求
                getWeather(ForecastType.Now, adcode);
                getWeather(ForecastType.Future, adcode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "取消选择");
            }
        });
        if (mCurAdcode == null) {
            // 默认北京市
            mCurAdcode = mSpAmap.getString(mSharedAreaKey, "110000");
            if (!mCurAdcode.equals("110000")) {
                for (Map.Entry<String, String> cc : MyApplication.CityCodeMap.entrySet()) {
                    if (cc.getValue().equals(mCurAdcode)) {
                        mCurRegion = cc.getKey();
                        break;
                    }
                }
            } else {
                mCurRegion = "北京市";
            }

            for (int i = 0; i < MyApplication.CityCodeMapKeys.size(); i++) {
                if (MyApplication.CityCodeMapKeys.get(i).equals(mCurRegion)) {
                    mArea.setSelection(i);
                    break;
                }
            }
        }
    }
}
