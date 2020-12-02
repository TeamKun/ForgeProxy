package one.oktw.mixin.velocity;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import one.oktw.ForgeProxyMixin;
import one.oktw.VelocityLib;
import one.oktw.mixin.ClientConnectionAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.netty.buffer.Unpooled.EMPTY_BUFFER;

@Mixin(ServerLoginNetHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    private int velocityLoginQueryId = -1;
    private boolean ready = false;
    private boolean bypassProxy = false;
    private CLoginStartPacket loginPacket;

    @Shadow(aliases = "networkManager")
    @Final
    public NetworkManager connection;

    @Shadow(aliases = "loginGameProfile")
    private GameProfile profile;

    @Shadow(aliases = "tryAcceptPlayer")
    public abstract void acceptPlayer();

    @Shadow(aliases = "onDisconnect")
    public abstract void disconnect(ITextComponent text);

    @Shadow(aliases = "processLoginStart")
    public abstract void onHello(CLoginStartPacket loginHelloC2SPacket);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "processLoginStart",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/login/client/CLoginStartPacket;getProfile()Lcom/mojang/authlib/GameProfile;"),
            cancellable = true)
    private void sendVelocityPacket(CLoginStartPacket loginHelloC2SPacket, CallbackInfo ci) {
        if (ForgeProxyMixin.config.getVelocity() && !bypassProxy) {
            if (ForgeProxyMixin.config.getAllowBypassProxy()) {
                loginPacket = loginHelloC2SPacket;
            }
            this.velocityLoginQueryId = java.util.concurrent.ThreadLocalRandom.current().nextInt();
            SCustomPayloadLoginPacket packet = new SCustomPayloadLoginPacket();
            ((LoginQueryRequestS2CPacketAccessor) packet).setQueryId(velocityLoginQueryId);
            ((LoginQueryRequestS2CPacketAccessor) packet).setChannel(VelocityLib.PLAYER_INFO_CHANNEL);
            ((LoginQueryRequestS2CPacketAccessor) packet).setPayload(new PacketBuffer(EMPTY_BUFFER));

            connection.sendPacket(packet);
            ci.cancel();
        }
    }

    @Inject(method = "processEncryptionResponse", at = @At("HEAD"), cancellable = true)
    private void forwardPlayerInfo(CEncryptionResponsePacket packet, CallbackInfo ci) {
        if (ForgeProxyMixin.config.getVelocity() && ((LoginQueryResponseC2SPacketAccessor) packet).getQueryId() == velocityLoginQueryId) {
            PacketBuffer buf = ((LoginQueryResponseC2SPacketAccessor) packet).getResponse();
            if (buf == null) {
                if (!ForgeProxyMixin.config.getAllowBypassProxy()) {
                    disconnect(new StringTextComponent("This server requires you to connect with Velocity."));
                    return;
                }

                bypassProxy = true;
                onHello(loginPacket);
                ci.cancel();
                return;
            }

            if (!VelocityLib.checkIntegrity(buf)) {
                disconnect(new StringTextComponent("Unable to verify player details"));
                return;
            }

            ((ClientConnectionAccessor) connection).setAddress(new java.net.InetSocketAddress(VelocityLib.readAddress(buf), ((java.net.InetSocketAddress) connection.getRemoteAddress()).getPort()));

            profile = VelocityLib.createProfile(buf);

            ready = true;
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/network/login/ServerLoginNetHandler;connectionTimer:I"))
    private void login(CallbackInfo ci) {
        if (ready) {
            ready = false;
            acceptPlayer();
        }
    }
}
