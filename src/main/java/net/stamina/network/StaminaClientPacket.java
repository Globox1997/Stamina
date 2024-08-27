package net.stamina.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.stamina.access.StaminaAccess;
import net.stamina.network.packet.StaminaPacket;

@Environment(EnvType.CLIENT)
public class StaminaClientPacket {

    @SuppressWarnings("resource")
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(StaminaPacket.PACKET_ID, (payload, context) -> {
            int stamina = payload.stamina();
            context.client().execute(() -> {
                ((StaminaAccess) context.client().player).setStamina(stamina);
            });
        });
    }
}
