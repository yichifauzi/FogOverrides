package com.fabbe50.fogoverrides.neoforge;

import com.fabbe50.fogoverrides.FogOverridesExpectPlatform;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class FogOverridesExpectPlatformImpl {
    /**
     * This is our actual method to {@link FogOverridesExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
