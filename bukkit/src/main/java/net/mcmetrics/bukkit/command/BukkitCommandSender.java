package net.mcmetrics.bukkit.command;

import net.mcmetrics.common.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class BukkitCommandSender implements CommandSender<org.bukkit.command.CommandSender> {

    private final org.bukkit.command.CommandSender sender;

    public BukkitCommandSender(org.bukkit.command.CommandSender sender) {
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
        return sender instanceof ConsoleCommandSender;
    }

    @Override
    public org.bukkit.command.CommandSender getPlatformSender() {
        return sender;
    }
}
