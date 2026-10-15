package net.stamina.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "stamina")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class StaminaConfig implements ConfigData {

    @ConfigEntry.Category("gameplay")
    @Comment("Base max stamina, 10 - 2048")
    public int maxStamina = 360;
    @ConfigEntry.Category("gameplay")
    @Comment("Base stamina regeneration per tick")
    public float staminaRegen = 1.0f;
    @ConfigEntry.Category("gameplay")
    @Comment("Stamina cost per tick while sprinting")
    public float sprintStaminaCost = 1.0f;
    @ConfigEntry.Category("gameplay")
    @Comment("Stamina cost per point of vanilla exhaustion (jumping, attacking, mining...)")
    public float exhaustionStaminaMultiplier = 10.0f;
    @ConfigEntry.Category("gameplay")
    @Comment("Is in %")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int minSprintStamina = 17;
    @ConfigEntry.Category("gameplay")
    @Comment("Ticks without regeneration after running out of stamina")
    public int exhaustionTime = 40;
    @ConfigEntry.Category("gameplay")
    @Comment("Ticks without regeneration after using stamina")
    public int regenDelay = 20;
    @ConfigEntry.Category("gameplay")
    @Comment("Regeneration requires a food level above this")
    public int minFoodLevel = 6;
    @ConfigEntry.Category("gameplay")
    @Comment("Movement speed reduction while exhausted")
    public float exhaustionSpeedReduction = 0.05f;
    @ConfigEntry.Category("gameplay")
    @Comment("Attack damage reduction while exhausted, 0.5 = 50%")
    public float exhaustionAttackDamageReduction = 0.5f;
    @ConfigEntry.Category("gameplay")
    public boolean exhaustionBlocksJumping = true;
    @ConfigEntry.Category("gameplay")
    @Comment("Stamina cost per blocked hit")
    public float shieldBlockStaminaCost = 10.0f;
    @ConfigEntry.Category("gameplay")
    @Comment("Stamina cost per tick while climbing upwards")
    public float climbStaminaCost = 0.5f;
    @ConfigEntry.Category("gameplay")
    @Comment("Stamina cost per firework boost while flying with an elytra")
    public float elytraBoostStaminaCost = 30.0f;
    @ConfigEntry.Category("gameplay")
    @Comment("Additional sprint cost per speed effect level, 0.2 = 20%")
    public float speedEffectSprintMultiplier = 0.2f;
    @ConfigEntry.Category("gameplay")
    @Comment("Regeneration multiplier while having the hunger effect")
    public float hungerEffectRegenMultiplier = 0.5f;
    @ConfigEntry.Category("gameplay")
    @Comment("Regeneration multiplier while having the weakness effect")
    public float weaknessEffectRegenMultiplier = 0.75f;
    @ConfigEntry.Category("gameplay")
    @Comment("Regeneration multiplier while having food saturation")
    public float saturationRegenMultiplier = 1.25f;
    @ConfigEntry.Category("gameplay")
    @Comment("Max stamina bonus of the endurance effect, requires restart")
    @ConfigEntry.Gui.RequiresRestart
    public int enduranceMaxStaminaBonus = 90;
    @ConfigEntry.Category("gameplay")
    @Comment("Regeneration bonus of the endurance effect, 0.25 = 25%, requires restart")
    @ConfigEntry.Gui.RequiresRestart
    public float enduranceRegenBonus = 0.25f;
    @ConfigEntry.Category("gameplay")
    @Comment("Max stamina reduction of the fatigue effect, requires restart")
    @ConfigEntry.Gui.RequiresRestart
    public int fatigueMaxStaminaReduction = 90;
    @ConfigEntry.Category("gameplay")
    @Comment("Dehydration compat: regeneration requires a thirst level above this")
    public int dehydrationMinThirstLevel = 6;

    // Hud, client only
    @ConfigEntry.Category("hud")
    public int hudOffsetX = 0;
    @ConfigEntry.Category("hud")
    public int hudOffsetY = 0;
    @ConfigEntry.Category("hud")
    public boolean hudFadeWhenFull = true;
    @ConfigEntry.Category("hud")
    public boolean hudShowNumber = false;
    @ConfigEntry.Category("hud")
    public boolean hudShowSprintThreshold = true;
    @ConfigEntry.Category("hud")
    public boolean hudColorBelowSprintThreshold = true;

    @Override
    public void validatePostLoad() {
        this.maxStamina = Math.max(10, Math.min(2048, this.maxStamina));
        this.staminaRegen = Math.max(0.0f, this.staminaRegen);
        this.sprintStaminaCost = Math.max(0.0f, this.sprintStaminaCost);
        this.exhaustionStaminaMultiplier = Math.max(0.0f, this.exhaustionStaminaMultiplier);
        this.minSprintStamina = Math.max(0, Math.min(100, this.minSprintStamina));
        this.exhaustionTime = Math.max(0, this.exhaustionTime);
        this.regenDelay = Math.max(0, this.regenDelay);
        this.exhaustionSpeedReduction = Math.max(0.0f, this.exhaustionSpeedReduction);
        this.exhaustionAttackDamageReduction = Math.max(0.0f, Math.min(1.0f, this.exhaustionAttackDamageReduction));
        this.shieldBlockStaminaCost = Math.max(0.0f, this.shieldBlockStaminaCost);
        this.climbStaminaCost = Math.max(0.0f, this.climbStaminaCost);
        this.elytraBoostStaminaCost = Math.max(0.0f, this.elytraBoostStaminaCost);
        this.hungerEffectRegenMultiplier = Math.max(0.0f, this.hungerEffectRegenMultiplier);
        this.weaknessEffectRegenMultiplier = Math.max(0.0f, this.weaknessEffectRegenMultiplier);
        this.saturationRegenMultiplier = Math.max(0.0f, this.saturationRegenMultiplier);
    }

}
