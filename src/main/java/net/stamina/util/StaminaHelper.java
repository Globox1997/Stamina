package net.stamina.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.stamina.access.StaminaAccess;
import net.stamina.init.AttributeInit;
import net.stamina.init.ConfigInit;
import net.stamina.network.packet.StaminaConfigPacket;
import net.stamina.network.packet.StaminaPacket;

public class StaminaHelper {

    public static boolean isExempt(PlayerEntity playerEntity) {
        return playerEntity.isCreative() || playerEntity.isSpectator();
    }

    public static boolean isStaminaFree(PlayerEntity playerEntity) {
        return isExempt(playerEntity) || playerEntity.hasVehicle();
    }

    public static boolean isExhausted(PlayerEntity playerEntity) {
        return !isExempt(playerEntity) && ((StaminaAccess) playerEntity).getStamina() <= 0;
    }

    public static int getSprintThreshold(PlayerEntity playerEntity) {
        return (int) (playerEntity.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA) * (ConfigInit.get(playerEntity.getWorld()).minSprintStamina / 100.0f));
    }

    public static boolean isOutOfStamina(PlayerEntity playerEntity) {
        if (isStaminaFree(playerEntity)) {
            return false;
        }
        return ((StaminaAccess) playerEntity).getStamina() <= getSprintThreshold(playerEntity);
    }

    public static void syncStamina(ServerPlayerEntity serverPlayerEntity, boolean force) {
        if (serverPlayerEntity.networkHandler != null && ServerPlayNetworking.canSend(serverPlayerEntity, StaminaPacket.PACKET_ID)) {
            StaminaAccess staminaAccess = (StaminaAccess) serverPlayerEntity;
            ServerPlayNetworking.send(serverPlayerEntity, new StaminaPacket(staminaAccess.getStamina(), staminaAccess.getExhaustionTime(), force));
        }
    }

    public static void syncConfig(ServerPlayerEntity serverPlayerEntity) {
        if (serverPlayerEntity.networkHandler != null && ServerPlayNetworking.canSend(serverPlayerEntity, StaminaConfigPacket.PACKET_ID)) {
            ServerPlayNetworking.send(serverPlayerEntity, new StaminaConfigPacket(ConfigInit.GSON.toJson(ConfigInit.CONFIG)));
        }
    }

}
