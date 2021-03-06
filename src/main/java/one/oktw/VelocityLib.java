package one.oktw;

import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VelocityLib {
    public static final ResourceLocation PLAYER_INFO_CHANNEL = new ResourceLocation("velocity", "player_info");
    private static final int SUPPORTED_FORWARDING_VERSION = 1;

    public static boolean checkIntegrity(final PacketBuffer buf) {
        final byte[] signature = new byte[32];
        buf.readBytes(signature);

        final byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);

        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(ForgeProxyMixin.config.getSecret().getBytes(), "HmacSHA256"));
            final byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature)) {
                return false;
            }
        } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        int version = buf.readVarInt();
        if (version != SUPPORTED_FORWARDING_VERSION) {
            throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + SUPPORTED_FORWARDING_VERSION);
        }

        return true;
    }

    public static InetAddress readAddress(final PacketBuffer buf) {
        return InetAddresses.forString(buf.readString(Short.MAX_VALUE));
    }

    public static GameProfile createProfile(final PacketBuffer buf) {
        final GameProfile profile = new GameProfile(buf.readUniqueId(), buf.readString(16));
        readProperties(buf, profile);
        return profile;
    }

    private static void readProperties(final PacketBuffer buf, final GameProfile profile) {
        final int properties = buf.readVarInt();
        for (int i1 = 0; i1 < properties; i1++) {
            final String name = buf.readString(Short.MAX_VALUE);
            final String value = buf.readString(Short.MAX_VALUE);
            final String signature = buf.readBoolean() ? buf.readString(Short.MAX_VALUE) : null;
            profile.getProperties().put(name, new Property(name, value, signature));
        }
    }
}
