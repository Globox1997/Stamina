package net.stamina.init;

import net.fabricmc.loader.api.FabricLoader;
import net.stamina.compat.DehydrationCompat;

public class CompatInit {

    public static void init(){
        if (FabricLoader.getInstance().isModLoaded("dehydration")) {
            DehydrationCompat.init();
        }
    }
}
