package net.stamina.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StaminaPacket(int stamina, int exhaustionTime, boolean force) implements CustomPayload {

    public static final CustomPayload.Id<StaminaPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("stamina", "stamina_packet"));

    public static final PacketCodec<RegistryByteBuf, StaminaPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeVarInt(value.stamina());
        buf.writeVarInt(value.exhaustionTime());
        buf.writeBoolean(value.force());
    }, buf -> new StaminaPacket(buf.readVarInt(), buf.readVarInt(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
