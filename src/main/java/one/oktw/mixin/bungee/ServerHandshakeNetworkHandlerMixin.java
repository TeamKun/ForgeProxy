package one.oktw.mixin.bungee;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.ServerHandshakeNetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import one.oktw.interfaces.BungeeClientConnection;
import one.oktw.interfaces.HandshakeC2SPacketData;
import one.oktw.mixin.ClientConnectionAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static one.oktw.ForgeProxyMixin.config;

@Mixin(ServerHandshakeNetHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    private static final Gson gson = new Gson();

    @Shadow
    @Final
    private NetworkManager networkManager;

    @Inject(method = "processHandshake", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/login/ServerLoginNetHandler;<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/NetworkManager;)V"))
    private void onProcessHandshakeStart(CHandshakePacket packet, CallbackInfo ci) {
        if (config.getBungeeCord() && packet.getRequestedState().equals(ProtocolType.LOGIN)) {
            String[] split = ((HandshakeC2SPacketData) packet).getPayload().split("\00");
            if (split.length == 3 || split.length == 4) {
                ((ClientConnectionAccessor) networkManager).setAddress(new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) networkManager.getRemoteAddress()).getPort()));
                ((BungeeClientConnection) networkManager).setSpoofedUUID(UUIDTypeAdapter.fromString(split[2]));

                if (split.length == 4) {
                    ((BungeeClientConnection) networkManager).setSpoofedProfile(gson.fromJson(split[3], Property[].class));
                }
            } else {
                if (!config.getAllowBypassProxy()) {
                    ITextComponent disconnectMessage = new StringTextComponent("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
                    networkManager.sendPacket(new SDisconnectLoginPacket(disconnectMessage));
                    networkManager.closeChannel(disconnectMessage);
                }
            }
        }
    }
}
