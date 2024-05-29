package com.fabbe50.fogoverrides.forge;

import dev.architectury.platform.forge.EventBuses;
import com.fabbe50.fogoverrides.FogOverrides;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FogOverrides.MOD_ID)
public class FogOverridesForge {
    public FogOverridesForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FogOverrides.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FogOverrides.init();


        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> FogOverridesForgeClient::initClient);
    }

}
