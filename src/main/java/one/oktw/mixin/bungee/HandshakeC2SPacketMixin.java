package one.oktw.mixin.bungee;

import net.minecraft.network.handshake.client.CHandshakePacket;
import one.oktw.FabricProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CHandshakePacket.class)
public abstract class HandshakeC2SPacketMixin {
    @ModifyConstant(method = "readPacketData", constant = @Constant(intValue = 255))
    private int readStringSize(int i) {
        if (FabricProxy.config.getBungeeCord()) {
            return Short.MAX_VALUE;
        }

        return i;
    }
}
