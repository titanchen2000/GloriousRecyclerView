package com.xpc.gloriousrecyclerviewdemo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.xpc.gloriousrecyclerview.GloriousRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cxp on 17-2-15.
 */

public class GloriousActivity extends AppCompatActivity implements OnItemClickListener {

    private GloriousRecyclerView mRecyclerView;
    private NormalAdapter mAdapter;
    private int mLoadMoreTimes;
    private ScrollChildSwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glorious_recycler_view);
        mRecyclerView = (GloriousRecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new NormalAdapter(this);
        mAdapter.addDatas(constructTestData());
        mAdapter.setOnItemClickListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.layout_header, mRecyclerView, false);
        View empty = LayoutInflater.from(this).inflate(R.layout.layout_empty, mRecyclerView, false);
        final View footer = LayoutInflater.from(this).inflate(R.layout.layout_footer, mRecyclerView, false);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.removeFooterView();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addHeaderView(header);
        mRecyclerView.addFooterView(footer);
        mRecyclerView.setEmptyView(empty);
        mRecyclerView.setLoadMoreListener(new GloriousRecyclerView.AutoLoadMoreListener() {
            @Override
            public void onLoadMore() {
                constructLoadMoreTestData();
            }
        });
        mSwipeRefreshLayout = (ScrollChildSwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setScrollUpChild(mRecyclerView);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setDatas(constructRefreshTestData());
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private List<String> constructTestData() {
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

    private void constructLoadMoreTestData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Mock the network
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
                        datas.add("战国 " + mLoadMoreTimes);
                        datas.add("秦 " + mLoadMoreTimes);
                        boolean mockSuccess = new Random().nextBoolean();
                        if (mockSuccess) {
                            mAdapter.addDatas(datas);
                            boolean mockHasMore = new Random().nextBoolean();
                            mRecyclerView.notifyLoadMoreSuccessful(mockHasMore);
                            mLoadMoreTimes++;
                        } else {
                            mRecyclerView.notifyLoadMoreFailed();
                        }
                    }
                });
            }
        }).start();

    }

    private List<String> constructRefreshTestData() {
        List<String> datas = new ArrayList<>();
        datas.add("刘一刘");
        datas.add("陈二陈");
        datas.add("张三张");
        datas.add("李四李");
        datas.add("王五王");
        datas.add("赵六赵");
        datas.add("孙七孙");
        datas.add("周八周");
        datas.add("吴九吴");
        datas.add("郑十郑");
        return datas;
    }

    @Override
    public void OnItemClick(int position) {
        Toast.makeText(this, mAdapter.getDatas().get(position), Toast.LENGTH_SHORT).show();
    }
}
