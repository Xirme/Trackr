package org.kitteh.trackr;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DamageTracker {
    private Map<UUID, String> map = new HashMap<UUID, String>();

    public void add(UUID hurt, String by) {
        map.put(hurt, by);
    }

    public String get(UUID hurt) {
        return map.get(hurt);
    }
}