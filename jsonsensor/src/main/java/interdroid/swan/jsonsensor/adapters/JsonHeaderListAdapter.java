package interdroid.swan.jsonsensor.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import interdroid.swan.jsonsensor.pojos.JsonItem;
import com.liutoapps.android.jsonsensor.R;

import java.util.ArrayList;

/**
 * Created by steven on 21/10/14.
 */
public class JsonHeaderListAdapter extends RecyclerView.Adapter<JsonHeaderListAdapter.ViewHolder> {

    private ArrayList<JsonItem> mJsonItems;
    private OnJsonItemClickListener mOnJsonItemClickListener;

    public JsonHeaderListAdapter(OnJsonItemClickListener onJsonItemClickListener) {
        mJsonItems = new ArrayList<JsonItem>();
        mOnJsonItemClickListener = onJsonItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mRoot;
        public TextView mKey;
        public int mPosition;
        public ViewHolder(View view) {
            super(view);
            mRoot = view;
            mKey = (TextView) mRoot.findViewById(R.id.item_json_key);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_json_header, parent, false);

        // set the view's size, margins, paddings and layout parameters

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        view.setOnClickListener(mOnClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        JsonItem jsonItem = mJsonItems.get(i);

        viewHolder.mKey.setText(jsonItem.key);
        viewHolder.mPosition = i;

    }

    @Override
    public int getItemCount() {
        return mJsonItems.size();
    }

    public void addJsonItem(JsonItem jsonItem) {
        mJsonItems.add(jsonItem);
        notifyDataSetChanged();
    }

    public void setJsonItems(ArrayList<JsonItem> jsonItems) {
        mJsonItems = jsonItems;
        notifyDataSetChanged();
    }

    public ArrayList<JsonItem> getJsonItems() {
        return mJsonItems;
    }

    private void removeJsonItemsAfter(int position) {
        for (int i = mJsonItems.size() - 1; i > position; i--) {
            mJsonItems.remove(i);
        }
        notifyDataSetChanged();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mOnJsonItemClickListener != null) {
                int position = ((ViewHolder) v.getTag()).mPosition;
                removeJsonItemsAfter(position);
                mOnJsonItemClickListener.onJsonItemClicked(mJsonItems.get(position));
            }
        }
    };

    public interface OnJsonItemClickListener {
        public void onJsonItemClicked(JsonItem jsonItem);
    }
}
