package one.oktw.mixin.bungee;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import one.oktw.ForgeProxyMixin;
import one.oktw.interfaces.BungeeClientConnection;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    private boolean bypassProxy = false;
    @Shadow
    @Final
    public NetworkManager networkManager;
    @Shadow
    private GameProfile loginGameProfile;

    @Inject(method = "processLoginStart", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/network/login/ServerLoginNetHandler;loginGameProfile:Lcom/mojang/authlib/GameProfile;", shift = At.Shift.AFTER))
    private void initUuid(CallbackInfo ci) {
        if (ForgeProxyMixin.config.getBungeeCord()) {
            if (((BungeeClientConnection) networkManager).getSpoofedUUID() == null) {
                bypassProxy = true;
                return;
            }

            this.loginGameProfile = new GameProfile(((BungeeClientConnection) networkManager).getSpoofedUUID(), this.loginGameProfile.getName());

            if (((BungeeClientConnection) networkManager).getSpoofedProfile() != null) {
                for (Property property : ((BungeeClientConnection) networkManager).getSpoofedProfile()) {
                    this.loginGameProfile.getProperties().put(property.getName(), property);
                }
            }
        }
    }

    @Redirect(method = "processLoginStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isServerInOnlineMode()Z"))
    private boolean skipKeyPacket(MinecraftServer minecraftServer) {
        return (bypassProxy || !ForgeProxyMixin.config.getBungeeCord()) && minecraftServer.isServerInOnlineMode();
    }
}
