package net.stamina.api;

import net.minecraft.entity.player.PlayerEntity;
import net.stamina.access.StaminaAccess;
import net.stamina.init.AttributeInit;
import net.stamina.util.StaminaHelper;

public class StaminaAPI {

    public static int getStamina(PlayerEntity player) {
        return ((StaminaAccess) player).getStamina();
    }

    public static int getMaxStamina(PlayerEntity player) {
        return (int) player.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
    }

    public static void setStamina(PlayerEntity player, int stamina) {
        ((StaminaAccess) player).setStamina(stamina);
    }

    public static void addStamina(PlayerEntity player, int stamina) {
        ((StaminaAccess) player).addStamina(stamina);
    }

    public static void consumeStamina(PlayerEntity player, float cost) {
        ((StaminaAccess) player).consumeStamina(cost, StaminaCostSource.OTHER);
    }

    public static boolean canStartSprinting(PlayerEntity player) {
        return !StaminaHelper.isOutOfStamina(player);
    }

    public static boolean isExhausted(PlayerEntity player) {
        return StaminaHelper.isExhausted(player);
    }

}
