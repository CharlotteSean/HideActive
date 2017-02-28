package com.hideactive.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView的Adapter,实现加载更多
 * Created by senierr on 2016.11.16
 */
public abstract class BaseRVAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = BaseRVAdapter.class.getSimpleName();

    private static final int TYPE_LOAD_VIEW = 10001;

    protected final static int STATUS_NORMAL = 101;
    protected final static int STATUS_LOADING = 102;
    protected final static int STATUS_LOAD_NO_MORE = 103;
    protected final static int STATUS_LOAD_FAILURE = 104;

    private Context mContext;
    private List<T> mList;
    // 是否开启加载更多，默认关闭
    private boolean mOpenLoadMore = false;
    // 加载更多布局ID
    private int mLoadViewId;
    // 加载更多布局
    private ViewHolder mLoadViewHolder;
    // 加载状态
    private int mStatus = STATUS_NORMAL;
    // 加载更多回调
    private OnLoadMoreListener mOnLoadMoreListener;
    // 加载失败点击回调
    private View.OnClickListener mOnFailureClickListener;
    // 点击回调
    private OnItemClickListener mOnItemClickListener;

    protected abstract ViewHolder onCreateVH(ViewGroup parent, int viewType);

    protected abstract void onBindVH(ViewHolder holder, int position, T t);

    protected abstract void onStatusChanged(int status);

    protected int getViewType(int position, T t) {
        return 0;
    }

    public BaseRVAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        if (mOpenLoadMore && viewType == TYPE_LOAD_VIEW) {
            viewHolder = ViewHolder.create(mLoadViewId, parent);
            mLoadViewHolder = viewHolder;
            mLoadViewHolder.itemView.setOnClickListener(
                    mOnFailureClickListener != null
                            ? mOnFailureClickListener : new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLMStart();
                }
            });
            mStatus = STATUS_NORMAL;
            onStatusChanged(mStatus);
        } else {
            viewHolder = onCreateVH(parent, viewType);
            if (mOnItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(viewHolder, viewHolder.getLayoutPosition());
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onItemLongClick(viewHolder, viewHolder.getLayoutPosition());
                        return true;
                    }
                });
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() != TYPE_LOAD_VIEW) {
            onBindVH((ViewHolder) holder, position, getItem(position));
        }
    }

    @Override
    public int getItemCount() {
        int footerCount = mOpenLoadMore && !mList.isEmpty() ? 1 : 0;
        return mList.size() + footerCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position)) {
            return TYPE_LOAD_VIEW;
        }
        return getViewType(position, mList.get(position));
    }

    /**
     * 获取某一项
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        if (mList.isEmpty() || position < 0 || position >= mList.size()) {
            return null;
        }
        return mList.get(position);
    }

    /**
     * 是否是FooterView
     *
     * @param position
     * @return
     */
    private boolean isFooterView(int position) {
        return mOpenLoadMore && position >= getItemCount() - 1;
    }

    /**
     * StaggeredGridLayoutManager模式时，FooterView可占据一行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isFooterView(holder.getLayoutPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    /**
     * GridLayoutManager模式时， FooterView可占据一行，判断RecyclerView是否到达底部
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isFooterView(position)) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (checkCanLoadMore(recyclerView)) {
                    setLMStart();
                    Log.d(TAG, "startLoadMore");
                }
            }
        });
    }

    /**
     * 判断是否可以加载更多（列表最底部等）
     *
     * @param recyclerView
     */
    public boolean checkCanLoadMore(RecyclerView recyclerView) {
        return mOpenLoadMore && mOnLoadMoreListener != null
                && mStatus != STATUS_LOADING
                && getLastVisibleItemPosition(recyclerView) + 1 >= getItemCount();
    }

    /**
     * 获第一个可视Item
     */
    public int getFirstVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        int firstVisibleItemPosition;
        if (layoutManager instanceof LinearLayoutManager) {
            firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions);
            firstVisibleItemPosition = findMax(lastPositions);
        } else {
            throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }
        return firstVisibleItemPosition;
    }

    /**
     * 获取最后一个可视Item
     */
    public int getLastVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        int lastVisibleItemPosition;
        if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
            lastVisibleItemPosition = findMax(lastPositions);
        } else {
            throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
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

    /**
     * 是否可滚动
     *
     * @param recyclerView
     * @return
     */
    private boolean canScroll(RecyclerView recyclerView) {
        return ViewCompat.canScrollVertically(recyclerView, 1) || ViewCompat.canScrollVertically(recyclerView, -1);
    }

    /**
     * 开始加载
     */
    public void setLMStart() {
        if (mOpenLoadMore && mOnLoadMoreListener != null) {
            if (mOnLoadMoreListener.onLoadMore()) {
                if (mLoadViewHolder != null && mLoadViewHolder.itemView != null) {
                    mLoadViewHolder.itemView.setClickable(false);
                }
                mStatus = STATUS_LOADING;
                onStatusChanged(mStatus);
            }
        }
    }

    /**
     * 加载完成
     */
    public void setLMNormal() {
        if (mOpenLoadMore && mOnLoadMoreListener != null) {
            if (mLoadViewHolder != null && mLoadViewHolder.itemView != null) {
                mLoadViewHolder.itemView.setClickable(false);
            }
            mStatus = STATUS_NORMAL;
            onStatusChanged(mStatus);
        }
    }

    /**
     * 没有更多数据
     */
    public void setLMNoMore() {
        if (mOpenLoadMore && mOnLoadMoreListener != null) {
            if (mLoadViewHolder != null && mLoadViewHolder.itemView != null) {
                mLoadViewHolder.itemView.setClickable(false);
            }
            mStatus = STATUS_LOAD_NO_MORE;
            onStatusChanged(mStatus);
        }
    }

    /**
     * 加载失败
     */
    public void setLMFailure() {
        if (mOpenLoadMore && mOnLoadMoreListener != null) {
            if (mLoadViewHolder != null && mLoadViewHolder.itemView != null) {
                mLoadViewHolder.itemView.setClickable(true);
            }
            mStatus = STATUS_LOAD_FAILURE;
            onStatusChanged(mStatus);
        }
    }

    /**
     * 设置新数据，清空旧数据
     *
     * @param list
     */
    public void resetData(List<T> list) {
        if (mList == null || list == null) {
            return;
        }
        synchronized (this) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 底部添加数据
     *
     * @param list
     */
    public void addData2Last(List<T> list) {
        if (mList == null || list == null) {
            return;
        }
        synchronized (this) {
            int size = mList.size();
            mList.addAll(list);
            notifyItemRangeInserted(size, list.size());
        }
    }

    /**
     * 顶部添加数据
     *
     * @param list
     */
    public void addData2First(List<T> list) {
        if (mList == null || list == null) {
            return;
        }
        synchronized (this) {
            mList.addAll(0, list);
            notifyItemRangeInserted(0, list.size());
        }
    }

    /**
     * 从指针处添加数据
     *
     * @param list
     */
    public void addData2Position(List<T> list, int position) {
        if (mList == null || list == null) {
            return;
        }
        synchronized (this) {
            mList.addAll(position, list);
            notifyItemRangeInserted(position, list.size());
        }
    }

    /**
     * 删除某项
     *
     * @param position
     */
    public void removeItem(int position) {
        if (mList == null || position < 0 || position >= mList.size()) {
            return;
        }
        synchronized (this) {
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * 删除全部数据
     *
     */
    public void removeAll() {
        if (mList == null) {
            return;
        }
        synchronized (this) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 获取数据
     *
     * @return
     */
    public List<T> getData() {
        return mList;
    }

    /**
     * 获取加载更多布局
     *
     * @return
     */
    public ViewHolder getLoadViewHolder() {
        return mLoadViewHolder;
    }

    /**
     * 判断是否开启加载更多
     *
     * @return
     */
    public boolean isOpenLoadMore() {
        return mOpenLoadMore;
    }

    /**
     * 判断是否正在加载
     * @return
     */
    public boolean isLoading() {
        return mStatus == STATUS_LOADING;
    }

    /**
     * 设置加载失败点击回调
     *
     * @param onFailureClickListener
     */
    public void setOnFailureClickListener(View.OnClickListener onFailureClickListener) {
        mOnFailureClickListener = onFailureClickListener;
    }

    /**
     * 开启加载更多
     *
     * @param loadViewId
     * @param onLoadMoreListener
     */
    public void setLMOpened(@LayoutRes int loadViewId, OnLoadMoreListener onLoadMoreListener) {
        mOpenLoadMore = true;
        mLoadViewId = loadViewId;
        mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * 设置点击回调
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 加载更多回调
     */
    public interface OnLoadMoreListener {

        /**
         * 加载更多回调
         *
         * @return 是否成功开始加载
         */
        boolean onLoadMore();
    }

    // 点击回调
    public static abstract class OnItemClickListener {
        public void onItemClick(ViewHolder viewHolder, int position) {}
        public void onItemLongClick(ViewHolder viewHolder, int position) {}
    }
}
