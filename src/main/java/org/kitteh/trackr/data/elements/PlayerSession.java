package org.kitteh.trackr.data.elements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.kitteh.trackr.Trackr;
import org.kitteh.trackr.data.DataType;
import org.kitteh.trackr.data.PersistentData;

/**
 * A player's time spent on the server
 */
public final class PlayerSession extends PersistentData {
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
        statement.setInt(1, (int) ((new Date().getTime() - this.start.getTime()) / 1000));
        statement.setInt(2, this.getID());
    }

    @Override
    protected String getIDStatementString() {
        return "SELECT `id` FROM `player_sessions` WHERE `player` = ? ORDER BY `id` DESC LIMIT 1;";
    }

    @Override
    protected String getInitStatementString() {
        return "INSERT INTO `player_sessions` (`server`,`player`,`start`) VALUES (?,?,?);";
    }

    @Override
    protected String getStatementString() {
        return "UPDATE `player_sessions` SET `duration` = ? WHERE `id` = ?;";
    }
}