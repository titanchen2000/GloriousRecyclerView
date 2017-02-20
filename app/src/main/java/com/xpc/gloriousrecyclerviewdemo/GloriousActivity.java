package com.xpc.gloriousrecyclerviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.xpc.gloriousrecyclerview.GloriousRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cxp on 17-2-15.
 */

public class GloriousActivity extends AppCompatActivity {

    private GloriousRecyclerView recyclerView;
    private NormalAdapter mAdapter;
    private int mLoadMoreTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glorious_recycler_view);
        recyclerView = (GloriousRecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new NormalAdapter(this);
        mAdapter.addDatas(constructTestDatas());

        final View footer = LayoutInflater.from(this).inflate(R.layout.layout_footer, recyclerView, false);
        View header = LayoutInflater.from(this).inflate(R.layout.layout_header, recyclerView, false);
        View empty = LayoutInflater.from(this).inflate(R.layout.layout_empty, recyclerView, false);

        recyclerView.setAdapter(mAdapter);
        recyclerView.addHeaderView(header);
        recyclerView.addFooterView(footer);
        recyclerView.setEmptyView(empty);
        recyclerView.setLoadMoreListener(new GloriousRecyclerView.AutoLoadMoreListener() {
            @Override
            public void onLoadMore() {
                constructLoadMoreTestDatas();
            }
        });
    }

    private List<String> constructTestDatas() {
        List<String> datas = new ArrayList<>();
        datas.add("刘一");
        datas.add("陈二");
        datas.add("张三");
        datas.add("李四");
        datas.add("王五");
        datas.add("赵六");
        datas.add("孙七");
        datas.add("周八");
        datas.add("吴九");
        datas.add("郑十");
        return datas;
    }

    private void constructLoadMoreTestDatas() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> datas = new ArrayList<>();
                        datas.add("夏 " + mLoadMoreTimes);
                        datas.add("商 " + mLoadMoreTimes);
                        datas.add("周 " + mLoadMoreTimes);
                        datas.add("春秋 " + mLoadMoreTimes);
                        datas.add("春秋 " + mLoadMoreTimes);
                        datas.add("秦 " + mLoadMoreTimes);
                        boolean mockSuccess = new Random().nextBoolean();
                        if (mockSuccess) {
                            mAdapter.addDatas(datas);
                            boolean mockHasMore = new Random().nextBoolean();
                            recyclerView.notifyLoadMoreSuccessful(mockHasMore);
                            mLoadMoreTimes++;
                        } else {
                            recyclerView.notifyLoadMoreFailed();
                        }
                    }
                });
            }
        }).start();

    }
}
