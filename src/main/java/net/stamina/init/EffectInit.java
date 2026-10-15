package net.stamina.init;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.stamina.effect.StaminaEffect;

public class EffectInit {

    public static final RegistryEntry<StatusEffect> ENDURANCE = register("stamina:endurance", new StaminaEffect(StatusEffectCategory.BENEFICIAL, 12221440)
            .addAttributeModifier(AttributeInit.GENERIC_MAX_STAMINA, Identifier.of("stamina", "endurance"), ConfigInit.CONFIG.enduranceMaxStaminaBonus, EntityAttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(AttributeInit.GENERIC_STAMINA_REGEN, Identifier.of("stamina", "endurance_regen"), ConfigInit.CONFIG.enduranceRegenBonus,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final RegistryEntry<StatusEffect> FATIGUE = register("stamina:fatigue", new StaminaEffect(StatusEffectCategory.HARMFUL, 7038072)
            .addAttributeModifier(AttributeInit.GENERIC_MAX_STAMINA, Identifier.of("stamina", "fatigue"), -ConfigInit.CONFIG.fatigueMaxStaminaReduction, EntityAttributeModifier.Operation.ADD_VALUE));

    public static void init() {
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(id), statusEffect);
    }
}
