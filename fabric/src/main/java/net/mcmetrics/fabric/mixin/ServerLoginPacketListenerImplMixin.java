package net.mcmetrics.fabric.mixin;

import com.mojang.authlib.GameProfile;
import net.mcmetrics.fabric.MCMetricsMod;
import net.mcmetrics.fabric.event.PlayerLoginCallback;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListenerImplMixin {

    @Shadow
    @Final
    Connection connection;

    @Inject(method = "finishLoginAndWaitForClient", at = @At("HEAD"))
    private void interceptGameProfile(GameProfile gameProfile, CallbackInfo ci) {
        final String hostName = MCMetricsMod.getInstance().getHostnameStore().getAndRemove(this.connection);
        PlayerLoginCallback.EVENT.invoker().interact(gameProfile, this.connection, hostName);
    }
}
