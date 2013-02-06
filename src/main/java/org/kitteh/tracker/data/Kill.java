package org.kitteh.tracker.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.kitteh.tracker.Trackr;

public class Kill extends Data {
    private String killer;
    private String victim;

    public Kill(String killer, String victim) {
        super(DataType.KILL);
    }

    @Override
    public void populateStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, Trackr.getServerName());
        statement.setString(2, this.killer);
        statement.setString(3, this.victim);
    }

    @Override
    protected String getStatementString() {
        return "INSERT INTO `kills` (`server`,`killer`,`victim`) VALUES (?,?,?);";
    }
}