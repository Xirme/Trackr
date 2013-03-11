package org.kitteh.trackr.lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class KDR extends Lookup {

    private final String name;

    public KDR(Player player) {
        super(player);
        this.name = player.getName();
    }

    @Override
    public void process(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT count(`id`) FROM `kills` WHERE victim=?");
        statement.setString(1, "PLAYER:" + name);
        ResultSet result = statement.executeQuery();
        double deaths = 0;
        if (result.first()) {
            deaths = result.getInt(1);
        }
        statement = connection.prepareStatement("SELECT count(`id`) FROM `kills` WHERE killer=?");
        statement.setString(1, "PLAYER:" + name);
        result = statement.executeQuery();
        double kills = 0;
        if (result.first()) {
            kills = result.getInt(1);
        }
        double kdr = kills / deaths;
        String kdrString = String.valueOf(kdr);
        if (kdrString.length() > 5) {
            kdrString = kdrString.substring(0, 5);
        }
        this.toSend = new String[] { "Your KDR is " + kdrString };
    }
}