package net.stamina.init;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.stamina.effect.EnduranceEffect;

public class EffectInit {

    public static final StatusEffect ENDURANCE = new EnduranceEffect(StatusEffectCategory.BENEFICIAL, 0x8B3833).addAttributeModifier(AttributeInit.GENERIC_MAX_STAMINA,
            "609b7bb0-2d77-4d04-8708-4fe6b8d43f6f", 90, EntityAttributeModifier.Operation.ADDITION);

    public static void init() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("stamina", "endurance"), ENDURANCE);
    }

}
