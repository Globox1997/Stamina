package net.stamina.compat;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.thirst.ThirstManager;
import net.stamina.access.StaminaAccess;
import net.stamina.api.StaminaEvents;
import net.stamina.config.StaminaConfig;
import net.stamina.init.ConfigInit;
import net.stamina.util.StaminaHelper;

public class DehydrationCompat {

    public static void init() {
        StaminaEvents.MODIFY_REGEN.register((player, regen) -> {
            if (player instanceof ThirstManagerAccess thirstManagerAccess) {
                ThirstManager thirstManager = thirstManagerAccess.getThirstManager();
                StaminaConfig config = ConfigInit.get(player.getWorld());
                if (thirstManager != null && thirstManager.hasThirst() && thirstManager.getThirstLevel() <= config.dehydrationMinThirstLevel) {
                    if (player.getHungerManager().getFoodLevel() <= config.minFoodLevel) {
                        return regen;
                    }
                    if (((StaminaAccess) player).getStamina() >= StaminaHelper.getStarvingStaminaLimit(player)) {
                        return 0.0f;
                    }
                    return regen * config.starvingRegenMultiplier;
                }
            }
            return regen;
        });
    }

}
