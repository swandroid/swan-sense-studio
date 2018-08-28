package interdroid.swan.actuator.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import interdroid.swan.R;

/**
 * The adapter for the {@link RecyclerView} in the {@link AbstractActuatorActivity} that contains
 * the config parameters of the actuator.
 */
public class ConfigurationAdapter extends RecyclerView.Adapter<ConfigurationAdapter.ViewHolder> {

    private String[] keys;

    private String[] values;

    private OnClickListener listener;

    public ConfigurationAdapter(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_actuator_config,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.key.setText(keys[position]);
        holder.value.setText(values[position]);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(holder.getAdapterPosition(), keys[holder.getAdapterPosition()],
                            values[holder.getAdapterPosition()]);
                }
            }
        });

        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values[holder.getAdapterPosition()] = "";
                holder.value.setText("");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (keys == null || values == null) {
            return 0;
        }

        return Math.min(keys.length, values.length);
    }

    public int getNonNullCount() {
        int count = 0;

        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Replace the elements of the list
     *
     * @param keys   the keys of the actuator parameters
     * @param values the values of the actuator parameters
     */
    public void swap(String[] keys, String[] values) {
        this.keys = keys;
        this.values = values;
    }

    /**
     * Set the value of a certain actuator parameter
     *
     * @param key   the key of the actuator parameter
     * @param value the value of the actuator parameter
     */
    public void swap(int key, String value) {
        values[key] = value;
    }

    /**
     * Get the key of an actuator parameter in the list
     *
     * @param position the position in the list
     * @return key of the parameter
     */
    public String getKey(int position) {
        return keys[position];
    }

    /**
     * Get the value of an actuator parameter in the list
     *
     * @param position the position in the list
     * @return value of the parameter
     */
    public String getValue(int position) {
        return values[position];
    }

    /**
     * A listener that will be notified when an item is clicked in on the {@link ConfigurationAdapter}.
     */
    public static interface OnClickListener {

        /**
         * Called when an item is clicked on.
         *
         * @param position the position of the item
         * @param key      the key of the parameter
         * @param value    the value of the parameter
         */
        void onClick(int position, String key, String value);
    }

    /**
     * View holder for {@link ConfigurationAdapter}
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        public View v;

        public TextView key;
        public TextView value;

        public Button clear;

        public ViewHolder(View v) {
            super(v);

            this.v = v;

            key = (TextView) v.findViewById(R.id.key);
            value = (TextView) v.findViewById(R.id.value);
            clear = (Button) v.findViewById(R.id.clear);
        }
    }
}
