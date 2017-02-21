/*
 * Copyright (C) 2017 CXP 277371483@qq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xpc.gloriousrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created on 17-2-14
 *
 * @author cxp
 */

public class GloriousRecyclerView extends RecyclerView {

    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;
    private View mLoadMoreView;
    private boolean mIsLoadMoreEnabled;
    private boolean mIsLoadingMore;
    private GloriousAdapter mGloriousAdapter;
    private AutoLoadMoreListener mLoadMoreListener;

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mIsLoadMoreEnabled && !mIsLoadingMore && dy > 0) {
                if (findLastVisibleItemPosition() == mGloriousAdapter.getItemCount() - 1) {
                    mIsLoadingMore = true;
                    mLoadMoreListener.onLoadMore();
                }
            }
        }
    };

    public GloriousRecyclerView(Context context) {
        super(context);
    }

    public GloriousRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GloriousRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View view) {
        mHeaderView = view;
        mGloriousAdapter.notifyItemInserted(0);
    }

    public void addFooterView(View view) {
        mFooterView = view;
        mGloriousAdapter.notifyItemInserted(mGloriousAdapter.getItemCount() - 1);
    }

    public void setEmptyView(View view) {
        mEmptyView = view;
        mGloriousAdapter.notifyDataSetChanged();
    }

    public void setLoadMoreListener(final AutoLoadMoreListener loadMoreListener) {
        if (null != loadMoreListener) {
            mLoadMoreListener = loadMoreListener;
            mIsLoadMoreEnabled = true;
            this.addOnScrollListener(mOnScrollListener);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mGloriousAdapter = new GloriousAdapter(adapter);
        }
        super.setAdapter(mGloriousAdapter);
    }

    private int findLastVisibleItemPosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = findMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得StaggeredGridLayoutManager最大的位置
     */
    private int findMaxPosition(int[] positions) {
        int maxPosition = 0;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }

    public void notifyLoadMoreFailed() {
        notifyLoadMoreFinish(false, true);
    }

    public void notifyLoadMoreSuccessful(boolean hasMore) {
        notifyLoadMoreFinish(true, hasMore);
    }

    private void notifyLoadMoreFinish(boolean success, boolean hasMore) {
        this.clearOnScrollListeners();
        mIsLoadingMore = false;
        //如果启用下面这行代码，当没有更多数据时，“mLoadMoreView”将不再显示
        //如果不启用,当没有更多数据时，“mLoadMoreView”将不再显示将显示“所有数据已加载完毕”
//        mIsLoadMoreEnabled = hasMore;
        TextView tvLoadingMore = (TextView) mLoadMoreView.findViewById(R.id.tv_loading_more);
        ProgressBar progressBar = (ProgressBar) mLoadMoreView.findViewById(R.id.pb_loading_more);
        if (success) {
            mGloriousAdapter.notifyDataSetChanged();
            if (hasMore) {
                progressBar.setVisibility(VISIBLE);
                tvLoadingMore.setText(R.string.glorious_recyclerview_loading_more);
                this.addOnScrollListener(mOnScrollListener);
            } else {
                mLoadMoreView.setOnClickListener(null);
                progressBar.setVisibility(GONE);
                tvLoadingMore.setText(R.string.glorious_recyclerview_no_more_data);
            }
        } else {
            tvLoadingMore.setText(R.string.glorious_recyclerview_load_more_failed);
            progressBar.setVisibility(GONE);
        }
    }

    public interface AutoLoadMoreListener {
        void onLoadMore();
    }

    private class GloriousAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Adapter mOriginalAdapter;
        private int ITEM_TYPE_NORMAL = 0;
        private int ITEM_TYPE_HEADER = 1;
        private int ITEM_TYPE_FOOTER = 2;
        private int ITEM_TYPE_EMPTY = 3;
        private int ITEM_TYPE_LOAD_MORE = 4;

        //聪明的人会发现我们这里用了一个装饰模式
        public GloriousAdapter(Adapter originalAdapter) {
            mOriginalAdapter = originalAdapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                return new GloriousViewHolder(mHeaderView);
            } else if (viewType == ITEM_TYPE_EMPTY) {
                return new GloriousViewHolder(mEmptyView);
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return new GloriousViewHolder(mFooterView);
            } else if (viewType == ITEM_TYPE_LOAD_MORE) {
                mLoadMoreView = LayoutInflater.from(getContext()).inflate(R.layout.glorious_recyclerview_layout_load_more, parent, false);
                mLoadMoreView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsLoadingMore) {
                            mIsLoadingMore = true;
                            mLoadMoreView.findViewById(R.id.pb_loading_more).setVisibility(VISIBLE);
                            TextView tvLoadingMore = (TextView) mLoadMoreView.findViewById(R.id.tv_loading_more);
                            tvLoadingMore.setText(R.string.glorious_recyclerview_loading_more);
                            tvLoadingMore.setVisibility(VISIBLE);
                            mLoadMoreListener.onLoadMore();
                        }
                    }
                });
                return new GloriousViewHolder(mLoadMoreView);
            } else {
                return mOriginalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == ITEM_TYPE_HEADER
                    || type == ITEM_TYPE_FOOTER
                    || type == ITEM_TYPE_EMPTY
                    || type == ITEM_TYPE_LOAD_MORE) {
                return;
            }
            int realPosition = getRealItemPosition(position);
            mOriginalAdapter.onBindViewHolder(holder, realPosition);
        }

        @Override
        public int getItemCount() {
            int itemCount = mOriginalAdapter.getItemCount();
            //加上其他各种View
            if (null != mHeaderView) itemCount++;
            if (null != mFooterView) itemCount++;
            if (null != mEmptyView && itemCount == 0) {
                itemCount++;
                //如果数据为空，不显示加载更多
                return itemCount;
            }
            if (mIsLoadMoreEnabled) itemCount++;
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (null != mHeaderView && position == 0) return ITEM_TYPE_HEADER;
            if (null != mFooterView && position == getItemCount() - 1) return ITEM_TYPE_FOOTER;
            //如果数据为空，不显示加载更多
            if (null != mEmptyView && mOriginalAdapter.getItemCount() == 0) {
                return ITEM_TYPE_EMPTY;
            } else if (mIsLoadMoreEnabled && position == getLoadMorePosition()) {
                return ITEM_TYPE_LOAD_MORE;
            }
            return ITEM_TYPE_NORMAL;
        }

        private int getRealItemPosition(int position) {
            if (null != mHeaderView) {
                return position - 1;
            }
            return position;
        }

        /**
         * 获取加载更多的Position
         *
         * @return 如果mFooterView为空，那么加载更多在最后一个，如果不为空，加载更多在倒数第二个
         */
        int getLoadMorePosition() {
            if (null == mFooterView) {
                return getItemCount() - 1;
            } else {
                return getItemCount() - 2;
            }
        }

        /**
         * ViewHolder 是一个抽象类
         */
        class GloriousViewHolder extends ViewHolder {

            GloriousViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
