package net.mcmetrics.common.command;

public interface CommandSender<C> {

    void sendMessage(String message);

    boolean hasPermission(String permission);

    boolean isConsole();

    C getPlatformSender();
}
