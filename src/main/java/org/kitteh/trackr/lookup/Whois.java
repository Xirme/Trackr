package org.kitteh.trackr.lookup;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public final class Whois extends Lookup {
    private final String name;
    private final InetAddress address;
    private final KDR kdr;

    public Whois(Player who, Player... receivers) {
        super(receivers);
        this.name = who.getName();
        this.address = who.getAddress().getAddress();
        this.kdr = new KDR(who);
    }

    @Override
    public void process(Connection connection) throws SQLException {
        this.kdr.process(connection);
        final List<String> list = new ArrayList<String>();
        list.add("Player " + this.name);
        list.add("  IP: " + this.address.getHostAddress());
        list.add("  KDR: " + this.kdr.getKDR() + " (" + this.kdr.getKills() + " kills, " + this.kdr.getDeaths() + " deaths)");
        this.toSend = list.toArray(new String[0]);
    }
}