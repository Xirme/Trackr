package org.kitteh.trackr;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kitteh.trackr.data.DataType;
import org.kitteh.trackr.data.elements.Kill;
import org.kitteh.trackr.data.elements.PlayerSession;

/**
 * Self-registering listener of love
 */
public final class UberListener implements Listener {
    private final Trackr plugin;

    public UberListener(Trackr plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.playerJoin(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof Projectile) {
            LivingEntity shooter = ((Projectile) damager).getShooter();
            if (shooter != null) {
                this.plugin.getDamageTracker().add(damaged.getUniqueId(), Kill.typeOrName(shooter));
            } else {
                this.plugin.getDamageTracker().add(damaged.getUniqueId(), Kill.typeOrName(damager));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerDeath(EntityDeathEvent event) {
        this.plugin.getSQL().add(new Kill(event.getEntity().getKiller(), event.getEntity()));
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        final String name = event.getPlayer().getName();
        this.playerJoin(name);
    }

    private void playerJoin(String name) {
        final PlayerSession session = new PlayerSession(name);
        this.plugin.getDataTracker().getPlayr(name).setData(session);
        this.plugin.getSQL().add(session);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        this.plugin.getSQL().add(this.plugin.getDataTracker().getPlayr(event.getPlayer()).getData(DataType.PLAYER_SESSION));
    }
}