package com.fabbe50.fogoverrides.neoforge;

import com.fabbe50.fogoverrides.FogOverrides;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(FogOverrides.MOD_ID)
public class FogOverridesNeoForge {
    public FogOverridesNeoForge(IEventBus bus) {
        FogOverrides.init();
        if (FMLEnvironment.dist.isClient()) {
            FogOverridesNeoForgeClient.initClient();
        }
    }
}
