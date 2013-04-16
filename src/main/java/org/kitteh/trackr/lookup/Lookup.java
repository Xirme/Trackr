package org.kitteh.trackr.lookup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.kitteh.trackr.Trackr;

public abstract class Lookup {
    protected String[] toSend = null;
    private final List<String> receivers;

    public Lookup(Player... receivers) {
        this.receivers = new ArrayList<String>(receivers.length);
        for (Player player : receivers) {
            this.receivers.add(player.getName());
        }
    }

    /**
     * Creates the data tosend
     * 
     * @param connection
     * @throws SQLException 
     */
    public abstract void process(Connection connection) throws SQLException;

    public void send() {
        if (toSend != null) {
            for (String name : receivers) {
                Player player = Trackr.getInstance().getServer().getPlayerExact(name);
                if (player != null) {
                    player.sendMessage(toSend);
                }
            }
        }
    }
}