package net.mcmetrics.bukkit.command;

import net.mcmetrics.bukkit.MCMetricsPlugin;
import net.mcmetrics.common.command.CommandSender;
import net.mcmetrics.common.command.PlatformCommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class BukkitPlatformCommandManager implements PlatformCommandManager {

    private final LegacyPaperCommandManager<BukkitCommandSender> commandManager;
    private final AnnotationParser<BukkitCommandSender>  annotationParser;

    public BukkitPlatformCommandManager() {
        SenderMapper<org.bukkit.command.CommandSender, BukkitCommandSender> senderMapper =
                SenderMapper.create(
                        BukkitCommandSender::new,
                        CommandSender::getPlatformSender
                );

        this.commandManager = new LegacyPaperCommandManager<>(
                MCMetricsPlugin.getInstance(),
                ExecutionCoordinator.simpleCoordinator(),
                senderMapper
        );

        if (this.commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.commandManager.registerBrigadier();
        } else if (this.commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.commandManager.registerAsynchronousCompletions();
        }

        this.annotationParser = new AnnotationParser<>(this.commandManager, BukkitCommandSender.class);
    }

    @Override
    public void registerCommands(Object commandHandler) {
        annotationParser.parse(commandHandler);
    }
}
