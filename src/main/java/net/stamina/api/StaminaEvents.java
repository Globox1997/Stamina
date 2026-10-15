package net.stamina.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Events run on the server and on the client for the local player, which predicts its stamina.
 */
public class StaminaEvents {

    /**
     * Modify a stamina cost before it gets applied. Return 0 to cancel the cost.
     */
    public static final Event<ModifyCost> MODIFY_COST = EventFactory.createArrayBacked(ModifyCost.class, listeners -> (player, source, cost) -> {
        for (ModifyCost listener : listeners) {
            cost = listener.modifyCost(player, source, cost);
        }
        return cost;
    });

    /**
     * Modify the stamina regeneration of the current tick. Return 0 to stop regeneration.
     */
    public static final Event<ModifyRegen> MODIFY_REGEN = EventFactory.createArrayBacked(ModifyRegen.class, listeners -> (player, regen) -> {
        for (ModifyRegen listener : listeners) {
            regen = listener.modifyRegen(player, regen);
        }
        return regen;
    });

    @FunctionalInterface
    public interface ModifyCost {
        float modifyCost(PlayerEntity player, StaminaCostSource source, float cost);
    }

    @FunctionalInterface
    public interface ModifyRegen {
        float modifyRegen(PlayerEntity player, float regen);
    }

}
