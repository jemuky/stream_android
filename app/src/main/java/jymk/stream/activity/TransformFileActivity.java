package jymk.stream.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jymk.stream.Constants;
import jymk.stream.R;
import jymk.stream.tools.BaseTransformHTTP;
import jymk.stream.tools.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class TransformFileActivity extends ComponentActivity {
    private final static String TAG = "TransformFileActivity";

    ActivityResultLauncher<String> mSelectLauncher;

    // 要请求的url
    String mReqUrl = Constants.TRANS_BASE_URL + Constants.TRANS_FILE_BASE_URL;
    // 当前选择文件uri
    List<Uri> mCurSelectFile = new ArrayList<>();
    // 当前选择文件展示
    TextView mSelectFileTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> uris) {
                if (uris.size() == 0) {
                    return;
                }
                Log.e(TAG, String.format("urls size=%d", uris.size()));
                for (Uri uri : uris) {
                    Log.e(TAG, String.format("uri=%s", uri.toString()));
                }

                mCurSelectFile.clear();
                mCurSelectFile.addAll(uris);
                mSelectFileTV.setVisibility(View.VISIBLE);
                mSelectFileTV.setText(uris.stream().map(Uri::getPath).collect(Collectors.joining("、")));
            }
        });

        init();
    }

    private void init() {
        setContentView(R.layout.transform_file);

        Button mSelectBtn = findViewById(R.id.trans_file_select_file);
        Button mSendBtn = findViewById(R.id.trans_file_send);
        mSelectFileTV = findViewById(R.id.trans_file_select_contents);

        mSendBtn.setOnClickListener(v -> {
            if (mCurSelectFile.size() == 0) {
                Utils.sendToastMsg(TransformFileActivity.this, "至少选择一个文件");
                return;
            }

            MultipartBody.Builder builder = new MultipartBody.Builder();
            for (Uri uri : mCurSelectFile) {
                File f = getFileFromUri(uri);
                builder.addFormDataPart("file", f.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), f));
            }
            MultipartBody multiBody = builder.build();

            BaseTransformHTTP.post(TransformFileActivity.this, mReqUrl, multiBody, 100);
        });
        mSelectBtn.setOnClickListener(v -> {
            mSelectLauncher.launch("*/*");
        });
    }

    // 从 URI 获取文件
    private File getFileFromUri(Uri uri) {
        ContentResolver resolver = getContentResolver();
        File file = null;
        try {
            // 打开输入流
            InputStream inputStream = resolver.openInputStream(uri);
            if (inputStream != null) {
                // 将输入流写入临时文件
                file = File.createTempFile("tmp_", null, getCacheDir());
                copyInputStreamToFile(inputStream, file);
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Error while creating temp file", e);
        }
        return file;
    }
    public void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

}
