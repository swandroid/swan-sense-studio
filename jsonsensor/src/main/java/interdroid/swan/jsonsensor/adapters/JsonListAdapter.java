package interdroid.swan.jsonsensor.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import interdroid.swan.jsonsensor.R;
import interdroid.swan.jsonsensor.pojos.JsonItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by steven on 21/10/14.
 */
public class JsonListAdapter extends RecyclerView.Adapter<JsonListAdapter.ViewHolder> {

    private ArrayList<JsonItem> mJsonItems;
    private OnJsonItemClickListener mOnJsonItemClickListener;



    public JsonListAdapter(OnJsonItemClickListener onJsonItemClickListener) {
        mJsonItems = new ArrayList<JsonItem>();
        mOnJsonItemClickListener = onJsonItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mRoot;
        public TextView mKey;
        public TextView mValue;
        public TextView mSummary;
        public int mPosition;
        public ViewHolder(View view) {
            super(view);
            mRoot = view;
            mKey = (TextView) mRoot.findViewById(R.id.item_json_key);
            mValue = (TextView) mRoot.findViewById(R.id.item_json_value);
            mSummary = (TextView) mRoot.findViewById(R.id.item_json_summary);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_json, parent, false);

        // set the view's size, margins, paddings and layout parameters

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        view.setOnClickListener(mOnClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        JsonItem jsonItem = mJsonItems.get(position);

        viewHolder.mKey.setText(jsonItem.key);
        viewHolder.mSummary.setVisibility(View.GONE);
        if (jsonItem.jsonItems != null) {
            ArrayList<JsonItem> jsonItems = jsonItem.jsonItems;
            if (jsonItems.size() > 0) {
                viewHolder.mValue.setText("[" + jsonItems.size() + "]");
            }
        } else if (jsonItem.jsonItem != null) {
            ArrayList<JsonItem> jsonItems = jsonItem.jsonItem.jsonItems;
            viewHolder.mValue.setText("{" + jsonItems.size() + "}");
            int end = 3;
            if (jsonItems.size() < 3) {
                end = jsonItems.size();
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < end; i++) {
                JsonItem jsonSubItem = jsonItems.get(i);
                sb.append(jsonSubItem.key);
                sb.append(": ");
                if (jsonSubItem.stringItem != null) {
                    sb.append("\"");
                    sb.append(jsonSubItem.stringItem);
                    sb.append("\"");
                } else if (jsonSubItem.jsonItem != null && jsonSubItem.jsonItem.jsonItems.size() > 0) {
                    sb.append("{");
                    sb.append(jsonSubItem.jsonItem.jsonItems.size());
                    sb.append("}");
                } else if (jsonSubItem.jsonItems != null && jsonSubItem.jsonItems.size() > 0) {
                    sb.append("[");
                    sb.append(jsonSubItem.jsonItems.size());
                    sb.append("]");
                }
                if (i != end - 1) {
                    sb.append("\n");
                }
            }
            viewHolder.mSummary.setText(sb.toString());
            viewHolder.mSummary.setVisibility(View.VISIBLE);
        } else if (jsonItem.stringItem != null) {
            viewHolder.mValue.setText(jsonItem.stringItem);
        }
        viewHolder.mPosition = position;

    }

    @Override
    public int getItemCount() {
        return mJsonItems.size();
    }

    public void setJsonItems(ArrayList<JsonItem> jsonItems) {
        mJsonItems = jsonItems;
        if (mJsonItems != null && mJsonItems.size() > 0){
            try {
                Integer.parseInt(mJsonItems.get(0).key);
            } catch (NumberFormatException e) {
                Collections.sort(mJsonItems, new JsonItemsComparator());
            }
        }
        notifyDataSetChanged();
    }

    public class JsonItemsComparator implements Comparator<JsonItem> {
        @Override
        public int compare(JsonItem o1, JsonItem o2) {
            return o1.key.compareTo(o2.key);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mOnJsonItemClickListener != null) {
                mOnJsonItemClickListener.onJsonItemClicked(mJsonItems.get(((ViewHolder) v.getTag()).mPosition));
            }
        }
    };

    public interface OnJsonItemClickListener {
        public void onJsonItemClicked(JsonItem jsonItem);
    }
}
