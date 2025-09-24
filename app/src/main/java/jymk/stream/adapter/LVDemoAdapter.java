package jymk.stream.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import jymk.stream.R;
import jymk.stream.entity.LVDemoItem;
import jymk.stream.listener.OnDataUpdateListener;

public class LVDemoAdapter extends BaseAdapter {
    Context mCtx;
    List<LVDemoItem> mItems;
    int mPage, mSize;

    private final static String TAG = "LVDemoAdapter";

    private OnDataUpdateListener mListener;

    public LVDemoAdapter(Context ctx, List<LVDemoItem> items, int page, int size) {
        mItems = items;
        mCtx = ctx;
        mPage = page;
        mSize = size;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.lv_item, parent, false);
            holder = new ViewHolder();
            holder.mLeft = convertView.findViewById(R.id.lv_item_left);
            holder.mRight = convertView.findViewById(R.id.lv_item_right);
            holder.mDelete = convertView.findViewById(R.id.lv_item_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LVDemoItem item = mItems.get(position);
        holder.mLeft.setText(item.left);
        holder.mRight.setText(item.right);

        // 删除数据
        holder.mDelete.setOnClickListener(v -> {
            mItems.remove(position);
            if (mListener != null) {
                Log.e(TAG, "call remove, mItems size=" + mItems.size());
                mListener.onDataDeleted(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    // 设置接口对象的方法
    public void setOnDataUpdateListener(OnDataUpdateListener listener) {
        this.mListener = listener;
    }

    public static class ViewHolder {
        TextView mLeft;
        TextView mRight;
        Button mDelete;
    }
}
