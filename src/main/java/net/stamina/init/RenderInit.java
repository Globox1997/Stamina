package net.stamina.init;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.stamina.access.StaminaAccess;
import net.stamina.config.StaminaConfig;
import net.stamina.util.StaminaHelper;

@Environment(EnvType.CLIENT)
public class RenderInit {

    private static final Identifier ICONS = Identifier.of("stamina", "textures/gui/stamina_icons.png");
    private static final int BAR_WIDTH = 182;
    private static final int FADE_DELAY = 40;
    private static final int FADE_TICKS = 20;

    private static int fullStaminaTicks = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && ((StaminaAccess) client.player).getStamina() >= (int) client.player.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA)) {
                fullStaminaTicks++;
            } else {
                fullStaminaTicks = 0;
            }
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (client.options.hudHidden || player == null || client.interactionManager == null || !client.interactionManager.hasExperienceBar() || player.getJumpingMount() != null) {
                return;
            }
            int stamina = ((StaminaAccess) player).getStamina();
            if (stamina <= 0) {
                return;
            }
            StaminaConfig config = ConfigInit.CONFIG;
            float alpha = 1.0f;
            if (config.hudFadeWhenFull && fullStaminaTicks > FADE_DELAY) {
                alpha = Math.max(0.0f, 1.0f - (fullStaminaTicks - FADE_DELAY - 1 + tickCounter.getTickDelta(false)) / FADE_TICKS);
            }
            if (alpha <= 0.0f) {
                return;
            }
            renderStaminaBar(drawContext, client, player, stamina, config, alpha);
        });
    }

    private static void renderStaminaBar(DrawContext drawContext, MinecraftClient client, ClientPlayerEntity player, int stamina, StaminaConfig config, float alpha) {
        int maxStamina = (int) player.getAttributeValue(AttributeInit.GENERIC_MAX_STAMINA);
        int sprintThreshold = StaminaHelper.getSprintThreshold(player);
        int x = drawContext.getScaledWindowWidth() / 2 - 91 + config.hudOffsetX;
        int y = drawContext.getScaledWindowHeight() - 32 + 6 + config.hudOffsetY;
        float progress = Math.min(1.0f, (float) stamina / maxStamina);

        RenderSystem.enableBlend();
        if (config.hudColorBelowSprintThreshold && stamina <= sprintThreshold) {
            drawContext.setShaderColor(1.0f, 0.45f, 0.45f, alpha);
        } else {
            drawContext.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        }
        drawContext.drawTexture(ICONS, x, y, 0, 3, (int) (BAR_WIDTH * progress), 2);
        drawContext.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (config.hudShowSprintThreshold && sprintThreshold > 0 && sprintThreshold < maxStamina) {
            int markerX = x + (int) (BAR_WIDTH * ((float) sprintThreshold / maxStamina));
            drawContext.fill(markerX, y - 1, markerX + 1, y + 2, ColorHelper.Argb.getArgb((int) (alpha * 200), 255, 255, 255));
        }
        if (config.hudShowNumber && alpha > 0.05f) {
            drawContext.drawText(client.textRenderer, stamina + "/" + maxStamina, x + BAR_WIDTH + 4, y - 3, ColorHelper.Argb.getArgb((int) (alpha * 255), 255, 255, 255), true);
        }
        RenderSystem.disableBlend();
    }
}
