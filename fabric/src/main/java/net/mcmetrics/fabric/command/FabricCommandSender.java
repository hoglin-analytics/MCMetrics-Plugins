package net.mcmetrics.fabric.command;

import net.mcmetrics.common.command.CommandSender;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class FabricCommandSender implements CommandSender<CommandSourceStack> {

    private final CommandSourceStack sender;

    public FabricCommandSender(CommandSourceStack sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendSuccess(() -> Component.literal(message), false);
    }

    @Override
    public boolean hasPermission(String permission) {
        // Uhhh im not entirely sure how to handle this one
        return false;
    }

    @Override
    public boolean isConsole() {
        return sender.getEntity() == null;
    }

    @Override
    public CommandSourceStack getPlatformSender() {
        return sender;
    }
}
