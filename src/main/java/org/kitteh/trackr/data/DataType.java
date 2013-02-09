package org.kitteh.trackr.data;

public enum DataType {

    KILL,
    PLAYER_SESSION(true),
    SERVER_SESSION(true);
    private boolean persistent;

    private DataType() {
        this(false);
    }

    private DataType(boolean persistent) {
        this.persistent = persistent;
    }

    /**
     * Gets if this data type is persistent
     * 
     * @return true if persistent
     */
    public boolean isPersistent() {
        return this.persistent;
    }
}