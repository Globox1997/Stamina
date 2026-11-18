package net.stamina.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stamina.access.StaminaAccess;
import net.stamina.config.StaminaConfig;
import net.stamina.init.AttributeInit;
import net.stamina.init.ConfigInit;
import net.stamina.network.packet.StaminaConfigPacket;
import net.stamina.network.packet.StaminaPacket;

public class StaminaHelper {

    private static final double REFERENCE_ATTACK_SPEED = 1.6D;
    private static final float MAX_MINING_HARDNESS = 5.0f;

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

    public static int getStarvingStaminaLimit(PlayerEntity playerEntity) {
        return Math.max(1, getSprintThreshold(playerEntity));
    }

    public static boolean isOutOfStamina(PlayerEntity playerEntity) {
        if (isStaminaFree(playerEntity)) {
            return false;
        }
        return ((StaminaAccess) playerEntity).getStamina() <= getSprintThreshold(playerEntity);
    }

    public static float getAttackStaminaCost(PlayerEntity playerEntity, StaminaConfig config) {
        double attackSpeed = Math.max(0.1D, playerEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED));
        float cost = (float) (config.attackStaminaCost * REFERENCE_ATTACK_SPEED / attackSpeed);
        if (playerEntity.getAttackCooldownProgress(0.5f) > 0.9f) {
            if (playerEntity.isSprinting()) {
                cost *= config.sprintAttackStaminaMultiplier;
            } else if (playerEntity.fallDistance > 0.0f && !playerEntity.isOnGround() && !playerEntity.isClimbing() && !playerEntity.isTouchingWater()
                    && !playerEntity.hasStatusEffect(StatusEffects.BLINDNESS) && !playerEntity.hasVehicle()) {
                cost *= config.criticalAttackStaminaMultiplier;
            }
        }
        return cost;
    }

    public static float getMiningStaminaCost(BlockState state, World world, BlockPos pos, StaminaConfig config) {
        float hardness = state.getHardness(world, pos);
        if (hardness <= 0.0f) {
            return 0.0f;
        }
        return config.miningStaminaCost * Math.min(hardness, MAX_MINING_HARDNESS);
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
