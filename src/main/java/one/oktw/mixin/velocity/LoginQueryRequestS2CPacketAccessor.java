package one.oktw.mixin.velocity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SCustomPayloadLoginPacket.class)
public interface LoginQueryRequestS2CPacketAccessor {
    @Accessor("transaction")
    void setQueryId(int queryId);

    @Accessor("channel")
    void setChannel(ResourceLocation channel);

    @Accessor
    void setPayload(PacketBuffer payload);
}
