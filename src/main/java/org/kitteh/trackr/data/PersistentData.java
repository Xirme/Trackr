package org.kitteh.trackr.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A data class that is maintained throughout the session
 * Init statement creates the record
 * getID statement gets the ID for updating
 * the statement (from Data) is for updating the record
 */
public abstract class PersistentData extends Data {

    private boolean sessionStarted = false;
    private int id;

    public PersistentData(DataType type) {
        super(type);
    }

    public int getID() {
        return this.id;
    }

    /**
     * Prepare the getID statement for this data
     * 
     * @param connection
     *            Connection on which this statement will run
     * @return PreparedStatement for this data
     * @throws SQLException
     */
    public PreparedStatement getIDStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(this.getIDStatementString());
    }

    /**
     * Prepare the init statement for this data
     * 
     * @param connection
     *            Connection on which this statement will run
     * @return PreparedStatement for this data
     * @throws SQLException
     */
    public PreparedStatement getInitStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(this.getInitStatementString());
    }

    public boolean isStarted() {
        return this.sessionStarted;
    }

    /**
     * Populate the statement with data
     * 
     * @param statement
     *            PreparedStatement for populating
     * @throws SQLException
     */
    public abstract void populateIDStatement(PreparedStatement statement) throws SQLException;

    /**
     * Populate the statement with data
     * 
     * @param statement
     *            PreparedStatement for populating
     * @throws SQLException
     */
    public abstract void populateInitStatement(PreparedStatement statement) throws SQLException;

    public void setID(int id) {
        this.id = id;
    }

    public void setStarted() {
        this.sessionStarted = true;
    }

    /**
     * Get the SQL query String for this data's getID statement
     * 
     * @return SQL query String
     */
    protected abstract String getIDStatementString();

    /**
     * Get the SQL query String for this data's init statement
     * 
     * @return SQL query String
     */
    protected abstract String getInitStatementString();

}