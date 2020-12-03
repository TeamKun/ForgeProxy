package one.oktw;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import one.oktw.config.ForgeProxyConfigImpl;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("forgeproxy")
public class ForgeProxy {

    public ForgeProxy() {
        ForgeProxyConfigImpl config = new ForgeProxyConfigImpl();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.spec);
        ForgeProxyMixin.config = config;
    }

}
