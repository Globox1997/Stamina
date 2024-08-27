package net.stamina.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "stamina")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class StaminaConfig implements ConfigData {

    @Comment("Is in %")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int minSprintStamina = 17;
    @Comment("In ticks")
    public int exhaustionTime = 40;

}
