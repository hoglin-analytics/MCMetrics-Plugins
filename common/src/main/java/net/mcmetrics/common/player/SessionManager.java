package net.mcmetrics.common.player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<UUID, TrackedPlayer> players = new ConcurrentHashMap<>();

    public TrackedPlayer addPlayer(final UUID uuid) {
        final TrackedPlayer player = new TrackedPlayer();
        players.put(uuid, player);
        return player;
    }

    public TrackedPlayer getPlayer(final UUID uuid) {
        return players.get(uuid);
    }

    public void removePlayer(final UUID uuid) {
        players.remove(uuid);
    }

    public List<TrackedPlayer> getAllPlayers() {
        return List.copyOf(players.values());
    }

}
