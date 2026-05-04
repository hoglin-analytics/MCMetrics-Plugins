package net.mcmetrics.bungee.command;

import net.mcmetrics.bungee.MCMetricsPlugin;
import net.mcmetrics.common.command.CommandSender;
import net.mcmetrics.common.command.PlatformCommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bungee.BungeeCommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;

public class BungeePlatformCommandManager implements PlatformCommandManager {

    private final BungeeCommandManager<BungeeCommandSender> commandManager;
    private final AnnotationParser<BungeeCommandSender>  annotationParser;

    public BungeePlatformCommandManager() {
        SenderMapper<net.md_5.bungee.api.CommandSender, BungeeCommandSender> senderMapper =
                SenderMapper.create(
                        BungeeCommandSender::new,
                        CommandSender::getPlatformSender
                );

        this.commandManager = new BungeeCommandManager<>(
                MCMetricsPlugin.getInstance(),
                ExecutionCoordinator.simpleCoordinator(),
                senderMapper
        );

        this.annotationParser = new AnnotationParser<>(this.commandManager, BungeeCommandSender.class);
    }

    @Override
    public void registerCommands(Object commandHandler) {
        annotationParser.parse(commandHandler);
    }
}
