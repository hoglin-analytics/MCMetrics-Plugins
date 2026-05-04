package net.mcmetrics.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.mcmetrics.common.command.CommandSender;

public class VelocityCommandSender implements CommandSender<CommandSource> {

    private final CommandSource sender;

    public VelocityCommandSender(CommandSource sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return sender instanceof ConsoleCommandSource;
    }

    @Override
    public CommandSource getPlatformSender() {
        return sender;
    }
}
