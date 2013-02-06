package org.kitteh.tracker.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.kitteh.tracker.Trackr;

public class Kill extends Data {
    private String killer;
    private String victim;
    private Timestamp timestamp;

    public Kill(String killer, String victim) {
        super(DataType.KILL);
        this.killer = killer;
        this.victim = victim;
        this.timestamp = new Timestamp(new Date().getTime());
    }

    @Override
    public void populateStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, Trackr.getServerName());
        statement.setString(2, this.killer);
        statement.setString(3, this.victim);
        statement.setTimestamp(4, this.timestamp);
    }

    @Override
    protected String getStatementString() {
        return "INSERT INTO `kills` (`server`,`killer`,`victim`,`timestamp`) VALUES (?,?,?,?);";
    }
}