package org.kitteh.tracker.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.kitteh.tracker.Trackr;

public class PlayerSession extends PersistentData {
    private final Timestamp start;
    private final String player;

    public PlayerSession(String player) {
        super(DataType.PLAYER_SESSION);
        this.player = player;
        this.start = new Timestamp(new Date().getTime());
    }

    @Override
    public void populateIDStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, this.player);
    }

    @Override
    public void populateInitStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, Trackr.getServerName());
        statement.setString(2, this.player);
        statement.setTimestamp(3, this.start);
    }

    @Override
    public void populateStatement(PreparedStatement statement) throws SQLException {
        statement.setInt(1, this.getID());
        statement.setLong(2, new Date().getTime() - this.start.getTime());
    }

    @Override
    protected String getIDStatementString() {
        return "SELECT `id` FROM `sessions` WHERE `player` = ? ORDER BY `id` DESC LIMIT 1;";
    }

    @Override
    protected String getInitStatementString() {
        return "INSERT INTO `sessions` (`server`,`player`,`start`) VALUES (?,?,?);";
    }

    @Override
    protected String getStatementString() {
        return "UPDATE `sessions` SET `duration` = ? WHERE `id` = ?;";
    }
}