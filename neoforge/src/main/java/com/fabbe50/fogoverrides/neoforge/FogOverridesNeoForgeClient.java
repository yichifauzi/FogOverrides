package com.fabbe50.fogoverrides.neoforge;

import com.fabbe50.fogoverrides.ClothScreen;
import com.fabbe50.fogoverrides.FogOverrides;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ConfigScreenHandler;

public class FogOverridesNeoForgeClient {
    public static void initClient() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
        if (FMLEnvironment.dist.isClient()) {
            FogOverrides.clientInit();
            registerConfigScreen();
        }
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> ClothScreen.getConfigScreen(screen)));
    }
}
