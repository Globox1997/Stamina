package net.stamina.util;

import net.minecraft.entity.player.PlayerEntity;
import net.stamina.access.StaminaAccess;
import net.stamina.init.AttributeInit;
import net.stamina.init.ConfigInit;

public class StaminaHelper {

    public static boolean isOutOfStamina(PlayerEntity playerEntity) {
        if (!playerEntity.isCreative()
                && ((StaminaAccess) playerEntity).getStamina() <= (int) (playerEntity.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA) * (ConfigInit.CONFIG.minSprintStamina / 100.0f))) {
            return true;
        }
        return false;
    }

}
