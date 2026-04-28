package net.mcmetrics.fabric.mixin;

import net.fabricmc.loader.impl.lib.sat4j.minisat.core.CircularBuffer;
import net.mcmetrics.fabric.TpsUtils;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Unique
    private long startTime;

    @Inject(method = "tickServer", at = @At("HEAD"))
    public void tickServerStart(CallbackInfo ci) {
        startTime = System.nanoTime();
    }

    @Inject(method = "tickServer", at = @At("RETURN"))
    public void tickServerEnd(CallbackInfo ci) {
        long tickTime = System.nanoTime() - startTime;
        TpsUtils.mspt.push(tickTime);
    }
}
