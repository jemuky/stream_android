package jymk.stream.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import jymk.stream.Constants;
import jymk.stream.R;
import jymk.stream.tools.BaseTransformHTTP;
import jymk.stream.tools.Utils;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class TransformTextActivity extends ComponentActivity {
    private final static String TAG = "TransformTextActivity";
    // 要请求的url
    String mReqUrl = Constants.TRANS_BASE_URL + Constants.TRANS_TEXT_BASE_URL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    void init(){
        setContentView(R.layout.transform_text);

        TextView mTextRecv = findViewById(R.id.trans_text_recv);
        TextView mTextSend = findViewById(R.id.trans_text_send);
        Button mTextSendBtn = findViewById(R.id.trans_text_send_btn);

        mTextRecv.setVisibility(View.GONE);

        mTextSendBtn.setOnClickListener(v -> {
            if (mTextSend.getText().toString().trim().length() == 0){
                Utils.sendToastMsg(TransformTextActivity.this, "未输入任何数据");
                return;
            }
            RequestBody reqBody = new FormBody.Builder().add("text", mTextSend.getText().toString()).build();
            BaseTransformHTTP.post(TransformTextActivity.this, mReqUrl, reqBody);
        });
    }

}
