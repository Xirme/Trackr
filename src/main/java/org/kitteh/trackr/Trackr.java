package org.kitteh.trackr;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.trackr.data.DataTracker;
import org.kitteh.trackr.data.elements.ServerSession;
import org.kitteh.trackr.lookup.KDR;

public class Trackr extends JavaPlugin {
    private static String servername;
    private static Trackr instance;

    public static Trackr getInstance() {
        return instance;
    }

    public static String getServerName() {
        return Trackr.servername;
    }

    private SQLManager sql;
    private ServerSession session;
    private final DamageTracker damageTracker = new DamageTracker();
    private final DataTracker dataTracker = new DataTracker();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            this.sql.add(new KDR((Player) sender));
            sender.sendMessage(ChatColor.GREEN + "Looking up your query...");
        }
        return true;
    }

    @Override
    public void onDisable() {
        if (this.sql != null) {
            this.sql.shutdown();
            long start = System.currentTimeMillis();
            while (((System.currentTimeMillis() - start) < 10000) && !this.sql.isEmptied()) {
                // Whee
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        Trackr.servername = this.getConfig().getString("servername");
        final String host = this.getConfig().getString("mysql.host");
        final String database = this.getConfig().getString("mysql.database");
        final int port = this.getConfig().getInt("mysql.port");
        final String user = this.getConfig().getString("mysql.user");
        final String pass = this.getConfig().getString("mysql.pass");
        try {
            this.sql = new SQLManager(this, host, database, port, user, pass);
        } catch (ClassNotFoundException | SQLException | IOException e) {
            this.getLogger().log(Level.SEVERE, "Could not start up SQL: " + e.getMessage());
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.resetServerSession();
        new UberListener(this);
    }

    public DamageTracker getDamageTracker() {
        return this.damageTracker;
    }

    DataTracker getDataTracker() {
        return this.dataTracker;
    }

    SQLManager getSQL() {
        return this.sql;
    }

    public ServerSession getServerSession() {
        return session;
    }

    public void resetServerSession() {
        this.session = new ServerSession();
    }
}