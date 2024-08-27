package net.stamina;

import net.fabricmc.api.ClientModInitializer;
import net.stamina.init.RenderInit;
import net.stamina.network.StaminaClientPacket;

public class StaminaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RenderInit.init();
        StaminaClientPacket.init();
    }

}
