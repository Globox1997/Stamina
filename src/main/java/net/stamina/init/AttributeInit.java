package net.stamina.init;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class AttributeInit {

    public static final RegistryEntry<EntityAttribute> GENERIC_MAX_STAMINA = Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla("generic.max_stamina"),
            new ClampedEntityAttribute("attribute.name.generic.max_stamina", 360.0, 10.0, 2048.0).setTracked(true));

    public static void init() {
    }

}
