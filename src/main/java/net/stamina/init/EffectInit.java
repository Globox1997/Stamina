package net.stamina.init;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.stamina.effect.EnduranceEffect;

public class EffectInit {

    public static final RegistryEntry<StatusEffect> ENDURANCE = register("stamina:endurance", new EnduranceEffect(StatusEffectCategory.BENEFICIAL, 12221440)
            .addAttributeModifier(AttributeInit.GENERIC_MAX_STAMINA, Identifier.of("stamina", "endurance"), 90, EntityAttributeModifier.Operation.ADD_VALUE));

    public static void init() {
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(id), statusEffect);
    }
}
