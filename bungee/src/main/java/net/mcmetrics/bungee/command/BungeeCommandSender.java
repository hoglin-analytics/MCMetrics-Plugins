package net.mcmetrics.bungee.command;

import net.mcmetrics.common.command.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public class BungeeCommandSender implements CommandSender<net.md_5.bungee.api.CommandSender> {

    private final net.md_5.bungee.api.CommandSender sender;

    public BungeeCommandSender(net.md_5.bungee.api.CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return sender.equals(ProxyServer.getInstance().getConsole());
    }

    @Override
    public net.md_5.bungee.api.CommandSender getPlatformSender() {
        return sender;
    }
}
