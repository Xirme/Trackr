package org.kitteh.tracker.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A data class that is maintained throughout the session
 */
public abstract class PersistentData extends Data {

    private boolean sessionStarted = false;
    private int id;

    public PersistentData(DataType type) {
        super(type);
    }

    public PreparedStatement getInit(Connection connection) throws SQLException {
        return connection.prepareStatement(this.getInitStatementString());
    }

    public PreparedStatement getIDStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(this.getIDStatementString());
    }

    protected abstract String getIDStatementString();

    protected abstract String getInitStatementString();

    public abstract void populateIDStatement(PreparedStatement statement) throws SQLException;

    protected int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public boolean isStarted() {
        return this.sessionStarted;
    }

    public void setStarted() {
        this.sessionStarted = true;
    }

    public abstract void populateInitStatement(PreparedStatement statement) throws SQLException;

}