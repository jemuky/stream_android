package jymk.stream.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.UUID;

import jymk.stream.R;
import jymk.stream.tools.BaseTransformWS;
import jymk.stream.tools.Utils;
import jymk.stream.entity.TransformWSResult;

public class TransformTextWSActivity extends ComponentActivity {
    private final static String TAG = "TransformTextWSActivity";
    private BaseTransformWS mWS;
    EditText mSendView;
    TextView mRecvView;

    private static String mRoomId = UUID.randomUUID().toString();
    Message mToastMsg = new Message();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        setContentView(R.layout.transform_text);
        mSendView = findViewById(R.id.trans_text_send);
        mRecvView = findViewById(R.id.trans_text_recv);
        Button send = findViewById(R.id.trans_text_send_btn);

        mWS = new BaseTransformWS("room:" + mRoomId, (BaseTransformWS.TextListener) text -> {
            TransformWSResult res = new Gson().fromJson(text, TransformWSResult.class);
            if (!BaseTransformWS.SEND_MSG_EVENT.equals(res.event)) {
                Log.e(TAG, String.format("recv string=%s", text));

                switch (res.event) {
                    case "phx_reply":
                        mToastMsg.obj = "recv reply";
                        break;
                    case "phx_close":
                        mToastMsg.obj = "exit room";
                        break;
                }
                toastHandler.sendMessage(mToastMsg);
                return;
            }
            Message msg = new Message();
            msg.what = 2;
            msg.obj = getString(R.string.secondside) + res.payload;
            handler.sendMessage(msg);
        });
        send.setOnClickListener(v -> {
            if (!mWS.send(mSendView.getText().toString())) {
                Log.e(TAG, "send failed");
                Utils.t(TransformTextWSActivity.this, "send failed");
                return;
            }
            Message msg = new Message();
            msg.what = 1;
            msg.obj = getString(R.string.self) + mSendView.getText();
            handler.sendMessage(msg);
        });
    }

    @SuppressLint("SetTextI18n")
    private void setRecvView(String text) {
        mRecvView.setText(mRecvView.getText() + getString(R.string.newline) + text);
    }

    Handler toastHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Utils.t(TransformTextWSActivity.this, (String) msg.obj);
        }
    };

    // what: 1自己，2对方
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setRecvView((String) (msg.obj));
                    mSendView.setText("");
                    break;
                case 2:
                    setRecvView((String) (msg.obj));
                    break;
            }
        }
    };
}
