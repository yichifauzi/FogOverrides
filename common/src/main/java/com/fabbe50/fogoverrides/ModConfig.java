package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.ModFogData;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.platform.Platform;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ModConfig {
    private static File configFile;

    private static long serverSettingsLastUpdated = 0L;

    private static final List<ResourceLocation> biomeList = new ArrayList<>();
    private static final Map<ResourceLocation, ModFogData> biomeStorage = new HashMap<>();

    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "text.fogoverrides.keybinds.open_menu",

            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F8,
            "text.fogoverrides.keybinds"
    );

    // Config Values
    public static boolean spectatorHasModFog = false;
    public static float spectatorNearDistance = -1f;
    public static float spectatorFarDistance = -1f;
    public static float spectatorWaterNearDistance = -1f;
    public static float spectatorWaterFarDistance = -1f;
    public static float spectatorLavaNearDistance = -1f;
    public static float spectatorLavaFarDistance = -1f;
    public static boolean creativeHasModFog = false;
    public static float creativeNearDistance = -1f;
    public static float creativeFarDistance = -1f;
    public static float creativeWaterNearDistance = -1f;
    public static float creativeWaterFarDistance = -1f;
    public static float creativeLavaNearDistance = -1f;
    public static float creativeLavaFarDistance = -1f;

    public static ModFogData overworldFogData = Utilities.getDefaultFogData();
    public static ModFogData netherFogData = Utilities.getDefaultFogData();
    public static ModFogData theEndFogData = Utilities.getDefaultFogData();

    public static int cloudHeight = 192;

    public static boolean renderWaterOverlay = true;
    public static boolean renderFireOverlay = true;
    public static int fireOverlayOffset = 0;
    public static int firePotionOverlayOffset = -25;

    public static void register() {
        configFile = new File(Platform.getConfigFolder().toFile(), "fogoverrides.properties");
        load(configFile);
    }

    public static void load(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fis);
            fis.close();

            serverSettingsLastUpdated = Long.parseLong((String)properties.computeIfAbsent("serverSettingsLastUpdated", o -> String.valueOf(System.currentTimeMillis())));

            spectatorHasModFog = ((String) properties.computeIfAbsent("spectatorHasModFog", o -> "false")).equalsIgnoreCase("true");
            spectatorNearDistance = Float.parseFloat((String) properties.computeIfAbsent("spectatorNearDistance", o -> "-1"));
            spectatorFarDistance = Float.parseFloat((String) properties.computeIfAbsent("spectatorFarDistance", o -> "-1"));
            spectatorWaterNearDistance = Float.parseFloat((String) properties.computeIfAbsent("spectatorWaterNearDistance", o -> "-1"));
            spectatorWaterFarDistance = Float.parseFloat((String) properties.computeIfAbsent("spectatorWaterFarDistance", o -> "-1"));
            spectatorLavaNearDistance = Float.parseFloat((String) properties.computeIfAbsent("spectatorLavaNearDistance", o -> "-1"));
            spectatorLavaFarDistance = Float.parseFloat((String) properties.computeIfAbsent("spectatorLavaFarDistance", o -> "-1"));
            creativeHasModFog = ((String) properties.computeIfAbsent("creativeHasModFog", o -> "false")).equalsIgnoreCase("true");
            creativeNearDistance = Float.parseFloat((String) properties.computeIfAbsent("creativeNearDistance", o -> "-1"));
            creativeFarDistance = Float.parseFloat((String) properties.computeIfAbsent("creativeFarDistance", o -> "-1"));
            creativeWaterNearDistance = Float.parseFloat((String) properties.computeIfAbsent("creativeWaterNearDistance", o -> "-1"));
            creativeWaterFarDistance = Float.parseFloat((String) properties.computeIfAbsent("creativeWaterFarDistance", o -> "-1"));
            creativeLavaNearDistance = Float.parseFloat((String) properties.computeIfAbsent("creativeLavaNearDistance", o -> "-1"));
            creativeLavaFarDistance = Float.parseFloat((String) properties.computeIfAbsent("creativeLavaFarDistance", o -> "-1"));

            overworldFogData = readModFogDataFromProperties(properties, Utilities.OVERWORLD);
            netherFogData = readModFogDataFromProperties(properties, Utilities.THE_NETHER);
            theEndFogData = readModFogDataFromProperties(properties, Utilities.THE_END);

            cloudHeight = Integer.parseInt((String) properties.computeIfAbsent("cloudHeight", o -> "192"));

            renderWaterOverlay = ((String) properties.computeIfAbsent("waterOverlay", o -> "true")).equalsIgnoreCase("true");
            renderFireOverlay = ((String) properties.computeIfAbsent("fireOverlay", o -> "true")).equalsIgnoreCase("true");
            fireOverlayOffset = Integer.parseInt((String) properties.computeIfAbsent("fireOffset", o -> "0"));
            firePotionOverlayOffset = Integer.parseInt((String) properties.computeIfAbsent("firePotOffset", o -> "-25"));

            for (ResourceLocation location : biomeList) {
                ModFogData fogData = readModFogDataFromProperties(properties, location);
                updateFogData(location, fogData);
                BiomeModifications.replaceProperties(biomeContext -> biomeContext.getKey().get().equals(location), (biomeContext, mutable) -> mutable.getEffectsProperties().setWaterColor(fogData.getWaterColor()));
            }
        } catch (IOException e) {
            for (ResourceLocation location : biomeList) {
                updateFogData(location, Utilities.getDefaultFogData());
            }
            try {
                save(file);
                load(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.err.println(e.toString());
        }
    }

    public static void save(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);

        Utilities.writeData(fos, "serverSettingsLastUpdated", String.valueOf(System.currentTimeMillis()));

        Utilities.writeData(fos, "spectatorHasModFog", String.valueOf(spectatorHasModFog));
        Utilities.writeData(fos, "spectatorNearDistance", String.valueOf(spectatorNearDistance));
        Utilities.writeData(fos, "spectatorFarDistance", String.valueOf(spectatorFarDistance));
        Utilities.writeData(fos, "spectatorWaterNearDistance", String.valueOf(spectatorWaterNearDistance));
        Utilities.writeData(fos, "spectatorWaterFarDistance", String.valueOf(spectatorWaterFarDistance));
        Utilities.writeData(fos, "spectatorLavaNearDistance", String.valueOf(spectatorLavaNearDistance));
        Utilities.writeData(fos, "spectatorLavaFarDistance", String.valueOf(spectatorLavaFarDistance));
        Utilities.writeData(fos, "creativeHasModFog", String.valueOf(creativeHasModFog));
        Utilities.writeData(fos, "creativeNearDistance", String.valueOf(creativeNearDistance));
        Utilities.writeData(fos, "creativeFarDistance", String.valueOf(creativeFarDistance));
        Utilities.writeData(fos, "creativeWaterNearDistance", String.valueOf(creativeWaterNearDistance));
        Utilities.writeData(fos, "creativeWaterFarDistance", String.valueOf(creativeWaterFarDistance));
        Utilities.writeData(fos, "creativeLavaNearDistance", String.valueOf(creativeLavaNearDistance));
        Utilities.writeData(fos, "creativeLavaFarDistance", String.valueOf(creativeLavaFarDistance));

        writeModFogDataToProperties(fos, Utilities.OVERWORLD, overworldFogData);
        writeModFogDataToProperties(fos, Utilities.THE_NETHER, netherFogData);
        writeModFogDataToProperties(fos, Utilities.THE_END, theEndFogData);

        Utilities.writeData(fos, "cloudHeight", String.valueOf(cloudHeight));

        Utilities.writeData(fos, "waterOverlay", String.valueOf(renderWaterOverlay));
        Utilities.writeData(fos, "fireOverlay", String.valueOf(renderFireOverlay));
        Utilities.writeData(fos, "fireOffset", String.valueOf(fireOverlayOffset));
        Utilities.writeData(fos, "firePotOffset", String.valueOf(firePotionOverlayOffset));

        for (ResourceLocation location : biomeStorage.keySet()) {
            ModFogData data = biomeStorage.get(location);
            writeModFogDataToProperties(fos, location, data);
        }
        fos.close();
    }

    private static ModFogData readModFogDataFromProperties(Properties properties, ResourceLocation location) {
        ModFogData defaults = Utilities.getDefaultFogData();

        boolean overrideFog = ((String)properties.computeIfAbsent("overrideFog_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideGameFog()))).equalsIgnoreCase("true");
        boolean isEnabled = ((String)properties.computeIfAbsent("fogEnabled_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isFogEnabled()))).equalsIgnoreCase("true");
        float nearDistance = Float.parseFloat((String) properties.computeIfAbsent("nearDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getNearDistance())));
        float farDistance = Float.parseFloat((String) properties.computeIfAbsent("farDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getFarDistance())));
        boolean overrideSkyColor = ((String)properties.computeIfAbsent("overrideSkyColor_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideSkyColor()))).equalsIgnoreCase("true");
        int skyColor = Integer.parseInt((String)properties.computeIfAbsent("skyColor_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getSkyColor())));
        boolean overrideFogColor = ((String)properties.computeIfAbsent("overrideFogColor_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideFogColor()))).equalsIgnoreCase("true");
        int fogColor = Integer.parseInt((String)properties.computeIfAbsent("fogColor_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getFogColor())));
        boolean overrideWaterFog = ((String)properties.computeIfAbsent("overrideWaterFog_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideWaterFog()))).equalsIgnoreCase("true");
        float waterNearDistance = Float.parseFloat((String) properties.computeIfAbsent("waterNearDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterNearDistance())));
        float waterFarDistance = Float.parseFloat((String) properties.computeIfAbsent("waterFarDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterFarDistance())));
        boolean waterPotionEffect = ((String)properties.computeIfAbsent("waterPotionEffect_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isWaterPotionEffect()))).equalsIgnoreCase("true");
        float waterPotionNearDistance = Float.parseFloat((String) properties.computeIfAbsent("waterPotionNearDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterPotionNearDistance())));
        float waterPotionFarDistance = Float.parseFloat((String) properties.computeIfAbsent("waterPotionFarDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterPotionFarDistance())));
        boolean overrideWaterColor = ((String)properties.computeIfAbsent("overrideWaterColor_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideWaterColor()))).equalsIgnoreCase("true");
        int waterColor = Integer.parseInt((String) properties.computeIfAbsent("waterColor_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterColor())));
        boolean overrideWaterFogColor = ((String)properties.computeIfAbsent("overrideWaterFogColor_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterFogColor()))).equalsIgnoreCase("true");
        int waterFogColor = Integer.parseInt((String) properties.computeIfAbsent("waterFogColor_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterFogColor())));
        boolean overrideLavaFog = ((String)properties.computeIfAbsent("overrideLavaFog_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideLavaFog()))).equalsIgnoreCase("true");
        float lavaNearDistance = Float.parseFloat((String) properties.computeIfAbsent("lavaNearDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getLavaNearDistance())));
        float lavaFarDistance = Float.parseFloat((String) properties.computeIfAbsent("lavaFarDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getLavaFarDistance())));
        boolean lavaPotionEffect = ((String)properties.computeIfAbsent("lavaPotionEffect_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isLavaPotionEffect()))).equalsIgnoreCase("true");
        float lavaPotionNearDistance = Float.parseFloat((String) properties.computeIfAbsent("lavaPotionNearDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getLavaPotionNearDistance())));
        float lavaPotionFarDistance = Float.parseFloat((String) properties.computeIfAbsent("lavaPotionFarDistance_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getLavaPotionFarDistance())));

        ModFogData fogData = new ModFogData(overrideFog, isEnabled, nearDistance, farDistance, overrideSkyColor, skyColor, overrideFogColor, fogColor, overrideWaterFog, waterNearDistance, waterFarDistance, overrideWaterColor, waterColor, overrideWaterFogColor, waterFogColor);
        fogData.setWaterPotionEffect(waterPotionEffect);
        fogData.setWaterPotionNearDistance(waterPotionNearDistance);
        fogData.setWaterPotionFarDistance(waterPotionFarDistance);
        fogData.setOverrideLavaFog(overrideLavaFog);
        fogData.setLavaNearDistance(lavaNearDistance);
        fogData.setLavaFarDistance(lavaFarDistance);
        fogData.setLavaPotionEffect(lavaPotionEffect);
        fogData.setLavaPotionNearDistance(lavaPotionNearDistance);
        fogData.setLavaPotionFarDistance(lavaPotionFarDistance);

        return fogData;
    }

    private static void writeModFogDataToProperties(FileOutputStream fos, ResourceLocation location, ModFogData data) throws IOException {
        Utilities.writeData(fos, "overrideFog_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideGameFog()));
        Utilities.writeData(fos, "fogEnabled_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isFogEnabled()));
        Utilities.writeData(fos, "nearDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getNearDistance()));
        Utilities.writeData(fos, "farDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getFarDistance()));
        Utilities.writeData(fos, "overrideSkyColor_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideSkyColor()));
        Utilities.writeData(fos, "skyColor_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getSkyColor()));
        Utilities.writeData(fos, "overrideFogColor_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideFogColor()));
        Utilities.writeData(fos, "fogColor_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getFogColor()));
        Utilities.writeData(fos, "overrideWaterFog_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideWaterFog()));
        Utilities.writeData(fos, "waterNearDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterNearDistance()));
        Utilities.writeData(fos, "waterFarDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterFarDistance()));
        Utilities.writeData(fos, "waterPotionEffect_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isWaterPotionEffect()));
        Utilities.writeData(fos, "waterPotionNearDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterPotionNearDistance()));
        Utilities.writeData(fos, "waterPotionFarDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterPotionFarDistance()));
        Utilities.writeData(fos, "overrideWaterColor_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideWaterColor()));
        Utilities.writeData(fos, "waterColor_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterColor()));
        Utilities.writeData(fos, "overrideWaterFogColor_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideWaterFogColor()));
        Utilities.writeData(fos, "waterFogColor_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterFogColor()));
        Utilities.writeData(fos, "overrideLavaFog_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideLavaFog()));
        Utilities.writeData(fos, "lavaNearDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getLavaNearDistance()));
        Utilities.writeData(fos, "lavaFarDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getLavaFarDistance()));
        Utilities.writeData(fos, "lavaPotionEffect_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isLavaPotionEffect()));
        Utilities.writeData(fos, "lavaPotionNearDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getLavaPotionNearDistance()));
        Utilities.writeData(fos, "lavaPotionFarDistance_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getLavaPotionFarDistance()));
    }

    public static void addBiomeToList(ResourceLocation location) {
        biomeList.add(location);
    }

    private static void addBiomeToStorage(ResourceLocation location, ModFogData fogData) {
        biomeStorage.put(location, fogData);
    }

    private static void replaceBiomeInStorage(ResourceLocation location, ModFogData fogData) {
        biomeStorage.replace(location, fogData);
    }

    public static void updateFogData(ResourceLocation location, ModFogData fogData) {
        if (biomeStorage.containsKey(location)) {
            replaceBiomeInStorage(location, fogData);
        } else {
            addBiomeToStorage(location, fogData);
        }
    }

    public static ModFogData getFogDataFromBiomeLocation(ResourceLocation biome) {
        return biomeStorage.getOrDefault(biome, Utilities.getDefaultFogData());
    }

    public static ModFogData getFogDataFromDimension(ResourceLocation dimension) {
        if (dimension.equals(Utilities.OVERWORLD)) {
            return ModConfig.overworldFogData;
        } else if (dimension.equals(Utilities.THE_NETHER)) {
            return ModConfig.netherFogData;
        } else if (dimension.equals(Utilities.THE_END)) {
            return ModConfig.theEndFogData;
        }
        return null;
    }

    public static Map<ResourceLocation, ModFogData> getBiomeStorage() {
        return biomeStorage;
    }

    public static long getServerSettingsLastUpdated() {
        return serverSettingsLastUpdated;
    }

    public static File getConfigFile() {
        return configFile;
    }
}
