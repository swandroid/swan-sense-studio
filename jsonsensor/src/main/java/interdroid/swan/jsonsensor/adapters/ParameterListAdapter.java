package interdroid.swan.jsonsensor.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import interdroid.swan.jsonsensor.R;
import interdroid.swan.jsonsensor.pojos.Parameter;

import java.util.ArrayList;

/**
 * Created by steven on 21/10/14.
 */
public class ParameterListAdapter extends RecyclerView.Adapter<ParameterListAdapter.ViewHolder> {

    private ArrayList<Parameter> mParameters;
    private OnParameterClickListener mOnParameterClickListener;

    public ParameterListAdapter(OnParameterClickListener onParameterClickListener) {
        mParameters = new ArrayList<Parameter>();
        mOnParameterClickListener = onParameterClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mRoot;
        public TextView mName;
        public TextView mValue;
        public int mPosition;
        public ViewHolder(View view) {
            super(view);
            mRoot = view;
            mName = (TextView) mRoot.findViewById(R.id.item_param_name);
            mValue = (TextView) mRoot.findViewById(R.id.item_param_value);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_param, parent, false);

        // set the view's size, margins, paddings and layout parameters

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        view.setOnClickListener(mOnClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Parameter parameter = mParameters.get(i);

        viewHolder.mName.setText(parameter.name);
        viewHolder.mValue.setText(parameter.value);
        viewHolder.mPosition = i;

    }

    @Override
    public int getItemCount() {
        return mParameters.size();
    }

    public void addParameter(Parameter parameter) {
        mParameters.add(parameter);
        notifyDataSetChanged();
    }

    public void setParameters(ArrayList<Parameter> parameters) {
        mParameters = parameters;
        notifyDataSetChanged();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mOnParameterClickListener != null) {
                int position = ((ViewHolder) v.getTag()).mPosition;
                mOnParameterClickListener.onParameter(mParameters.get(position));
            }
        }
    };

    public interface OnParameterClickListener {
        public void onParameter(Parameter parameter);
    }

    public ArrayList<Parameter> getParamterList() {
        return mParameters;
    }
}
