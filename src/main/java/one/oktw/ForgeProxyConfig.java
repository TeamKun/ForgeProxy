package one.oktw;

import net.minecraftforge.common.ForgeConfigSpec;

public class ForgeProxyConfig {
    private final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private final ForgeConfigSpec.ConfigValue<Boolean> BungeeCord = BUILDER.define("BungeeCord", false);
    private final ForgeConfigSpec.ConfigValue<Boolean> Velocity = BUILDER.define("Velocity", false);
    private final ForgeConfigSpec.ConfigValue<Boolean> allowBypassProxy = BUILDER.define("allowBypassProxy", false);
    private final ForgeConfigSpec.ConfigValue<String> secret = BUILDER.comment("Velocity proxy secret").define("secret", "");
    public final ForgeConfigSpec spec = BUILDER.build();

    public Boolean getVelocity() {
        String env = System.getenv("FABRIC_PROXY_VELOCITY");
        if (env == null) {
            return Velocity.get();
        } else {
            return env.equalsIgnoreCase("true");
        }
    }

    public Boolean getBungeeCord() {
        String env = System.getenv("FABRIC_PROXY_BUNGEECORD");
        if (env == null) {
            return BungeeCord.get();
        } else {
            return env.equalsIgnoreCase("true");
        }
    }

    public String getSecret() {
        String env = System.getenv("FABRIC_PROXY_SECRET");
        if (env == null) {
            return secret.get();
        } else {
            return env;
        }
    }

    public Boolean getAllowBypassProxy() {
        String env = System.getenv("FABRIC_PROXY_ALLOW_BYPASS_PROXY");
        if (env == null) {
            return allowBypassProxy.get();
        } else {
            return Boolean.valueOf(env);
        }
    }
}
