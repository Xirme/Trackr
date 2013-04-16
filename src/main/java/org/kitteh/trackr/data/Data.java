package org.kitteh.trackr.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Data {
    private final DataType type;

    public Data(DataType type) {
        this.type = type;
    }

    /**
     * Prepare the primary statement for this data
     * 
     * @param connection
     *            Connection on which this statement will run
     * @return PreparedStatement for this data
     * @throws SQLException
     */
    public PreparedStatement getStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(this.getStatementString());
    }

    /**
     * Get the DataType of this data
     * 
     * @return DataType
     */
    public DataType getType() {
        return this.type;
    }

    /**
     * Populate the statement with data
     * 
     * @param statement
     *            PreparedStatement for populating
     * @throws SQLException
     */
    public abstract void populateStatement(PreparedStatement statement) throws SQLException;

    /**
     * Get the SQL query String for this data's primary statement
     * 
     * @return SQL query String
     */
    protected abstract String getStatementString();
}