package net.stamina.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StaminaConfigPacket(String config) implements CustomPayload {

    public static final CustomPayload.Id<StaminaConfigPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("stamina", "stamina_config_packet"));

    public static final PacketCodec<RegistryByteBuf, StaminaConfigPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeString(value.config());
    }, buf -> new StaminaConfigPacket(buf.readString()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
