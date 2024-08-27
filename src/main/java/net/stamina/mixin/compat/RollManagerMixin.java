package net.stamina.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.combatroll.client.RollManager;
import net.minecraft.entity.player.PlayerEntity;
import net.stamina.util.StaminaHelper;

@Mixin(RollManager.class)
public class RollManagerMixin {

    @Inject(method = "isRollAvailable", at = @At("RETURN"), cancellable = true)
    private void isRollAvailableMixin(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue() && StaminaHelper.isOutOfStamina(player)) {
            info.setReturnValue(false);
        }
    }
}
