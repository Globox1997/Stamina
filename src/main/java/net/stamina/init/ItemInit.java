package net.stamina.init;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ItemInit {

    // Potion
    public static final Potion ENDURANCE = new Potion(new StatusEffectInstance(EffectInit.ENDURANCE, 3600));
    public static final Potion LONG_ENDURANCE = new Potion(new StatusEffectInstance(EffectInit.ENDURANCE, 9600));

    public static void init() {
        // Potion
        Registry.register(Registries.POTION, "endurance", ENDURANCE);
        Registry.register(Registries.POTION, "long_endurance", LONG_ENDURANCE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.LAPIS_LAZULI, ENDURANCE);
        BrewingRecipeRegistry.registerPotionRecipe(ENDURANCE, Items.REDSTONE, LONG_ENDURANCE);
    }

}
