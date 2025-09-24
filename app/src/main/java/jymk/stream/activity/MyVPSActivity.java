package jymk.stream.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jymk.stream.Constants;
import jymk.stream.R;
import jymk.stream.entity.AuditLog;
import jymk.stream.tools.BWG;
import jymk.stream.tools.BaseTransformHTTP;
import jymk.stream.tools.Utils;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MyVPSActivity extends ComponentActivity {
    private final static String TAG = "MyVPSActivity";

    private Button mRestart, mStart, mStop, mStatus, mAuditLog;
    private TextView mResp;
    private ProgressBar mProgressBar;
    private BWG mBwg;
    public static final String mSpBwgConfKey = "sp_bwg";
    public static final String mSpBwgConfVeidKey = "veid";
    public static final String mSpBwgConfApiKeyKey = "api_key";
    private SharedPreferences mSpBwgConf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    void init() {
        setContentView(R.layout.my_vps);

        mBwg = new BWG();
        // init bwg conf
        mSpBwgConf = getSharedPreferences(mSpBwgConfKey, MODE_PRIVATE);
        mBwg.setVeid(mSpBwgConf.getString(mSpBwgConfVeidKey, "")).setApiKey(mSpBwgConf.getString(mSpBwgConfApiKeyKey, ""));

        mStart = findViewById(R.id.vps_start);
        mStop = findViewById(R.id.vps_stop);
        mRestart = findViewById(R.id.vps_restart);
        mStatus = findViewById(R.id.vps_status);
        mAuditLog = findViewById(R.id.vps_audit_log);

        mResp = findViewById(R.id.vps_resp);
        // 使TextView可滑动
        mResp.setMovementMethod(new ScrollingMovementMethod());
        mProgressBar = findViewById(R.id.vps_progress_bar);

        mRestart.setOnClickListener(v -> {
            showConfirmationDialog(3, "重启");
        });
        mStart.setOnClickListener(v -> {
            showConfirmationDialog(1, "启动");
        });
        mStop.setOnClickListener(v -> {
            showConfirmationDialog(2, "停止");
        });
        mStatus.setOnClickListener(v -> {
            showProcessBar();
            mBwg.getServiceInfo(MyVPSActivity.this, (suc, text) -> {
                Message msg = new Message();
                msg.what = 2;
                msg.obj = text;
                handler.sendMessage(msg);
            });
        });
        mAuditLog.setOnClickListener(v -> {
            showProcessBar();
            mBwg.getAuditLog(MyVPSActivity.this, (suc, text) -> {
                Message msg = new Message();
                msg.what = 3;
                if (!suc) {
                    msg.obj = text;
                } else {
                    try {
                        AuditLog auditLog = new Gson().fromJson(text, AuditLog.class);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT));
                        StringBuilder sb = new StringBuilder();
                        for (AuditLog.AuditLogEntry entry : auditLog.log_entries) {
                            sb.append(dateFormat.format(new Date(entry.timestamp * 1000)));
                            sb.append(": ");
                            sb.append(entry.summary);
                            sb.append('\n');
                        }
                        msg.obj = sb.toString();
                    } catch (Exception e) {
                        msg.obj = String.format("反序列化失败!\n%s\n原始数据:\n%s", e.getMessage(), text);
                    }

                }
                handler.sendMessage(msg);
            });
        });
    }

    // type:
    //   1 启动
    //   2 停止
    //   3 重启(看着好像无效)
    private void showConfirmationDialog(int type, String dialogMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(String.format("确认%s?", dialogMsg));

        String message = String.format("您确定要%svps吗", dialogMsg);
        builder.setMessage(message);

        // 添加确认按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProcessBar();
                switch (type) {
                    case 1:
                        mBwg.start(MyVPSActivity.this, (suc, text) -> {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = text;
                            handler.sendMessage(msg);
                        });
                        break;
                    case 2:
                        mBwg.stop(MyVPSActivity.this, (suc, text) -> {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = text;
                            handler.sendMessage(msg);
                        });
                        break;
                    case 3:
                        mBwg.restart(MyVPSActivity.this, (suc, text) -> {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = text;
                            handler.sendMessage(msg);
                        });
                        break;
                }
            }
        });

        // 添加取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showProcessBar() {
        Message msg = new Message();
        msg.what = 100;
        handler.sendMessage(msg);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1: // 重启vps
                case 2: // 获取服务状态
                case 3: // 获取audit日志
                    String text = (String) (msg.obj);
                    mResp.setText(text);
                    // 取消动画
                    mProgressBar.post(() -> mProgressBar.setVisibility(View.GONE));
                case 100:
                    mProgressBar.setVisibility(View.VISIBLE);
                default:
            }
        }
    };

}
