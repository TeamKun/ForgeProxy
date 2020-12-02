package one.oktw.mixin.velocity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CCustomPayloadLoginPacket.class)
public interface LoginQueryResponseC2SPacketAccessor {
    @Accessor("transaction")
    int getQueryId();

    @Accessor("payload")
    PacketBuffer getResponse();
}
