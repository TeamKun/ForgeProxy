package one.oktw.mixin.bungee;

import net.minecraft.network.handshake.client.CHandshakePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CHandshakePacket.class)
public interface HandshakeC2SPacketAccessor {
    @Accessor("ip")
    String getAddress();
}
