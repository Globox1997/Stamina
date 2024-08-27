package net.stamina.init;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ItemInit {

    public static final RegistryEntry<Potion> ENDURANCE = register("stamina:endurance", new Potion(new StatusEffectInstance(EffectInit.ENDURANCE, 3600)));
    public static final RegistryEntry<Potion> LONG_ENDURANCE = register("stamina:long_endurance", new Potion(new StatusEffectInstance(EffectInit.ENDURANCE, 9600)));

    public static void init() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
            builder.registerPotionRecipe(Potions.AWKWARD, Items.LAPIS_LAZULI, ENDURANCE);
            builder.registerPotionRecipe(ItemInit.ENDURANCE, Items.REDSTONE, LONG_ENDURANCE);
        });
    }

    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(name), potion);
    }

}
