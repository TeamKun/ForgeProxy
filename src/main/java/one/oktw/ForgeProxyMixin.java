package one.oktw;

import one.oktw.config.ForgeProxyConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ForgeProxyMixin implements IMixinConfigPlugin {
    public static ForgeProxyConfig config = new ForgeProxyConfig() {
    };
    private final Logger logger = LogManager.getLogger("FabricProxy");

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String module = mixinClassName.split("\\.")[3];
        if (module.equals("bungee") && config.getBungeeCord()) {
            logger.info("BungeeCord support injected: {}", mixinClassName);
            return true;
        }

        if (module.equals("velocity") && config.getVelocity()) {
            if (config.getSecret().isEmpty()) {
                logger.error("Error: velocity secret is empty!");
            } else {
                logger.info("Velocity support injected: {}", mixinClassName);
                return true;
            }
        }

        return false;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
