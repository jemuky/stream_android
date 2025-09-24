package jymk.stream.tools;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 搬瓦工服务器工具类
 **/
public class BWG {
    static String mBaseUrl = "https://api.64clouds.com/v1/{{cmd}}?veid={{veid}}&api_key={{api_key}}";
    static String TAG = "BWG";
    static String mReplaceCmd = "{{cmd}}";
    static String mReplaceVeid = "{{veid}}";
    static String mReplaceApiKey = "{{api_key}}";

    String veid = "";
    String api_key = "";

    public BWG() {
    }

    public BWG setVeid(String veid) {
        this.veid = veid;
        return this;
    }

    public BWG setApiKey(String apiKey) {
        this.api_key = apiKey;
        return this;
    }

    // 获取服务信息状态
    public void getServiceInfo(Context ctx, TextListener listener) {
        request(ctx, "getLiveServiceInfo", listener);
    }

    // 启动vps
    public void start(Context ctx, TextListener listener) {
        request(ctx, "start", listener);
    }

    // 停止vps
    public void stop(Context ctx, TextListener listener) {
        request(ctx, "stop", listener);
    }

    // 重启vps
    public void restart(Context ctx, TextListener listener) {
        request(ctx, "restart", listener);
    }

    // 获取审计日志
    public void getAuditLog(Context ctx, TextListener listener) {
        request(ctx, "getAuditLog", listener);
    }

    // 请求
    void request(Context ctx, String cmd, TextListener listener) {
        String url = mBaseUrl.replace(mReplaceCmd, cmd).replace(mReplaceVeid, veid).replace(mReplaceApiKey, api_key);

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, String.format("request bwg fail, service info e=%s", e.getMessage()));
                Utils.sendToastMsg(ctx, String.format("request bwg fail, service info, e=%s", e.getMessage()));
                listener.onMessage(false, "请求失败!\n" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Utils.sendToastMsg(ctx, "response failed, not success");
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    Utils.sendToastMsg(ctx, "response failed, body is null");
                    return;
                }
                String s = body.string();
                listener.onMessage(true, s);
            }
        });
    }

    public interface TextListener {
        void onMessage(boolean suc, @NonNull String text);
    }
}
