package org.kitteh.trackr.data;

import java.util.EnumMap;
import java.util.Map;

public final class Playr {
    private final Map<DataType, PersistentData> data = new EnumMap<DataType, PersistentData>(DataType.class);

    /**
     * Acquire existing persistent data, for purposes of updating it
     * 
     * @param type
     *            Type to acquire
     * @return
     */
    public synchronized PersistentData getData(DataType type) {
        return this.data.get(type);
    }

    /**
     * Mo' data mo' problems
     * 
     * @param data
     *            Data to set
     */
    public synchronized void setData(PersistentData data) {
        this.data.put(data.getType(), data);
    }
}