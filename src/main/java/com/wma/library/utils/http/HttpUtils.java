package com.wma.library.utils.http;

import android.text.TextUtils;

import com.wma.library.log.Logger;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.Map;

/**
 * create by wma
 * on 2020/10/23 0023
 */
public class HttpUtils {
    final String TAG = HttpUtils.class.getSimpleName();


    public Callback.Cancelable request(final Request request, final HttpCallbackListener callback) {
        if (request == null) {
            Logger.e(TAG, "request: 请求对象为空");
            return null;
        }
        HttpMethod httpMethod = request.getRequestType();
        if (httpMethod == null) {
            Logger.e(TAG, "request: 请求对方式不对");
            return null;
        }
        String url = request.getUrl();
        if (TextUtils.isEmpty(url)) {
            Logger.e(TAG, "request: 请求地址为空");
            return null;
        }
        RequestParams entity = new RequestParams(url);
        entity.setConnectTimeout(10000);
        Map<String, String> parameters = request.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (String key : parameters.keySet()) {
                entity.addBodyParameter(key, parameters.get(key));
            }
        }
        Callback.CommonCallback xUtilsCallBack = new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (callback != null) {
                    callback.onSuccess(result, request);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) {
                    callback.onError(ex, isOnCallback);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (callback != null) {
                    callback.onCancelled(cex);
                }
            }

            @Override
            public void onFinished() {
                if (callback != null) {
                    callback.onFinished();
                }
            }
        };
        return x.http().request(httpMethod, entity, xUtilsCallBack);
    }



    public Callback.Cancelable uploadFile(final Request request, final HttpProgressListener callback) {
        if (request == null) {
            Logger.e(TAG, "request: 请求对象为空");
            return null;
        }
        HttpMethod httpMethod = request.getRequestType();
        if (httpMethod == null) {
            Logger.e(TAG, "request: 请求对方式不对");
            return null;
        }
        String url = request.getUrl();
        if (TextUtils.isEmpty(url)) {
            Logger.e(TAG, "request: 请求地址为空");
            return null;
        }
        RequestParams entity = new RequestParams(url);
        entity.setAutoResume(true);// 置是否在下载是自动断点续传
        entity.setAutoRename(true);
        entity.setConnectTimeout(60000);
        entity.setMultipart(true);
        Map<String, String> parameters = request.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (String key : parameters.keySet()) {
                entity.addBodyParameter(key, parameters.get(key));
            }
        }
        File file = request.getFile();
        entity.addBodyParameter("file", file);
        Callback.ProgressCallback xUtilsCallback = new Callback.ProgressCallback<String>() {
            @Override
            public void onWaiting() {
                if (callback != null) {
                    callback.onWaiting();
                }
            }

            @Override
            public void onStarted() {
                if (callback != null) {
                    callback.onStarted();
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (callback != null) {
                    callback.onLoading(total, current, isDownloading);
                }
            }

            @Override
            public void onSuccess(String result) {
                if (callback != null) {
                    callback.onSuccess(result, request);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) {
                    callback.onError(ex, isOnCallback);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (callback != null) {
                    callback.onCancelled(cex);
                }
            }

            @Override
            public void onFinished() {
                if (callback != null) {
                    callback.onFinished();
                }
            }
        };
        return x.http().request(httpMethod, entity, xUtilsCallback);
    }
}
