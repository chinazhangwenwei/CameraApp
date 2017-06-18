package com.interjoy.camer2application.adpter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.wiseweb.watermelon.R;

import java.util.List;

/**
 * Created by admin on 2017/3/24.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context mContext;

    private List<T> mDatas;
    private View headViews;
    private View footViews;
    private View loadMoreView;
    private int layoutId;
    private int positionOffset = 0;

    private boolean isLoad = false;
    protected int TYPE_HEADER = 1;
    protected int TYPE_FOOTER = -1;
    protected int TYPE_NORMAL = 0;
    protected int TYPE_LOAD_MORE = -2;


    public BaseRecyclerViewAdapter(Context context, List<T> datas, int layoutId) {
        mContext = context;
        mDatas = datas;
        this.layoutId = layoutId;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new RecyclerViewHolder(headViews);
        } else if (viewType == TYPE_FOOTER) {
            return new RecyclerViewHolder(footViews);
        } else if (viewType == TYPE_LOAD_MORE) {
            RecyclerViewHolder viewHolder = RecyclerViewHolder.get(mContext, parent,
                    R.layout.list_load_more_item);
            loadMoreView = viewHolder.getView(R.id.load_layout);
            return viewHolder;
        }
        return RecyclerViewHolder.get(mContext, parent, layoutId);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        int type = getItemViewType(position);
        if (type == TYPE_NORMAL) {
        }
        if (type == TYPE_LOAD_MORE) {
        }
        if (type == TYPE_HEADER) {
        }
        if (type == TYPE_NORMAL) {

            convert(holder, mDatas.get(position - positionOffset));
        }

    }

    public View getHeadViews() {
        return headViews;
    }

    @Override
    public int getItemViewType(int position) {

        if (headViews != null && position == 0) {
            return TYPE_HEADER;
        }
        if (footViews != null && loadMoreView == null &&
                position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        if (footViews != null && loadMoreView != null &&
                position == getItemCount() - 2) {
            return TYPE_FOOTER;
        }

        if (isLoad && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }
        return TYPE_NORMAL;

    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (headViews != null) {
            itemCount = 1;
        }
        if (mDatas != null) {
            itemCount += mDatas.size();
        }
        if (footViews != null) {
            itemCount += 1;
        }
        if (isLoad) {
            itemCount += 1;
        }
        return itemCount;
    }

    /**
     * 处理GridLayoutManager的foot和head
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER ||
                            getItemViewType(position) == TYPE_FOOTER ||
                            getItemViewType(position) == TYPE_LOAD_MORE
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    public void addHeadView(View headView) {
        headViews = headView;
        positionOffset = 1;

    }

    public void removeHeadView() {
        if (headViews != null) {
            headViews = null;
            positionOffset = 0;
            notifyItemChanged(0);

        }
    }

    public void removeFootView() {
        if (footViews != null) {
            footViews = null;
            if (loadMoreView == null) {
                notifyItemChanged(getItemCount() - 1);
            } else {
                notifyItemChanged(getItemCount() - 2);
            }
        }
    }


    public void addFootView(View footView) {
        footViews = footView;
        if (loadMoreView == null) {
            notifyItemChanged(getItemCount() - 1);
        } else {
            notifyItemChanged(getItemCount() - 2);
        }
    }

    public void enableLoadMoreView() {
        if (mContext == null) {
            throw new RuntimeException("mContext 为null");
        }
        isLoad = true;
    }

    public void cancelLoadMoreView() {
        if (mContext == null) {
            throw new RuntimeException("mContext 为null");
        }
        isLoad = false;
    }

    public void setLoadingState() {
        if (loadMoreView != null) {
            loadMoreView.findViewById(R.id.tv_load_end).setVisibility(View.INVISIBLE);
            loadMoreView.findViewById(R.id.load_layout_loading).setVisibility(View.VISIBLE);
        }

    }

    public void setLoadEndState() {
        if (loadMoreView != null) {
            loadMoreView.findViewById(R.id.tv_load_end).setVisibility(View.VISIBLE);
            loadMoreView.findViewById(R.id.load_layout_loading).setVisibility(View.INVISIBLE);
        }
    }

    public void setL1oadNetErrorState() {

    }


    /**
     * 处理StaggeredGridLayoutManager的foot和head
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(holder.getLayoutPosition() == 0);
        }
    }


    public abstract void convert(RecyclerViewHolder viewHolder, T t);
}
