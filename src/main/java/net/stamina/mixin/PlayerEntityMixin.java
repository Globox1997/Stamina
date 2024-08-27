package net.stamina.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.stamina.access.StaminaAccess;
import net.stamina.init.AttributeInit;
import net.stamina.init.ConfigInit;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements StaminaAccess {

    @Shadow
    @Mutable
    @Final
    private PlayerAbilities abilities;

    private static final EntityAttributeModifier STAMINA_SPEED_REDUCTION = new EntityAttributeModifier(UUID.fromString("1dfe298b-4f31-432a-a159-f766c304cac7"), "Stamina speed reduction", -0.05f,
            EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private int stamina = (int) this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
    private int exhaustionTime = 0;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.stamina = nbt.getInt("Stamina");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("Stamina", this.stamina);
    }

    @Inject(method = "addExhaustion", at = @At("HEAD"))
    private void addExhaustionMixin(float exhaustion, CallbackInfo info) {
        addStamina(-(int) (exhaustion * 10f));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        // if (this.getWorld().getTime() % 5 == 0) { // maybe outcomment?
        if (!this.isSprinting()) {
            if (this.exhaustionTime <= 0) {
                if (this.getHungerManager().getFoodLevel() > 6.0f) {
                    addStamina(1);
                }
            } else {
                this.exhaustionTime--;
            }
        } else if (!this.abilities.creativeMode) {
            addStamina(-1); // check for speed effect
        }
        // }
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void createPlayerAttributesMixin(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue().add(AttributeInit.GENERIC_MAX_STAMINA);
    }

    @Override
    public void addStamina(int stamina) {
        if (this.stamina + stamina > this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA)) {
            this.stamina = (int) this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
        } else if (this.stamina + stamina <= 0) {
            this.stamina = 0;
            this.exhaustionTime = ConfigInit.CONFIG.exhaustionTime;
            if (!this.getWorld().isClient() && !this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).hasModifier(STAMINA_SPEED_REDUCTION)) {
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(STAMINA_SPEED_REDUCTION);
            }
        } else {
            this.stamina += stamina;
            if (!this.getWorld().isClient() && this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).hasModifier(STAMINA_SPEED_REDUCTION)) {
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(STAMINA_SPEED_REDUCTION);
            }
        }
    }

    @Override
    public int getStamina() {
        return this.stamina;
    }

    @Override
    public void setStamina(int stamina) {
        if (stamina > this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA)) {
            this.stamina = (int) this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
        } else if (stamina < 0) {
            this.stamina = 0;
        } else {
            this.stamina = stamina;
        }
    }

    @Shadow
    public HungerManager getHungerManager() {
        return null;
    }

}
