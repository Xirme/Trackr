package org.kitteh.trackr.data;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.kitteh.trackr.util.LCMap;

public class DataTracker {
    private final LCMap<Playr> players = new LCMap<Playr>(new ConcurrentHashMap<String, Playr>());

    /**
     * Get a Playr based on Player object
     * If the Playr does not exist, one is created
     * 
     * @param player
     *            Player object for the player
     * @return Playr
     */
    public Playr getPlayr(Player player) {
        return this.getPlayr(player.getName());
    }

    /**
     * Get a Playr based on Player object
     * If the Playr does not exist, one is created
     * 
     * @param name
     *            Name of the player
     * @return Playr
     */
    public Playr getPlayr(String name) {
        if (!this.players.containsKey(name)) {
            this.players.put(name, new Playr());
        }
        return this.players.get(name);
    }
}