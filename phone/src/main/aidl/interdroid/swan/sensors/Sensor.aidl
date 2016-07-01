package interdroid.swan.sensors;

import interdroid.swancore.swansong.TimestampedValue;

interface Sensor {

	void register(in String id, in String valuePath,
	              in Bundle configuration, in Bundle httpConfiguration,
	              in Bundle extraConfiguration);

	void unregister(in String id);

	List<TimestampedValue> getValues(in String id, long now, long timespan);

	long getStartUpTime(in String id);
	
	Bundle getInfo();
}

