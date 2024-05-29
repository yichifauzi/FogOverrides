package com.fabbe50.fogoverrides.fabric;

import com.fabbe50.fogoverrides.FogOverridesExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FogOverridesExpectPlatformImpl {
    /**
     * This is our actual method to {@link FogOverridesExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
