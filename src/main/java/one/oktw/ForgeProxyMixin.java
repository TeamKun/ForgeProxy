package one.oktw;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class ForgeProxyMixin implements IMixinConfigPlugin {
    public static ForgeProxyConfig config;
    private final Logger logger = LogManager.getLogger("ForgeProxy");
    static final Marker CONFIG = MarkerManager.getMarker("CONFIG");

    @Override
    public void onLoad(String mixinPackage) {
        if (config == null) {
            config = new ForgeProxyConfig();
            Path configPath = FMLPaths.CONFIGDIR.get().resolve(String.format("%s-%s.toml", "forgeproxy", "mixin"));
            final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync()
                    .preserveInsertionOrder()
                    .autosave()
                    .onFileNotFound((newfile, configFormat) -> {
                        Files.createFile(newfile);
                        configFormat.initEmptyFile(newfile);
                        return true;
                    })
                    .writingMode(WritingMode.REPLACE)
                    .build();
            logger.debug(CONFIG, "Built TOML config for {}", configPath.toString());
            configData.load();
            logger.debug(CONFIG, "Loaded TOML config file {}", configPath.toString());
            config.spec.setConfig(configData);
        }
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
