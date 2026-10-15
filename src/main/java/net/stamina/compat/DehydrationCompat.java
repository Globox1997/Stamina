package net.stamina.compat;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.thirst.ThirstManager;
import net.stamina.api.StaminaEvents;
import net.stamina.init.ConfigInit;

public class DehydrationCompat {

    public static void init() {
        StaminaEvents.MODIFY_REGEN.register((player, regen) -> {
            if (player instanceof ThirstManagerAccess thirstManagerAccess) {
                ThirstManager thirstManager = thirstManagerAccess.getThirstManager();
                if (thirstManager != null && thirstManager.hasThirst() && thirstManager.getThirstLevel() <= ConfigInit.get(player.getWorld()).dehydrationMinThirstLevel) {
                    return 0.0f;
                }
            }
            return regen;
        });
    }

}
