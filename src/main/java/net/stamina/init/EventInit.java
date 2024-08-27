package net.stamina.init;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.stamina.access.StaminaAccess;
import net.stamina.network.packet.StaminaPacket;

public class EventInit {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayNetworking.send(handler.player, new StaminaPacket(((StaminaAccess) handler.player).getStamina()));
        });
    }

}
