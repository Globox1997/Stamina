package net.stamina.init;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.stamina.access.StaminaAccess;
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
    }

}
