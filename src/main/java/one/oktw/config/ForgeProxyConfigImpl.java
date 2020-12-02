package one.oktw.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ForgeProxyConfigImpl extends ForgeProxyConfig {
    private final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private final ForgeConfigSpec.ConfigValue<Boolean> BungeeCord = BUILDER.define("BungeeCord", super.getVelocity());
    private final ForgeConfigSpec.ConfigValue<Boolean> Velocity = BUILDER.define("Velocity", super.getBungeeCord());
    private final ForgeConfigSpec.ConfigValue<Boolean> allowBypassProxy = BUILDER.define("allowBypassProxy", super.getAllowBypassProxy());
    private final ForgeConfigSpec.ConfigValue<String> secret = BUILDER.comment("Velocity proxy secret").define("allowBypassProxy", super.getSecret());
    public final ForgeConfigSpec spec = BUILDER.build();

    @Override
    public Boolean getVelocity() {
        String env = System.getenv("FABRIC_PROXY_VELOCITY");
        if (env == null) {
            return Velocity.get();
        } else {
            return env.equalsIgnoreCase("true");
        }
    }

    @Override
    public Boolean getBungeeCord() {
        String env = System.getenv("FABRIC_PROXY_BUNGEECORD");
        if (env == null) {
            return BungeeCord.get();
        } else {
            return env.equalsIgnoreCase("true");
        }
    }

    @Override
    public String getSecret() {
        String env = System.getenv("FABRIC_PROXY_SECRET");
        if (env == null) {
            return secret.get();
        } else {
            return env;
        }
    }

    @Override
    public Boolean getAllowBypassProxy() {
        String env = System.getenv("FABRIC_PROXY_ALLOW_BYPASS_PROXY");
        if (env == null) {
            return allowBypassProxy.get();
        } else {
            return Boolean.valueOf(env);
        }
    }
}
