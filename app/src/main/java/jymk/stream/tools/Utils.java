package jymk.stream.tools;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

import jymk.stream.activity.TransformTextActivity;

public class Utils {
    public static void t(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static void sendToastMsg(Context ctx, String text) {
        Message mToastMsg = new Message();
        mToastMsg.obj = text;
        Handler mToastHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                Utils.t(ctx, (String) msg.obj);
            }
        };
        mToastHandler.sendMessage(mToastMsg);
    }

    public static boolean isInvalidUrl(String url) {
        String regex = "^(http|https)://[a-zA-Z0-9.\\\\-]+(:\\d+)?/";
        return !Pattern.matches(regex, url);
    }


}
