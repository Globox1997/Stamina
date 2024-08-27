package net.stamina.mixin.client;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.stamina.access.StaminaAccess;
import net.stamina.util.StaminaHelper;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "canSprint", at = @At("RETURN"), cancellable = true)
    private void canSprintMixin(CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue() && !this.isCreative() && ((StaminaAccess) this).getStamina() <= 0) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "canStartSprinting", at = @At("RETURN"), cancellable = true)
    private void canStartSprintingMixin(CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue() && StaminaHelper.isOutOfStamina(this)) {
            info.setReturnValue(false);
        }
    }
}
