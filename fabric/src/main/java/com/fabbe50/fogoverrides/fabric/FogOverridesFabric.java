package com.fabbe50.fogoverrides.fabric;

import com.fabbe50.fogoverrides.FogOverrides;
import net.fabricmc.api.ModInitializer;

public class FogOverridesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FogOverrides.init();
    }
}
