package org.kitteh.trackr.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Data {
    private final DataType type;

    public Data(DataType type) {
        this.type = type;
    }

    protected abstract String getStatementString();

    public DataType getType() {
        return this.type;
    }

    public PreparedStatement getStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(this.getStatementString());
    }

    public abstract void populateStatement(PreparedStatement statement) throws SQLException;

}