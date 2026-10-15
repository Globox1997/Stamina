package net.stamina.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.stamina.access.StaminaAccess;
import net.stamina.api.StaminaCostSource;
import net.stamina.api.StaminaEvents;
import net.stamina.config.StaminaConfig;
import net.stamina.init.AttributeInit;
import net.stamina.init.ConfigInit;
import net.stamina.util.StaminaHelper;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements StaminaAccess {

    @Unique
    private static final Identifier STAMINA_SPEED_REDUCTION = Identifier.of("stamina", "stamina_speed_reduction");
    @Unique
    private static final Identifier STAMINA_ATTACK_DAMAGE_REDUCTION = Identifier.of("stamina", "stamina_attack_damage_reduction");
    @Unique
    private static final int SYNC_INTERVAL = 4;
    @Unique
    private static final int PREDICTION_TOLERANCE = 8;

    @Unique
    private int stamina = -1;
    @Unique
    private int exhaustionTime = 0;
    @Unique
    private int regenDelay = 0;
    @Unique
    private float pendingStaminaCost = 0.0f;
    @Unique
    private float pendingStaminaRegen = 0.0f;
    @Unique
    private double lastClimbY = 0.0D;
    @Unique
    private int lastSyncedStamina = -1;
    @Unique
    private boolean forceStaminaSync = false;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("Stamina")) {
            this.setStamina(nbt.getInt("Stamina"));
        }
        this.exhaustionTime = Math.max(0, nbt.getInt("StaminaExhaustionTime"));
        this.regenDelay = Math.max(0, nbt.getInt("StaminaRegenDelay"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("Stamina", this.getStamina());
        nbt.putInt("StaminaExhaustionTime", this.exhaustionTime);
        nbt.putInt("StaminaRegenDelay", this.regenDelay);
    }

    @Inject(method = "addExhaustion", at = @At("HEAD"))
    private void addExhaustionMixin(float exhaustion, CallbackInfo info) {
        consumeStamina(exhaustion * ConfigInit.get(this.getWorld()).exhaustionStaminaMultiplier, StaminaCostSource.EXHAUSTION);
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void jumpMixin(CallbackInfo info) {
        if (ConfigInit.get(this.getWorld()).exhaustionBlocksJumping && StaminaHelper.isExhausted((PlayerEntity) (Object) this)) {
            info.cancel();
        }
    }

    @Inject(method = "damageShield", at = @At("HEAD"))
    private void damageShieldMixin(float amount, CallbackInfo info) {
        consumeStamina(ConfigInit.get(this.getWorld()).shieldBlockStaminaCost, StaminaCostSource.SHIELD);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (!isStaminaSide()) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        StaminaConfig config = ConfigInit.get(this.getWorld());
        if (!this.getWorld().isClient()) {
            updateBaseValue(AttributeInit.GENERIC_MAX_STAMINA, config.maxStamina);
            updateBaseValue(AttributeInit.GENERIC_STAMINA_REGEN, config.staminaRegen);
        }
        int maxStamina = (int) this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
        if (this.stamina < 0 || this.stamina > maxStamina) {
            this.stamina = maxStamina;
        }

        if (StaminaHelper.isExempt(playerEntity)) {
            this.stamina = maxStamina;
            this.exhaustionTime = 0;
            this.regenDelay = 0;
            this.pendingStaminaCost = 0.0f;
        } else {
            if (this.isSprinting() && !this.hasVehicle()) {
                float sprintCost = config.sprintStaminaCost;
                StatusEffectInstance speed = this.getStatusEffect(StatusEffects.SPEED);
                if (speed != null) {
                    sprintCost *= 1.0f + (speed.getAmplifier() + 1) * config.speedEffectSprintMultiplier;
                }
                consumeStamina(sprintCost, StaminaCostSource.SPRINT);
            } else if (this.exhaustionTime > 0) {
                this.exhaustionTime--;
            } else if (this.regenDelay > 0) {
                this.regenDelay--;
            } else {
                regenerateStamina(config, maxStamina);
            }
            if (this.isClimbing() && this.getY() - this.lastClimbY > 0.01D) {
                consumeStamina(config.climbStaminaCost, StaminaCostSource.CLIMB);
            }
        }
        this.lastClimbY = this.getY();

        if ((Object) this instanceof ServerPlayerEntity serverPlayerEntity) {
            boolean exhausted = StaminaHelper.isExhausted(playerEntity);
            updateExhaustionModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, STAMINA_SPEED_REDUCTION, -config.exhaustionSpeedReduction, EntityAttributeModifier.Operation.ADD_VALUE, exhausted);
            updateExhaustionModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, STAMINA_ATTACK_DAMAGE_REDUCTION, -config.exhaustionAttackDamageReduction,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, exhausted);
            syncStamina(serverPlayerEntity, maxStamina);
        }
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void createPlayerAttributesMixin(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue().add(AttributeInit.GENERIC_MAX_STAMINA).add(AttributeInit.GENERIC_STAMINA_REGEN);
    }

    @Override
    public void addStamina(int stamina) {
        int maxStamina = (int) this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
        int newStamina = this.getStamina() + stamina;
        if (newStamina > maxStamina) {
            this.stamina = maxStamina;
        } else if (newStamina <= 0) {
            this.stamina = 0;
            this.exhaustionTime = ConfigInit.get(this.getWorld()).exhaustionTime;
            this.forceStaminaSync = true;
        } else {
            this.stamina = newStamina;
        }
    }

    @Override
    public int getStamina() {
        if (this.stamina < 0) {
            return (int) this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
        }
        return this.stamina;
    }

    @Override
    public void setStamina(int stamina) {
        this.stamina = Math.max(0, Math.min(stamina, (int) this.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA)));
        this.forceStaminaSync = true;
    }

    @Override
    public void consumeStamina(float cost, StaminaCostSource source) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (!isStaminaSide() || cost <= 0.0f || StaminaHelper.isStaminaFree(playerEntity)) {
            return;
        }
        cost = StaminaEvents.MODIFY_COST.invoker().modifyCost(playerEntity, source, cost);
        if (cost <= 0.0f) {
            return;
        }
        this.pendingStaminaCost += cost;
        int staminaCost = (int) this.pendingStaminaCost;
        if (staminaCost > 0) {
            this.pendingStaminaCost -= staminaCost;
            this.regenDelay = ConfigInit.get(this.getWorld()).regenDelay;
            addStamina(-staminaCost);
        }
    }

    @Override
    public int getExhaustionTime() {
        return this.exhaustionTime;
    }

    @Override
    public void requestStaminaSync() {
        this.forceStaminaSync = true;
    }

    @Override
    public void applyServerStamina(int stamina, int exhaustionTime, boolean force) {
        if (force || this.stamina < 0 || Math.abs(this.getStamina() - stamina) > PREDICTION_TOLERANCE) {
            this.stamina = Math.max(0, stamina);
            this.exhaustionTime = exhaustionTime;
            this.pendingStaminaCost = 0.0f;
            this.pendingStaminaRegen = 0.0f;
        }
    }

    @Unique
    private boolean isStaminaSide() {
        return !this.getWorld().isClient() || this.isMainPlayer();
    }

    @Unique
    private void regenerateStamina(StaminaConfig config, int maxStamina) {
        if (this.stamina >= maxStamina) {
            this.pendingStaminaRegen = 0.0f;
            return;
        }
        HungerManager hungerManager = this.getHungerManager();
        if (hungerManager.getFoodLevel() <= config.minFoodLevel) {
            return;
        }
        float regen = (float) this.getAttributeValue(AttributeInit.GENERIC_STAMINA_REGEN);
        if (this.hasStatusEffect(StatusEffects.HUNGER)) {
            regen *= config.hungerEffectRegenMultiplier;
        }
        if (this.hasStatusEffect(StatusEffects.WEAKNESS)) {
            regen *= config.weaknessEffectRegenMultiplier;
        }
        if (hungerManager.getSaturationLevel() > 0.0f) {
            regen *= config.saturationRegenMultiplier;
        }
        regen = StaminaEvents.MODIFY_REGEN.invoker().modifyRegen((PlayerEntity) (Object) this, regen);
        if (regen <= 0.0f) {
            return;
        }
        this.pendingStaminaRegen += regen;
        int staminaRegen = (int) this.pendingStaminaRegen;
        if (staminaRegen > 0) {
            this.pendingStaminaRegen -= staminaRegen;
            addStamina(staminaRegen);
        }
    }

    @Unique
    private void syncStamina(ServerPlayerEntity serverPlayerEntity, int maxStamina) {
        if (this.stamina == this.lastSyncedStamina && !this.forceStaminaSync) {
            return;
        }
        int sprintThreshold = StaminaHelper.getSprintThreshold(serverPlayerEntity);
        boolean crossed = this.lastSyncedStamina < 0 || (this.lastSyncedStamina > 0) != (this.stamina > 0) || (this.lastSyncedStamina > sprintThreshold) != (this.stamina > sprintThreshold)
                || (this.lastSyncedStamina >= maxStamina) != (this.stamina >= maxStamina);
        if (this.forceStaminaSync || crossed || this.age % SYNC_INTERVAL == 0) {
            boolean force = this.forceStaminaSync;
            this.lastSyncedStamina = this.stamina;
            this.forceStaminaSync = false;
            StaminaHelper.syncStamina(serverPlayerEntity, force);
        }
    }

    @Unique
    private void updateBaseValue(RegistryEntry<EntityAttribute> attribute, double value) {
        EntityAttributeInstance attributeInstance = this.getAttributeInstance(attribute);
        if (attributeInstance != null && attributeInstance.getBaseValue() != value) {
            attributeInstance.setBaseValue(value);
        }
    }

    @Unique
    private void updateExhaustionModifier(RegistryEntry<EntityAttribute> attribute, Identifier id, double value, EntityAttributeModifier.Operation operation, boolean active) {
        EntityAttributeInstance attributeInstance = this.getAttributeInstance(attribute);
        if (attributeInstance == null) {
            return;
        }
        EntityAttributeModifier modifier = attributeInstance.getModifier(id);
        if (active && value != 0.0D) {
            if (modifier == null || modifier.value() != value) {
                attributeInstance.removeModifier(id);
                attributeInstance.addTemporaryModifier(new EntityAttributeModifier(id, value, operation));
            }
        } else if (modifier != null) {
            attributeInstance.removeModifier(id);
        }
    }

    @Shadow
    public HungerManager getHungerManager() {
        return null;
    }

    @Shadow
    public abstract boolean isMainPlayer();

}
