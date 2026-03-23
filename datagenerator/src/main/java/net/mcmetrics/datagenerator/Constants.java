package net.mcmetrics.datagenerator;

public class Constants {

    public static final long HORIZON = 30 * 24 * 60;  // Length of the event horizon in minutes

    public static final long MU_JOIN = 5;  // Average time between join/leaves in minutes

    public static final long MU_CHAT = 1;  // Average time between chat messages in minutes

    public static final long MU_PURCHASE = 6 * 60;  // Average time between purchases in minutes

    public static final long C_PLAYER_COUNT = 1;  // Deterministic time for player count packet in minutes

    public static final double P_JOIN = 0.55;  // Probability of joining rather than leaving

    public static final double P_NEW_PLAYER = 0.2;  // Probability that joining is a new player

    public static final double P_JAVA_PLAYER = 0.7;  // Probability that player joining is Java rather than Bedrock

    public static final double P_TOXICITY = 0.1;  // Probability that chat message is toxic

    public static final String CURRENCY = "USD";  // Currency for purchases

    public static final String[] HOSTNAMES = {"hub.example.com", "survival.example.com", "idk.example.com"};  // Possible hostnames to sample

    public static final String[] INSTANCES = {"hub", "survival", "creative", "darien land"};  // Possible instance names to sample

    public static final String[] IP_RANGES = {"8.0.0.0/8", "31.0.0.0/8", "101.0.0.0/8"};  // Possible IP ranges to sample

    public static final String[] PACKAGES = {"Bundle", "Super Bundle", "Super Bundle Deluxe"};  // Possible packages

    public static final Double[] PURCHASE_PRICES = {0.99, 1.99, 2.99};  // Possible purchase prices
}
