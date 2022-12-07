package com.example.ljl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.ljd.retrofit.R;
import com.zk.tspmanager.api.DownloadApi;
import com.zk.tspmanager.datastore.DownloadDataStore;
import com.zk.tspmanager.progress.DownloadProgressHandler;
import com.zk.tspmanager.progress.ProgressHandler;
import com.zk.tspmanager.retrofit.RetrofitManger;
import com.zk.tspmanager.scope.ScopeName;
import com.zk.tspmanager.scope.ScopeTools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import kotlin.coroutines.Continuation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadActivity extends Activity {

    private static final String TAG = "DownloadActivity";
    private static final String TEST_BASE_URL = "http://msoftdl.360.cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

//        TextView textView = findViewById(R.id.mmap_text);
//        MMKV mmkv = MMKV.mmkvWithID("SidebarAppList", MMKV.MULTI_PROCESS_MODE);
//        //测试mmap数据共享
//        findViewById(R.id.get_mmap_data).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtil.dlog(TAG, "get_mmap_data onClick");
//                //写入数据
////                mmkv.putString("test", "testValue");
//                String test = mmkv.decodeString("test", "");
//                LogUtil.dlog(TAG, "decodeString: " + test);
//                textView.setText(test);
//            }
//        });

        //测试下载流程
        findViewById(R.id.download_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(DownloadActivity.this);
                dialog.setProgressNumberFormat("%1d KB/%2d KB");
                dialog.setTitle("下载");
                dialog.setMessage("正在下载，请稍后...");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.show();

                ProgressHandler progressHandler = new DownloadProgressHandler() {

                    @Override
                    protected void onProgress(long progress, long total, boolean done) {
                        Log.e("是否在主线程中运行", String.valueOf(Looper.getMainLooper() == Looper.myLooper()));
                        Log.e("onProgress", String.format("%d%% done\n", (100 * progress) / total));
                        Log.e("done", "--->" + String.valueOf(done));
                        dialog.setMax((int) (total / 1024));
                        dialog.setProgress((int) (progress / 1024));

                        if (done) {
                            dialog.dismiss();
                        }
                    }
                };
                //下载进度
                DownloadDataStore.INSTANCE.init(DownloadActivity.this);
//                DownloadApi downloadApi = RetrofitManger.getApiServiceWithBaseUrl(TEST_BASE_URL, progressHandler, DownloadApi.class);
//                Call<ResponseBody> call = downloadApi.getDownloadFileTest("360MobileSafe_6.2.3.1060.apk");

                DownloadApi downloadApi = RetrofitManger.getApiServiceWithBaseUrl(TEST_BASE_URL, progressHandler, DownloadApi.class);

                File file = new File(Environment.getExternalStorageDirectory(), "12345.apk");
                String range = "";
                if (file.exists() && file.isFile()) {
                    long totalInData = DownloadDataStore.INSTANCE.getTotal();
                    boolean isDone = DownloadDataStore.INSTANCE.getIsDone();
                    String totalLength = "-";
                    totalLength += totalInData;
                    long progress = DownloadDataStore.INSTANCE.getProgress();
                    LogUtil.ilog(TAG, "progress: " + progress + " totalInData: " + totalInData + " file.length(): " + file.length() + " isDone: " + isDone);
                    if (progress > totalInData) {
                        LogUtil.ilog(TAG, "file error, delete ");
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                        DownloadDataStore.INSTANCE.updateProgressNow(0).updateTotalAsync(0).updateIsDoneAsync(false);
                        isDone = false;
                        progress = 0;
                    }
                    if (progress == totalInData || isDone) {
                        LogUtil.ilog(TAG, "download complete, return");
                        dialog.dismiss();
                        Toast.makeText(DownloadActivity.this, "下载完了", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    range = "bytes=" + Long.toString(progress) + totalLength;
                } else {
                    LogUtil.ilog(TAG, "progress clear ");
                    DownloadDataStore.INSTANCE.updateProgressNow(0).updateTotalAsync(0).updateIsDoneAsync(false);
                }

                Call<ResponseBody> call = downloadApi.getDownloadFileUrlTest(range, TEST_BASE_URL + "/mobilesafe/shouji360/360safesis/360MobileSafe_6.2.3.1060.apk");
                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            if (response.body() == null) {
                                return;
                            }
                            //异步线程
//                            DownloadTask downloadTask = new DownloadTask();
//                            downloadTask.execute(response);

                            //协程
                            String scopeName = ScopeName.SAVE_ZIP_FILE.getScopeName();
                            ScopeTools.INSTANCE.executeIO(scopeName, new ScopeTools.IExecutor() {
                                @Override
                                public Object doInScope(@NonNull Continuation<? super Boolean> $completion) {
                                    return saveUpdateAccessFile(response);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_PERMISSION_STORAGE = 100;
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
                    return;
                }
            }
        }

    }

    /**
     * AsyncTask实现原理其实就是线程池
     * 修改这个异步线程为Executor，ThreadPoolExecutor，FutureTask区别不大
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<Response<ResponseBody>, Void, Void> {

        @SafeVarargs
        @SuppressLint("RestrictedApi")
        @Override
        protected final Void doInBackground(Response<ResponseBody>... responses) {
            LogUtil.dlog("DownloadTask", "doInBackground()");
            Response<ResponseBody> response = null;
            if (responses != null && responses.length > 0) {
                response = responses[0];
            } else {
                return null;
            }
            saveUpdateFile(response);
            return null;
        }
    }

    /**
     * 保存文件，不支持断点续传
     *
     * @param response Response
     * @return isDone
     */
    public boolean saveUpdateFile(Response<ResponseBody> response) {
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        try {
            assert response.body() != null;
            is = response.body().byteStream();
            LogUtil.dlog("saveUpdateFile", "getExternalStorageDirectory(): " + Environment.getExternalStorageDirectory());
            File file = new File(Environment.getExternalStorageDirectory(), "12345.apk");
            if (file.exists() && file.isFile()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            fos = new FileOutputStream(file);
            bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                assert fos != null;
                assert bis != null;
                fos.close();
                bis.close();
                is.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存文件，支持断点续传
     *
     * @param response Response
     * @return isDone
     */
    public boolean saveUpdateAccessFile(Response<ResponseBody> response) {
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        long range = 0;
        long totalDownload = 0;
        long responseLength = 0;

//        FileOutputStream fos = null;
//        BufferedInputStream bis = null;
        try {
            assert response.body() != null;
            inputStream = response.body().byteStream();
            responseLength = response.body().contentLength();
            range = DownloadDataStore.INSTANCE.getProgress();
            totalDownload = range;
            //save total to DataStore
            DownloadDataStore.INSTANCE.updateTotalAsync(responseLength);
            LogUtil.ilog(TAG, "range: " + range + " responseLength: " + responseLength);

            LogUtil.dlog(TAG, "saveUpdateAccessFile getExternalStorageDirectory(): " + Environment.getExternalStorageDirectory());
            File file = new File(Environment.getExternalStorageDirectory(), "12345.apk");
            //断点续传
            randomAccessFile = new RandomAccessFile(file, "rwd");
            if (range == 0) {
                randomAccessFile.setLength(responseLength);
            }
            randomAccessFile.seek(range);
            byte[] buffer = new byte[1024 * 1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, len);
                totalDownload += len;
                DownloadDataStore.INSTANCE.updateProgressAsync(totalDownload);
                LogUtil.ilog(TAG, "updateProgressAsync: " + totalDownload);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
//                DownloadDataStore.INSTANCE.updateProgressAsync(totalDownload);
                if (totalDownload < responseLength) {
                    LogUtil.ilog(TAG, "finally updateProgress ");
                } else {
                    LogUtil.ilog(TAG, "finally done ");
                    DownloadDataStore.INSTANCE.updateIsDoneAsync(true);
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.ilog(TAG, "onPause cancelIO SAVE_ZIP_FILE ");
        String scopeName = ScopeName.SAVE_ZIP_FILE.getScopeName();
        ScopeTools.INSTANCE.cancelIO(scopeName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
