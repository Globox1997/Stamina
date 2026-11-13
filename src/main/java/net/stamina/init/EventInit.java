package net.stamina.init;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.stamina.access.StaminaAccess;
import net.stamina.api.StaminaCostSource;
import net.stamina.config.StaminaConfig;
import net.stamina.util.StaminaHelper;

public class EventInit {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            StaminaHelper.syncConfig(handler.player);
            ((StaminaAccess) handler.player).requestStaminaSync();
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((StaminaAccess) player).requestStaminaSync();
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ((StaminaAccess) newPlayer).requestStaminaSync();
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof LivingEntity) {
                StaminaConfig config = ConfigInit.get(world);
                ((StaminaAccess) player).consumeStamina(StaminaHelper.getAttackStaminaCost(player, config), StaminaCostSource.ATTACK);
            }
            return ActionResult.PASS;
        });
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            StaminaConfig config = ConfigInit.get(world);
            if (config.miningCostsStamina) {
                ((StaminaAccess) player).consumeStamina(StaminaHelper.getMiningStaminaCost(state, world, pos, config), StaminaCostSource.MINING);
            }
        });
    }

}
