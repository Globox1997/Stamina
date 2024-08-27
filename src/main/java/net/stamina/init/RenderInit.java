package net.stamina.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.stamina.access.StaminaAccess;

@Environment(EnvType.CLIENT)
public class RenderInit {

    private static final Identifier ICONS = Identifier.of("stamina", "textures/gui/stamina_icons.png");

    public static void init() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!client.options.hudHidden && client.interactionManager.hasExperienceBar() && client.player != null) {
                int stamina = ((StaminaAccess) client.player).getStamina();

                if (stamina > 0) {
                    int maxStamina = (int) client.player.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
                    drawContext.drawTexture(ICONS, drawContext.getScaledWindowWidth() / 2 - 91, drawContext.getScaledWindowHeight() - 32 + 6, 0, 3, (int) (182.0f * ((float) stamina / maxStamina)),
                            2);
                }
            }
        });
    }
}
