package net.mcmetrics.datagenerator;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import gg.hoglin.sdk.models.analytic.RecordedAnalytic;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.mcmetrics.common.analytic.ServerPlayerCountAnalytic;
import net.mcmetrics.common.analytic.player.PlayerChatAnalytic;
import net.mcmetrics.common.analytic.player.PlayerJoinAnalytic;
import net.mcmetrics.common.analytic.player.PlayerPurchaseAnalytic;
import net.mcmetrics.common.analytic.player.PlayerQuitAnalytic;
import net.mcmetrics.common.platform.ClientPlatform;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static net.mcmetrics.datagenerator.Constants.*;
import static net.mcmetrics.datagenerator.DataGenerator.EventType.*;

/**
 * <p>Adapted from a private Jupyter notebook we used for generating synthetic data</p>
 *
 * <p>A data generator as an internal tool for testing and experimentation.
 * Methodology revolves around <a href="https://en.wikipedia.org/wiki/Poisson_point_process">Poisson processes</a></p>
 */
public class DataGenerator {

    private final Random random;
    private final PriorityQueue<Event> queue = new PriorityQueue<>(Comparator.comparingLong(event -> event.timestamp));

    /** Random seed */
    public DataGenerator() {
        this.random = new Random();
    }

    /** Set seed */
    public DataGenerator(int seed) {
        this.random = new Random(seed);
    }

    /**
     * Runs the simulation for the configured time horizon
     *
     * @return list of recorded analytics
     */
    public List<RecordedAnalytic<? extends NamedAnalytic>> runSimulation() {
        Instant start = Instant.now().minus(HORIZON, ChronoUnit.MINUTES);
        List<RecordedAnalytic<? extends NamedAnalytic>> events = new ArrayList<>();

        Set<UUID> players = new HashSet<>();
        Set<UUID> javaPlayers = new HashSet<>();
        Map<UUID, PlayerSession> sessions = new HashMap<>();
        CIDR[] cidrs = Arrays.stream(IP_RANGES).map(CIDR::new).toArray(CIDR[]::new);

        // Add eden events
        queueEventExp(0, JOIN, MU_JOIN);
        queueEventExp(0, CHAT, MU_CHAT);
        queueEventExp(0, PURCHASE, MU_PURCHASE);
        queueEventDeterministic(0, COUNT, C_PLAYER_COUNT);

        while (!queue.isEmpty()) {
            Event event = queue.poll();
            long t = event.timestamp;
            start = start.plus(t, ChronoUnit.MINUTES);

            if (t > HORIZON) break;

            // Join/Quit event
            if (event.type == JOIN) {
                queueEventExp(t, JOIN, MU_JOIN);

                boolean isNew = false;
                UUID uuid = null;

                // If no players online, then event must be join event
                if (sessions.isEmpty() || random.nextDouble() < P_JOIN) {
                    // Roll for new player, or force new player if no players exist yet
                    if (players.isEmpty() || random.nextDouble() < P_NEW_PLAYER) {
                        uuid = UUID.randomUUID();
                        players.add(uuid);
                        isNew = true;
                    } else {
                        // Sample from random offline player
                        Set<UUID> offlinePlayers = setDifference(players, sessions.keySet());

                        // If no offline players, force new player
                        if (offlinePlayers.isEmpty()) {
                            uuid = UUID.randomUUID();
                            players.add(uuid);
                            isNew = true;
                        } else {
                            uuid = getRandomElement(offlinePlayers);
                        }
                    }

                    boolean isJava = random.nextDouble() < P_JAVA_PLAYER;
                    if (isJava) javaPlayers.add(uuid);
                    String hostname = getRandomElement(HOSTNAMES);
                    String instance = getRandomElement(INSTANCES);
                    String ip = sampleIP(cidrs);
                    PlayerSession session = new PlayerSession(UUID.randomUUID(), t, hostname, ip, isNew, instance, isJava);
                    sessions.put(uuid, session);

                    // Add event
                    PlayerJoinAnalytic analytic = new PlayerJoinAnalytic(
                            instance,
                            session.id.toString(),
                            uuid,
                            hostname,
                            ip,
                            isJava ? ClientPlatform.JAVA : ClientPlatform.BEDROCK,
                            isNew);
                    events.add(new RecordedAnalytic<>("player_join", start, analytic));
                } else {
                    // Player quiting
                    uuid = getRandomElement(sessions.keySet());
                    PlayerSession playerSession = sessions.get(uuid);
                    sessions.remove(uuid);
                    javaPlayers.remove(uuid);

                    PlayerQuitAnalytic analytic = new PlayerQuitAnalytic(
                            playerSession.instance,
                            playerSession.id.toString(),
                            uuid,
                            playerSession.hostname,
                            playerSession.ip,
                            playerSession.isJava ? ClientPlatform.JAVA : ClientPlatform.BEDROCK,
                            (t - playerSession.startTime) * 60 * 1000
                    );
                    events.add(new RecordedAnalytic<>("player_quit", start, analytic));
                }
            }

            // Chat event
            else if (event.type == CHAT) {
                queueEventExp(t, CHAT, MU_CHAT);
                if (!sessions.isEmpty()) {
                    UUID uuid = getRandomElement(sessions.keySet());
                    boolean isToxic = random.nextDouble() < P_TOXICITY;

                    // Add event
                    PlayerSession session = sessions.get(uuid);
                    PlayerChatAnalytic analytic = new PlayerChatAnalytic(session.instance, uuid, "Lorem ipsum", isToxic);
                    events.add(new RecordedAnalytic<>("player_chat", start, analytic));
                }
            }

            // Purchase event
            else if (event.type == PURCHASE) {
                queueEventExp(t, PURCHASE, MU_PURCHASE);
                if (!players.isEmpty()) {
                    UUID uuid = getRandomElement(players);
                    String instance = getRandomElement(INSTANCES);
                    String pkg = getRandomElement(PACKAGES);
                    double price = getRandomElement(PURCHASE_PRICES);
                    PlayerPurchaseAnalytic analytic =  new PlayerPurchaseAnalytic(instance, uuid, pkg, CURRENCY, price);
                    events.add(new RecordedAnalytic<>("player_purchase", start, analytic));
                }
            }

            // Player count packet
            else if (event.type == COUNT) {
                queueEventDeterministic(t, COUNT, C_PLAYER_COUNT);

                for (String instance : INSTANCES) {
                    List<UUID> uuids = sessions.entrySet().stream().filter(entry -> entry.getValue().instance.equalsIgnoreCase(instance)).map(Map.Entry::getKey).toList();
                    int totalPlayers = uuids.size();
                    int totalJavaPlayers = javaPlayers.stream().filter(uuids::contains).toList().size();
                    int totalBedrockPlayers = totalPlayers - totalJavaPlayers;
                    ServerPlayerCountAnalytic analytic = new ServerPlayerCountAnalytic(instance, totalJavaPlayers, totalBedrockPlayers, totalPlayers);
                    events.add(new RecordedAnalytic<>("server_player_count", start, analytic));
                }
            }
        }

        return events;
    }

    /**
     * Queues an event to the priority queue at a randomly sampled future time
     *
     * @param curTime current time in the process
     * @param type event type
     * @param avgTime average interarrival time
     */
    private void queueEventExp(long curTime, EventType type, long avgTime) {
        double u = random.nextDouble();
        long next = (long) (curTime - avgTime * Math.log(u));

        Event e = new Event(next, type);
        queue.add(e);
    }

    /**
     * Queues an event to the priority queue at a deterministic future time
     *
     * @param curTime current time in the process
     * @param type event type
     * @param interval interarrival time
     */
    private void queueEventDeterministic(long curTime, EventType type, long interval) {
        long next = curTime + interval;
        Event e = new Event(next, type);
        queue.add(e);
    }

    /**
     * Gets random element from an array
     *
     * @param array array to sample from
     * @return random element from array
     */
    private <T> T getRandomElement(T[] array) {
        return  array[random.nextInt(array.length)];
    }

    /**
     * Gets random element from a set
     *
     * @param set set to sample from
     * @return random element from set
     */
    private <T> T getRandomElement(Set<T> set) {
        T[] array = (T[]) set.toArray();
        return getRandomElement(array);
    }

    /**
     * Does a set difference operation
     *
     * @param set1 left side set
     * @param set2 right side set
     * @return difference/intersection of set
     */
    private <T> Set<T> setDifference(Set<T> set1, Set<T> set2) {
        Set<T> set = new HashSet<>(set1);
        set.removeAll(set2);
        return set;
    }

    private String sampleIP(CIDR[] cidrs) {
        CIDR cidr = getRandomElement(cidrs);
        return cidr.sample(random);
    }

    @Data
    @AllArgsConstructor
    class Event {
        long timestamp;
        EventType type;
    }

    @Data
    @AllArgsConstructor
    class PlayerSession {
        UUID id;
        long startTime;
        String hostname;
        String ip;
        boolean isNew;
        String instance;
        boolean isJava;
    }

    enum EventType {
        JOIN,
        CHAT,
        PURCHASE,
        COUNT
    }
}
