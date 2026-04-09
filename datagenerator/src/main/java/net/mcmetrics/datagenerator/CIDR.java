package net.mcmetrics.datagenerator;

import java.util.Random;

/**
 * NGL I hate working on the bit level, so I let Copilot write this part
 */
public class CIDR {

    private final int start;
    private final int size;

    public CIDR(String cidr) {
        String[] parts =  cidr.split("/");
        int base = ipToInt(parts[0]);
        int prefix = Integer.parseInt(parts[1]);

        int mask = prefix == 0 ? 0 : (-1 << (32 - prefix));
        int network = base & mask;
        int broadcast = network | ~mask;

        int s = network + 1;
        int e = broadcast - 1;

        if (s > e) {
            s = network;
            e = broadcast;
        }

        this.start = s;
        this.size = e - s + 1;
    }

    private int ipToInt(String ip) {
        String[] parts = ip.split("\\.");
        int val = 0;
        for (String part : parts) {
            val = (val << 8) | Integer.parseInt(part);
        }
        return val;
    }

    private String intToIp(int val) {
        return ((val >>> 24) & 0xFF) + "." +
                ((val >>> 16) & 0xFF) + "." +
                ((val >>> 8) & 0xFF) + "." +
                (val & 0xFF);
    }

    public String sample(Random random) {
        int val = start + random.nextInt(size);
        return intToIp(val);
    }
}
