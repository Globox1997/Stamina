package net.stamina.network;

import com.google.gson.JsonParseException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.stamina.StaminaMain;
import net.stamina.access.StaminaAccess;
import net.stamina.config.StaminaConfig;
import net.stamina.init.ConfigInit;
import net.stamina.network.packet.StaminaConfigPacket;
import net.stamina.network.packet.StaminaPacket;

@Environment(EnvType.CLIENT)
public class StaminaClientPacket {

    @SuppressWarnings("resource")
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(StaminaPacket.PACKET_ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player != null) {
                    ((StaminaAccess) context.client().player).applyServerStamina(payload.stamina(), payload.exhaustionTime(), payload.force());
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(StaminaConfigPacket.PACKET_ID, (payload, context) -> {
            String json = payload.config();
            context.client().execute(() -> {
                try {
                    StaminaConfig config = ConfigInit.GSON.fromJson(json, StaminaConfig.class);
                    config.validatePostLoad();
                    ConfigInit.SYNCED_CONFIG = config;
                } catch (JsonParseException exception) {
                    StaminaMain.LOGGER.error("Failed to read the stamina server config", exception);
                }
            });
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            client.execute(() -> ConfigInit.SYNCED_CONFIG = null);
        });
    }
}
