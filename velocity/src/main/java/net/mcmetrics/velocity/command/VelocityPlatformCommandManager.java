package net.mcmetrics.velocity.command;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.velocitypowered.api.command.CommandSource;
import net.mcmetrics.common.command.CommandSender;
import net.mcmetrics.common.command.PlatformCommandManager;
import net.mcmetrics.velocity.MCMetricsPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.CloudInjectionModule;
import org.incendo.cloud.velocity.VelocityCommandManager;

public class VelocityPlatformCommandManager implements PlatformCommandManager {

    private final VelocityCommandManager<VelocityCommandSender> commandManager;
    private final AnnotationParser<VelocityCommandSender>  annotationParser;

    public VelocityPlatformCommandManager() {
        SenderMapper<CommandSource, VelocityCommandSender> senderMapper =
                SenderMapper.create(
                        VelocityCommandSender::new,
                        CommandSender::getPlatformSender
                );

        final Injector childInjector = MCMetricsPlugin.getInstance().getInjector().createChildInjector(
                new CloudInjectionModule<>(
                        VelocityCommandSender.class,
                        ExecutionCoordinator.simpleCoordinator(),
                        senderMapper
                )
        );

        this.commandManager = childInjector.getInstance(
                Key.get(new TypeLiteral<>() {
                })
        );

        this.annotationParser = new AnnotationParser<>(this.commandManager, VelocityCommandSender.class);
    }

    @Override
    public void registerCommands(Object commandHandler) {
        annotationParser.parse(commandHandler);
    }
}
