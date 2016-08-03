package com.hideactive.adapter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hideactive.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by senierr on 2016/07/08.
 */
public abstract class BaseLoadMoreAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = BaseLoadMoreAdapter.class.getSimpleName();

    public enum LoadMoreStatus {
        STATUS_INIT,
        STATUS_UNLOAD,
        STATUS_LOADING,
        STATUS_NO_MORE,
        STATUS_ERROR
    }

    public enum layoutManagerType {
        LINEAR_LAYOUT,
        GRID_LAYOUT,
        STAGGERED_GRID_LAYOUT
    }

    // 加载更多事件
    private OnLoadMoreListener mOnLoadMoreListener;

    // 布局类型
    protected layoutManagerType mLayoutManagerType;
    //正常条目
    private static final int TYPE_NORMAL_ITEM = 0;
    //加载条目
    private static final int TYPE_LOADING_ITEM = 1;
    //加载viewHolder
    private LoadMoreViewHolder mLoadMoreViewHolder;
    //数据集
    private List<T> mData = new ArrayList();

    private RecyclerView mRecyclerView;
    // 加载更多界面是否正在显示
    private boolean mLoadMoreIsVisible = false;
    // 状态
    private LoadMoreStatus mLoadMoreStatus = LoadMoreStatus.STATUS_INIT;

    public BaseLoadMoreAdapter(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        setSpanCount(recyclerView);
    }

    /**
     * 增加一项
     * @param t
     * @param position
     */
    public void addItem(T t, int position) {
        mData.add(position, t);
        notifyItemInserted(position);
    }

    /**
     * 删除一项
     * @param position
     */
    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 增加多项
     * @param t
     * @param position
     */
    public void addItems(List<T> t, int position) {
        mData.addAll(position, t);
        notifyItemRangeInserted(position, t.size());
    }

    /**
     * 删除多项
     * @param t
     */
    public void removeItems(List<T> t) {
        mData.removeAll(t);
        notifyDataSetChanged();
    }

    /**
     * 加载更多接口
     */
    public interface OnLoadMoreListener {
        void loadMore();
    }

    /**
     * 设置监听接口
     *
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        setScrollListener(mRecyclerView);
        mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * 加载更多布局
     */
    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public TextView tvLoading;

        public LoadMoreViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_loading);
            tvLoading = (TextView) view.findViewById(R.id.tv_loading);
        }
    }

    /**
     * 获取当前的状态
     * @return
     */
    public LoadMoreStatus getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    /**
     * 重置Adapter数据集
     * @param newData
     * @return
     */
    public List<T> resetData(List<T> newData) {
        List<T> temp = new ArrayList<>();
        mLoadMoreStatus = LoadMoreStatus.STATUS_UNLOAD;
        mLoadMoreIsVisible = false;
        mData.clear();
        // 更新数据
        if (newData != null) {
            mData.addAll(newData);
            notifyDataSetChanged();
        }
        temp.addAll(mData);
        return temp;
    }

    /**
     * 加载完成
     */
    public List<T> setLoadMore(List newData) {
        List<T> temp = new ArrayList<>();
        if (mData != null && mLoadMoreIsVisible && mLoadMoreStatus == LoadMoreStatus.STATUS_LOADING) {
            mLoadMoreStatus = LoadMoreStatus.STATUS_UNLOAD;
            mLoadMoreIsVisible = false;
            removeItem(mData.size() - 1);
            // 更新数据
            if (newData != null) {
                int positionStart = getItemCount();
                mData.addAll(newData);
                notifyItemRangeInserted(positionStart, newData.size());
            }
            temp.addAll(mData);
        }
        return temp;
    }

    /**
     * 加载更多
     */
    public void setLoadMore() {
        if (!mLoadMoreIsVisible) {
            addItem(null, mData.size());
            mLoadMoreIsVisible = true;
        }
        if (mLoadMoreStatus == LoadMoreStatus.STATUS_NO_MORE) {
            return;
        }
        mLoadMoreStatus = LoadMoreStatus.STATUS_LOADING;
        if (mLoadMoreViewHolder != null) {
            mLoadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
            mLoadMoreViewHolder.tvLoading.setText(R.string.load_more);
        }

        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.loadMore();
        }
    }

    /**
     * 没有更多数据
     */
    public void setLoadNoMore() {
        mLoadMoreStatus = LoadMoreStatus.STATUS_NO_MORE;
        if (mLoadMoreViewHolder != null) {
            mLoadMoreViewHolder.progressBar.setVisibility(View.GONE);
            mLoadMoreViewHolder.tvLoading.setText(R.string.no_more);
        }
    }

    /**
     * 加载失败
     */
    public void setLoadFailure() {
        if (mLoadMoreViewHolder != null) {
            mLoadMoreStatus = LoadMoreStatus.STATUS_ERROR;
            mLoadMoreViewHolder.progressBar.setVisibility(View.GONE);
            mLoadMoreViewHolder.tvLoading.setText(R.string.load_error);

            mLoadMoreViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnLoadMoreListener != null) {
                        mLoadMoreStatus = LoadMoreStatus.STATUS_LOADING;
                        mLoadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
                        mLoadMoreViewHolder.tvLoading.setText(R.string.load_more);
                        mOnLoadMoreListener.loadMore();
                    }
                }
            });
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll. Override this if the child view is a custom view.
     */
    private boolean canScroll(RecyclerView recyclerView) {
        return ViewCompat.canScrollVertically(recyclerView, 1) || ViewCompat.canScrollVertically(recyclerView, -1);
    }

    /**
     * 设置加载item占据一行
     *
     * @param recyclerView recycleView
     */
    private void setSpanCount(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (mLayoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                mLayoutManagerType = layoutManagerType.LINEAR_LAYOUT;
            } else if (layoutManager instanceof GridLayoutManager) {
                mLayoutManagerType = layoutManagerType.GRID_LAYOUT;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                mLayoutManagerType = layoutManagerType.STAGGERED_GRID_LAYOUT;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        //网格布局
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_NORMAL_ITEM) {
                        return 1;
                    } else {
                        return gridLayoutManager.getSpanCount();
                    }
                }
            });
        }
    }

    /**
     * 监听滚动事件
     *
     * @param recyclerView recycleView
     */
    private void setScrollListener(RecyclerView recyclerView) {
        if(recyclerView == null) {
            Log.e(TAG, "RecycleView is null!");
            return;
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当可滚动（数据没完全展示）时，显示加载更多界面
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                        && canScroll(recyclerView) && !mLoadMoreIsVisible) {
                    addItem(null, mData.size());
                    mLoadMoreIsVisible = true;
                    if (mLoadMoreStatus == LoadMoreStatus.STATUS_INIT) {
                        mLoadMoreStatus = LoadMoreStatus.STATUS_UNLOAD;
                    }
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE && canScroll(recyclerView)) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItemPosition = getLastVisibleItemPosition(recyclerView);

                    // 判断是否滚动到加载界面
                    if (lastVisibleItemPosition >= totalItemCount - 1
                            && mLoadMoreStatus == LoadMoreStatus.STATUS_UNLOAD) {
                        setLoadMore();
                    }
                }
            }

            /**
             * 获取最后一个可视Item
             */
            private int getLastVisibleItemPosition(RecyclerView recyclerView) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int lastVisibleItemPosition = 0;
                switch (mLayoutManagerType) {
                    case LINEAR_LAYOUT:
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case GRID_LAYOUT:
                        lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case STAGGERED_GRID_LAYOUT:
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                        lastVisibleItemPosition = findMax(lastPositions);
                        break;
                    default:
                        break;
                }
                return lastVisibleItemPosition;
            }

            private int findMax(int[] lastPositions) {
                int max = lastPositions[0];
                for (int value : lastPositions) {
                    if (value > max) {
                        max = value;
                    }
                }
                return max;
            }
        });
    }

    /**
     * 创建viewHolder
     *
     * @param parent viewGroup
     * @return viewHolder
     */
    public abstract RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent);

    /**
     * 绑定viewHolder
     *
     * @param holder   viewHolder
     * @param position position
     */
    public abstract void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position, T t);

    @Override
    public int getItemViewType(int position) {
        Object item = mData.get(position);
        if (item == null && position == getItemCount() - 1) {
            return TYPE_LOADING_ITEM;
        } else {
            return TYPE_NORMAL_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL_ITEM) {
            return onCreateNormalViewHolder(parent);
        } else {
            if (mLoadMoreViewHolder == null) {
                View view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.loading_layout, parent, false);
                mLoadMoreViewHolder = new LoadMoreViewHolder(view);
            }
            return mLoadMoreViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_NORMAL_ITEM) {
            onBindNormalViewHolder(holder, position, mData.get(position));
        } else {
            if (mLayoutManagerType == layoutManagerType.STAGGERED_GRID_LAYOUT) {
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        new StaggeredGridLayoutManager.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                holder.itemView.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
