package one.oktw.mixin.bungee;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.CHandshakePacket;
import one.oktw.ForgeProxyMixin;
import one.oktw.interfaces.HandshakeC2SPacketData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CHandshakePacket.class)
public abstract class HandshakeC2SPacketMixin implements HandshakeC2SPacketData {
    private String raw;

    @Override
    public String getRaw() {
        return this.raw;
    }

    @Redirect(method = "readPacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readString(I)Ljava/lang/String;"))
    private String onReadPacketData(PacketBuffer buf, int i) {
        if (!ForgeProxyMixin.config.getBungeeCord()) {
            return raw = buf.readString(255);
        }

        raw = buf.readString(Short.MAX_VALUE);

        return raw.split("\0")[0] + "\0" + "FML2" + "\0";
    }
}
