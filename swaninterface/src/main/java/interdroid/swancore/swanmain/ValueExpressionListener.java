package interdroid.swancore.swanmain;

import interdroid.swancore.swansong.HistoryReductionMode;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.ValueExpression;

public interface ValueExpressionListener {

    /**
     * This method will be invoked when a {@link ValueExpression} produces new
     * values. Depending on the {@link HistoryReductionMode} the array with new
     * values can have a single value or multiple values.
     *
     * @param id
     * @param newValues
     */
    public void onNewValues(String id, TimestampedValue[] newValues);

}
