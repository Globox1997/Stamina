package net.stamina;

import net.fabricmc.api.ModInitializer;
import net.stamina.init.AttributeInit;
import net.stamina.init.ConfigInit;
import net.stamina.init.EffectInit;
import net.stamina.init.ItemInit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaminaMain implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Stamina");

    @Override
    public void onInitialize() {
        ConfigInit.init();
        AttributeInit.init();
        EffectInit.init();
        ItemInit.init();
    }
}