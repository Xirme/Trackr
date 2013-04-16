package org.kitteh.trackr.lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public final class KDR extends Lookup {
    private final String name;
    private int kills;
    private int deaths;
    private double kdr;

    public KDR(Player player, Player... receivers) {
        super(receivers);
        this.name = player.getName();
    }

    public int getDeaths() {
        return this.deaths;
    }

    public double getKDR() {
        return this.kdr;
    }

    public int getKills() {
        return this.kills;
    }

    @Override
    public void process(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT count(`id`) FROM `kills` WHERE victim=?");
        statement.setString(1, "PLAYER:" + this.name);
        ResultSet result = statement.executeQuery();
        double deaths = 0;
        if (result.first()) {
            deaths = result.getInt(1);
            this.deaths = (int) deaths;
        }
        statement = connection.prepareStatement("SELECT count(`id`) FROM `kills` WHERE killer=?");
        statement.setString(1, "PLAYER:" + this.name);
        result = statement.executeQuery();
        double kills = 0;
        if (result.first()) {
            kills = result.getInt(1);
            this.kills = (int) kills;
        }
        this.kdr = kills / deaths;
        String kdrString = String.valueOf(this.kdr);
        if (kdrString.length() > 5) {
            kdrString = kdrString.substring(0, 5);
        }
        this.toSend = new String[] { this.name + "'s KDR is " + kdrString + " (" + this.kills + " kills, " + this.deaths + " deaths)" };
    }
}