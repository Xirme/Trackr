package org.kitteh.tracker.data;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.kitteh.tracker.util.LCMap;

public class DataTracker {
    private final LCMap<Playr> players = new LCMap<Playr>(new ConcurrentHashMap<String, Playr>());

    public Playr getPlayr(Player player) {
        return this.getPlayr(player.getName());
    }

    public Playr getPlayr(String name) {
        if (!this.players.containsKey(name)) {
            this.players.put(name, new Playr());
        }
        return this.players.get(name);
    }
}