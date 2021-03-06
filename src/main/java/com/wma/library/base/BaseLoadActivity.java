package com.wma.library.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.wma.library.log.Logger;
import com.wma.library.utils.http.HttpCallbackListener;
import com.wma.library.utils.http.Request;
import com.wma.library.utils.json.JsonParserUtils;
import com.wma.library.utils.json.ResultHandleListener;

import org.xutils.common.Callback;

import java.util.List;

/**
 * create by wma
 * on 2020/10/23 0023
 */
public abstract class BaseLoadActivity<T extends BaseModule, B extends ViewDataBinding> extends BaseActivity<B> implements HttpCallbackListener, OnRefreshListener, OnLoadMoreListener, ResultHandleListener<T> {
    private SmartRefreshLayout mSmartRefreshLayout;

    @Override
    public void init(Bundle savedInstanceState) {
        mSmartRefreshLayout = getSmartRefreshLayout();
        setEnableRefresh(enableRefresh());
        setEnableLoadMore(enableLoadMore());
        if (isAutoRefresh()) {
            autoRefresh();
        }

    }


    protected abstract void loadData();

    public void autoRefresh() {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.autoRefresh();
        }
    }

    /**
     * 是否可以刷新
     *
     * @return
     */
    public void setEnableRefresh(boolean refresh) {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.setEnableRefresh(refresh);
            if (refresh) {
                mSmartRefreshLayout.setRefreshHeader(getRefreshHeader());
                mSmartRefreshLayout.setOnRefreshListener(this);
            }
        }
    }

    /**
     * 是否可以加载更多
     *
     * @return
     */
    public void setEnableLoadMore(boolean loadMore) {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.setEnableLoadMore(loadMore);
            if (loadMore) {
                mSmartRefreshLayout.setRefreshFooter(getRefreshFooter());
                mSmartRefreshLayout.setOnLoadMoreListener(this);
            }
        }
    }


    /**
     * 是否自动刷新
     *
     * @return
     */
    public boolean isAutoRefresh() {
        return true;
    }

    /**
     * 是否可以刷新
     *
     * @return
     */
    protected boolean enableRefresh() {
        return true;
    }

    /**
     * 是否可以加载更多
     *
     * @return
     */
    protected boolean enableLoadMore() {
        return true;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        loadData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    /**
     * 获取刷新头，重写该方法可以替换刷新头部
     *
     * @return
     */
    protected RefreshHeader getRefreshHeader() {
        return new BezierRadarHeader(this);
    }

    /**
     * 获取加载底，重写该方法可以替换加载底
     *
     * @return
     */
    protected RefreshFooter getRefreshFooter() {
        return new ClassicsFooter(this);
    }


    protected abstract SmartRefreshLayout getSmartRefreshLayout();


    @Override
    public void onSuccess(String result, Request request) {
        String from = request.getFrom();
        Logger.d(TAG, "onSuccess: from = " + from + " url = " + request.getUrl());
        Logger.d(TAG, "onSuccess: result = " + result);
        JsonParserUtils<T> tJsonParserUtils = new JsonParserUtils<>(this);
        if (Request.FROM_JU_HE.equals(from)) {
            tJsonParserUtils.parserByJuHe(result, getType());
        } else if (Request.FROM_MYSELF.equals(from)) {
            tJsonParserUtils.parserByMySelf(result, getType());
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        Logger.e(TAG, "onError: ex = " + ex);
        fail(ex.toString());
    }

    @Override
    public void onCancelled(Callback.CancelledException cex) {
        Logger.d(TAG, "onCancelled: cex = " + cex);
        fail(cex.toString());
    }

    @Override
    public void onFinished() {
        if (mSmartRefreshLayout != null) {
            if (mSmartRefreshLayout.isRefreshing()) {
                mSmartRefreshLayout.finishRefresh();
            }
            if (mSmartRefreshLayout.isLoading()) {
                mSmartRefreshLayout.finishLoadMore();
            }
        }
        hideLoading();
    }


    public void handleBySuccess(T result) {

    }

    public void handleBySuccess(List<T> result) {

    }

    public void handleByFail(String msg) {
        showToast(msg);
    }

    @Override
    public void success(T result) {
        handleBySuccess(result);
    }

    @Override
    public void success(List<T> result) {
        handleBySuccess(result);
    }

    @Override
    public void fail(String msg) {
        handleByFail(msg);
    }
}
