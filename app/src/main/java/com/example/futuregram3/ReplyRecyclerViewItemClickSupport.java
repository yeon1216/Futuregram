package com.example.futuregram3;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class ReplyRecyclerViewItemClickSupport {
    private final RecyclerView mRecyclerView;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    // ItemClickSupport 생성자
    private ReplyRecyclerViewItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.replyRecyclerView, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        }
    };

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
            return false;
        }
    };
    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener);
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };

    public static ReplyRecyclerViewItemClickSupport addTo(RecyclerView view) {
        ReplyRecyclerViewItemClickSupport support = (ReplyRecyclerViewItemClickSupport) view.getTag(R.id.replyRecyclerView);
        if (support == null) {
            support = new ReplyRecyclerViewItemClickSupport(view);
        }
        return support;
    }

    public static ReplyRecyclerViewItemClickSupport removeFrom(RecyclerView view) {
        ReplyRecyclerViewItemClickSupport support = (ReplyRecyclerViewItemClickSupport) view.getTag(R.id.replyRecyclerView);
        if (support != null) {
            support.detach(view);
        }
        return support;
    }

    public ReplyRecyclerViewItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    public ReplyRecyclerViewItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        return this;
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.replyRecyclerView, null);
    }

    public interface OnItemClickListener {
        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
    }
}