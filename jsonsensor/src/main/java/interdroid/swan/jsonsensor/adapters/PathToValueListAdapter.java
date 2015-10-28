package interdroid.swan.jsonsensor.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import interdroid.swan.jsonsensor.R;
import interdroid.swan.jsonsensor.pojos.JsonPathType;
import interdroid.swan.jsonsensor.pojos.PathToValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by steven on 21/10/14.
 */
public class PathToValueListAdapter extends RecyclerView.Adapter<PathToValueListAdapter.ViewHolder> {

    private ArrayList<PathToValue> mPathToValues;
    private OnPathToValueClickListener mOnPathToValueClickListener;

    public PathToValueListAdapter(OnPathToValueClickListener onPathToValueClickListener) {
        mPathToValues = new ArrayList<PathToValue>();
        mOnPathToValueClickListener = onPathToValueClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mRoot;
        public TextView mName;
        public TextView mPath;
        public int mPosition;
        public ViewHolder(View view) {
            super(view);
            mRoot = view;
            mName = (TextView) mRoot.findViewById(R.id.item_path_to_value_name);
            mPath = (TextView) mRoot.findViewById(R.id.item_path_to_value_path);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_path_to_value, parent, false);

        // set the view's size, margins, paddings and layout parameters

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        view.setOnClickListener(mOnClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        PathToValue pathToValue = mPathToValues.get(position);

        viewHolder.mName.setText(pathToValue.name);
        getPathString(pathToValue);
        viewHolder.mPath.setText(getPathString(pathToValue));

        viewHolder.mPosition = position;

    }

    private String getPathString(PathToValue pathToValue) {
        StringBuilder sb = new StringBuilder();
        List<JsonPathType> jsonPathTypes = pathToValue.jsonPathTypes;
        int size = jsonPathTypes.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append("/");
            }
            if (jsonPathTypes.get(i).key == null) {
                sb.append(jsonPathTypes.get(i).index);
            } else {
                sb.append(jsonPathTypes.get(i).key);
            }
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return mPathToValues.size();
    }

    public void setPathToValues(ArrayList<PathToValue> pathToValues) {
        mPathToValues = pathToValues;
        if (mPathToValues != null) {
            Collections.sort(mPathToValues, new PathToValuesComparator());
            notifyDataSetChanged();
        }
    }

    public class PathToValuesComparator implements Comparator<PathToValue> {
        @Override
        public int compare(PathToValue o1, PathToValue o2) {
            return o1.name.compareTo(o2.name);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mOnPathToValueClickListener != null) {
                mOnPathToValueClickListener.onPathToValueClicked(mPathToValues.get(((ViewHolder) v.getTag()).mPosition));
            }
        }
    };

    public interface OnPathToValueClickListener {
        public void onPathToValueClicked(PathToValue pathToValue);
    }
}
