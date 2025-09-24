package jymk.stream.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jymk.stream.entity.TransformHTTPResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BaseTransformHTTP {
    private final static String TAG = "BaseTransformHTTP";

    public static void get(Context ctx, String url) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5000, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, String.format("request get fail, reqUrl=%s, e=%s", url, e.getMessage()));
                Utils.sendToastMsg(ctx, String.format("request get fail, url=%s, e=%s", url, e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Utils.sendToastMsg(ctx, "ping failed");
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    Utils.sendToastMsg(ctx, "ping failed");
                    return;
                }
                String s = body.string();
                Log.e(TAG, String.format("rsp=%s", s));
                Utils.sendToastMsg(ctx, s);
            }
        });
    }

    public static void post(Context ctx, String url, RequestBody body) {
        post(ctx, url, body, 5);
    }

    public static void post(Context ctx, String url, RequestBody body, long timeout) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(timeout, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().post(body).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, String.format("request post fail, reqUrl=%s, e=%s", url, e.getMessage()));
                Utils.sendToastMsg(ctx, String.format("request post fail, url=%s, e=%s", url, e.getMessage()));
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, String.format("request fail, reqUrl=%s, e=%d", url, response.code()));
                    Utils.sendToastMsg(ctx, String.format("request post suc, but code=%d, url=%s", response.code(), url));
                    return;
                }
                TransformHTTPResult res = new Gson().fromJson(response.body().string(), TransformHTTPResult.class);
                if (res.code == 0) {
                    Utils.sendToastMsg(ctx, "code suc, code=0");
                } else {
                    Utils.sendToastMsg(ctx, "code failed, code=" + res.code);
                }
            }
        });
    }
}
