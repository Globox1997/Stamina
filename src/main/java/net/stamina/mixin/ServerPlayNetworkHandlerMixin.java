package net.stamina.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.stamina.util.StaminaHelper;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onClientCommand", at = @At("TAIL"))
    private void onClientCommandMixin(ClientCommandC2SPacket packet, CallbackInfo info) {
        if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING && StaminaHelper.isOutOfStamina(this.player)) {
            this.player.setSprinting(false);
        }
    }

}
