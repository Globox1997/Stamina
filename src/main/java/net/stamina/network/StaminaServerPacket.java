package net.stamina.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.stamina.network.packet.StaminaPacket;

public class StaminaServerPacket {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(StaminaPacket.PACKET_ID, StaminaPacket.PACKET_CODEC);
    }
}
