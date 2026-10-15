package net.stamina.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.stamina.access.StaminaAccess;
import net.stamina.api.StaminaCostSource;
import net.stamina.init.ConfigInit;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {

    @Inject(method = "use", at = @At("RETURN"))
    private void useMixin(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        if (user.isFallFlying() && info.getReturnValue().getResult().isAccepted()) {
            ((StaminaAccess) user).consumeStamina(ConfigInit.get(world).elytraBoostStaminaCost, StaminaCostSource.ELYTRA_BOOST);
        }
    }
}
