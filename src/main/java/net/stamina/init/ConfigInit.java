package net.stamina.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.stamina.config.StaminaConfig;

public class ConfigInit {

    public static StaminaConfig CONFIG = new StaminaConfig();

    public static void init() {
        AutoConfig.register(StaminaConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(StaminaConfig.class).getConfig();
    }

}
