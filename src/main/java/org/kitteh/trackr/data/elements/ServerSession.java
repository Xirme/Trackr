package org.kitteh.trackr.data.elements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.kitteh.trackr.Trackr;
import org.kitteh.trackr.data.DataType;
import org.kitteh.trackr.data.PersistentData;

/**
 * Records the actual server's uptime.
 * Do not handle this normally. Handled by SQLManager.
 */
public final class ServerSession extends PersistentData {
    private final Timestamp start;
    private int maxPlayers = 0;

    public ServerSession() {
        super(DataType.SERVER_SESSION);
        this.start = new Timestamp(new Date().getTime());
    }

    @Override
    public void populateIDStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, Trackr.getServerName());
    }

    @Override
    public void populateInitStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, Trackr.getServerName());
        statement.setTimestamp(2, this.start);
    }

    @Override
    public void populateStatement(PreparedStatement statement) throws SQLException {
        statement.setInt(1, (int) ((new Date().getTime() - this.start.getTime()) / 1000));
        statement.setInt(2, maxPlayers);
        statement.setInt(3, this.getID());
    }

    public void playerUpdate(int current) {
        if (current > maxPlayers) {
            maxPlayers = current;
        }
    }

    @Override
    protected String getIDStatementString() {
        return "SELECT `id` FROM `server_sessions` WHERE `server` = ? ORDER BY `id` DESC LIMIT 1;";
    }

    @Override
    protected String getInitStatementString() {
        return "INSERT INTO `server_sessions` (`server`,`start`) VALUES (?,?);";
    }

    @Override
    protected String getStatementString() {
        return "UPDATE `server_sessions` SET `duration` = ?, `maxplayers` = ? WHERE `id` = ?;";
    }
}