package org.kitteh.tracker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.kitteh.tracker.data.Kill;

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
}