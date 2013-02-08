package org.kitteh.trackr;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.trackr.data.DataTracker;

public class Trackr extends JavaPlugin {

    private static String servername;

    public static String getServerName() {
        return Trackr.servername;
    }

    private SQLManager sql;
    private final DataTracker tracker = new DataTracker();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    @Override
    public void onDisable() {
        if (this.sql != null) {
            this.sql.shutdown();
            while (!this.sql.isEmptied()) {
                // Whee
            }
        }
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Trackr.servername = this.getConfig().getString("servername");
        final String host = this.getConfig().getString("mysql.host");
        final String database = this.getConfig().getString("mysql.database");
        final int port = this.getConfig().getInt("mysql.port");
        final String user = this.getConfig().getString("mysql.user");
        final String pass = this.getConfig().getString("mysql.pass");
        try {
            this.sql = new SQLManager(this, host, database, port, user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            this.getLogger().log(Level.SEVERE, "Could not start up SQL", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        new UberListener(this);
    }

    DataTracker getDataTracker() {
        return this.tracker;
    }

    SQLManager getSQL() {
        return this.sql;
    }

}