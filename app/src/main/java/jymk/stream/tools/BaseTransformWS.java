package jymk.stream.tools;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import jymk.stream.Constants;
import jymk.stream.entity.TransformText;
import jymk.stream.entity.WSStruct;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class BaseTransformWS {
    private final static String TAG = "BaseTransformWS";
    public final static String SEND_MSG_EVENT = "new_msg";
    private static final String WS_URL = Constants.TRANS_BASE_URL.replace("http", "ws").replace("https", "wss") + Constants.TRANS_TEXT_WS_BASE_URL;
    private WebSocket mWS;

    private TextListener mTextListener = null;
    private BinaryListener mBinListener = null;
    public String mTopic;

    public BaseTransformWS(String topic, TextListener tl) {
        this.mTopic = topic;
        this.mTextListener = tl;
        init();
    }
    public BaseTransformWS(String topic, BinaryListener bl) {
        this.mTopic = topic;
        this.mBinListener = bl;
        init();
    }

    public boolean send(String text){
        return mWS.send(new Gson().toJson(new WSStruct<>("new_msg", mTopic, new TransformText(text))));
    }

    private void init() {
        connPhoenixWS();
    }

    private void connPhoenixWS() {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url(WS_URL).build();
        PhoenixWSListener listener = new PhoenixWSListener();
        mWS = client.newWebSocket(request, listener);
        Log.e(TAG, "start link");
    }

    private class PhoenixWSListener extends WebSocketListener {
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            // 连接成功
            boolean suc = mWS.send(new Gson().toJson(new WSStruct<>("phx_join", mTopic, new TransformText("进入房间"))));
            Log.e(TAG, String.format("link suc, send suc?=%b", suc));
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            // 接收到字节消息
            Log.e(TAG, "recv byte");
            if (mBinListener == null){
                Log.e(TAG, "invalid init");
                return;
            }
            mBinListener.onMessage(bytes);
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            // 接收到文本消息
            if (mTextListener == null){
                Log.e(TAG, "invalid init");
                return;
            }
            mTextListener.onMessage(text);
        }

        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosed(webSocket, code, reason);
            // 连接关闭
            boolean suc = mWS.send(new Gson().toJson(new WSStruct<>("pnx_leave", "room:lobby", new TransformText("离开房间"))));
            Log.e(TAG, String.format("link close, send suc?=%b", suc));
            mWS.cancel();
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            // 连接失败
            if (response == null) {
                Log.e(TAG, String.format("link failed, msg=%s", t.getMessage()));
                return;
            }
            Log.e(TAG, String.format("link failed, msg=%s, code=%d, body=%s", t.getMessage(), response.code(), response.body().toString()));
        }
    }

    public interface BinaryListener {
        void onMessage(@NonNull ByteString bytes);
    }

    public interface TextListener {
        void onMessage(@NonNull String text);
    }
}
