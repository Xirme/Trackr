package org.kitteh.trackr;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kitteh.trackr.data.DataTracker;
import org.kitteh.trackr.data.DataType;
import org.kitteh.trackr.data.elements.Kill;
import org.kitteh.trackr.data.elements.PlayerSession;

public class UberListener implements Listener {
    private final DataTracker tracker;
    private final SQLManager sql;

    public UberListener(Trackr plugin) {
        this.tracker = plugin.getDataTracker();
        this.sql = plugin.getSQL();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerDeath(PlayerDeathEvent event) {
        final Player victim = event.getEntity();
        final LivingEntity leKiller = victim.getKiller();
        if (leKiller instanceof Player) {
            final Player killer = (Player) leKiller;
            this.sql.add(new Kill(killer.getName(), victim.getName()));
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        final String name = event.getPlayer().getName();
        final PlayerSession session = new PlayerSession(name);
        this.tracker.getPlayr(name).setData(session);
        this.sql.add(session);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        this.sql.add(this.tracker.getPlayr(event.getPlayer()).getData(DataType.PLAYER_SESSION));
    }
}