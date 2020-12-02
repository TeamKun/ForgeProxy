package one.oktw.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PreYggdrasilConverter;
import one.oktw.FabricProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PreYggdrasilConverter.class)
public class ServerConfigHandlerMixin {
    @Redirect(method = "lookupNames", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isServerInOnlineMode()Z"))
    private static boolean lookupProfile(MinecraftServer minecraftServer) {
        if (FabricProxy.config.getBungeeCord() || FabricProxy.config.getVelocity()) {
            return true;
        }

        return minecraftServer.isServerInOnlineMode();
    }
}
