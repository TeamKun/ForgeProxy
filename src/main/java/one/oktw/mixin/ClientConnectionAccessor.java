package one.oktw.mixin;

import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@Mixin(NetworkManager.class)
public interface ClientConnectionAccessor {
    @Accessor("socketAddress")
    void setAddress(SocketAddress address);
}
