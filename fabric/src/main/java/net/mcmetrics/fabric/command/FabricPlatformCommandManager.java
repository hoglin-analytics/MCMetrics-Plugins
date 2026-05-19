package net.mcmetrics.fabric.command;

import net.mcmetrics.common.command.CommandSender;
import net.mcmetrics.common.command.PlatformCommandManager;
import net.minecraft.commands.CommandSourceStack;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.fabric.FabricServerCommandManager;

public class FabricPlatformCommandManager implements PlatformCommandManager {

    private final FabricServerCommandManager<FabricCommandSender> commandManager;
    private final AnnotationParser<FabricCommandSender> annotationParser;

    public FabricPlatformCommandManager() {
        SenderMapper<CommandSourceStack, FabricCommandSender> senderMapper =
                SenderMapper.create(
                        FabricCommandSender::new,
                        CommandSender::getPlatformSender
                );

        this.commandManager = new FabricServerCommandManager<>(
                ExecutionCoordinator.simpleCoordinator(),
                senderMapper
        );

        this.annotationParser = new AnnotationParser<>(this.commandManager, FabricCommandSender.class);
    }

    @Override
    public void registerCommands(Object commandHandler) {
        annotationParser.parse(commandHandler);
    }
}
