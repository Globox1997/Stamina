package net.stamina.init;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.stamina.config.StaminaConfig;
import net.stamina.util.StaminaHelper;

public class ConfigInit {

    public static final Gson GSON = new Gson();

    public static StaminaConfig CONFIG = new StaminaConfig();
    @Nullable
    public static StaminaConfig SYNCED_CONFIG = null;
    @Nullable
    private static MinecraftServer server = null;

    public static void init() {
        ConfigHolder<StaminaConfig> holder = AutoConfig.register(StaminaConfig.class, JanksonConfigSerializer::new);
        CONFIG = holder.getConfig();

        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> server = minecraftServer);
        ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> server = null);
        holder.registerSaveListener((configHolder, config) -> {
            config.validatePostLoad();
            MinecraftServer minecraftServer = server;
            if (minecraftServer != null) {
                minecraftServer.execute(() -> minecraftServer.getPlayerManager().getPlayerList().forEach(StaminaHelper::syncConfig));
            }
            return ActionResult.SUCCESS;
        });
    }

    public static StaminaConfig get(World world) {
        StaminaConfig synced = SYNCED_CONFIG;
        if (world.isClient() && synced != null) {
            return synced;
        }
        return CONFIG;
    }

}
