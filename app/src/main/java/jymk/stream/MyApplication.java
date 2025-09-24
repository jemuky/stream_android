package jymk.stream;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jymk.stream.entity.CityCode;
import jymk.stream.tools.Utils;

public class MyApplication extends Application {
    public static HashMap<String, String> CityCodeMap = new HashMap<>();
    public static List<String> CityCodeMapKeys = null;
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        initWeather();
        initBaseUrl();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    void initBaseUrl(){
        Constants.TRANS_BASE_URL = "http://localhost:4000/";
    }
    void initWeather() {
        // 读取区域
        byte[] buffer = null;
        Log.d(TAG, "start openRawRes");
        try (InputStream is = getResources().openRawResource(R.raw.city_code)) {
            buffer = new byte[is.available()];
            is.read(buffer);
        } catch (IOException e) {
            Log.e(TAG, String.format("openRawResource failed, e=%s", e));
            Utils.t(this, "read raw failed");
            return;
        }
        String data = new String(buffer);
//        Log.d(TAG, String.format("rawdata=%s", data));

//        Log.d(TAG, String.format("city_code=%s", data));
        // 缓存区域
        CityCode obj = null;
        try {
            obj = new Gson().fromJson(data, CityCode.class);
        } catch (Exception e) {
            Utils.t(this, String.format(Locale.CHINA, "fromjson failed, err=%s", e.getMessage()));
            this.onTerminate();
            return;
        }

        for (CityCode.CitySheet sheet : obj.Sheet1) {
            CityCodeMap.put(sheet.中文名, sheet.adcode);
        }
        CityCodeMapKeys = new ArrayList<>(MyApplication.CityCodeMap.keySet());
    }
}
