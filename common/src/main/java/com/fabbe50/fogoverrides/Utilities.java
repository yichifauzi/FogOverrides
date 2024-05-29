package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModFogData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import java.io.FileOutputStream;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class Utilities {
    public static final ResourceLocation OVERWORLD = Level.OVERWORLD.location();
    public static final ResourceLocation THE_NETHER = Level.NETHER.location();
    public static final ResourceLocation THE_END = Level.END.location();

    public static int getColorIntegerFromRGB(int[] rgb) {
        return getColorIntegerFromRGB(rgb[0], rgb[1], rgb[2]);
    }

    public static int getColorIntegerFromVec3(Vec3 vec3) {
        return getColorIntegerFromRGB(vec3.x, vec3.y, vec3.z);
    }

    public static int getColorIntegerFromRGB(double r, double g, double b) {
        return getColorIntegerFromRGB((int)r, (int)g, (int)b);
    }

    public static int getColorIntegerFromRGB(float r, float g, float b) {
        return getColorIntegerFromRGB((int)r, (int)g, (int)b);
    }

    public static int getColorIntNoAlphaFromColor(Color color) {
        return getColorIntegerFromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static int getColorIntegerFromRGB(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

    public static int[] getRGBFromColorInteger(int color) {
        int r = ((color >> 16) & 255);
        int g = ((color >> 8) & 255);
        int b = (color & 255);
        return new int[] {r, g, b};
    }

    public static Vec3 getVec3ColorFromInteger(int color) {
        int[] rgb = getRGBFromColorInteger(color);
        return new Vec3(rgb[0] / 255D, rgb[1] / 255D, rgb[2] / 255D);
    }

    public static String getFormattedColor(int r, int g, int b) {
        return getFormattedColor(getColorIntegerFromRGB(r, g, b));
    }

    public static String getFormattedColor(int[] color) {
        return getFormattedColor(getColorIntegerFromRGB(color));
    }

    public static String getFormattedColor(int color) {
        int[] rgb = getRGBFromColorInteger(color);

        return color == -1 ? "{null}" : String.format("{R: %s, G: %s, B: %s}", rgb[0], rgb[1], rgb[2]);
    }

    public static int getBlendedColor(int originalColor, int blendColor, float blendingRatio) {
        if (blendColor != -1) {
            if (blendingRatio > 1f) {
                blendingRatio = 1f;
            } else if (blendingRatio < 0f) {
                blendingRatio = 0f;
            }
            float iRatio = 1.0f - blendingRatio;

            int oR = ((originalColor & 0xff0000) >> 16);
            int oG = ((originalColor & 0xff00) >> 8);
            int oB = (originalColor & 0xff);

            int tR = ((blendColor & 0xff0000) >> 16);
            int tG = ((blendColor & 0xff00) >> 8);
            int tB = (blendColor & 0xff);

            int newR = (int) ((oR * iRatio) + (tR * blendingRatio));
            int newG = (int) ((oG * iRatio) + (tG * blendingRatio));
            int newB = (int) ((oB * iRatio) + (tB * blendingRatio));

            return newR << 16 | newG << 8 | newB;
        }
        return originalColor;
    }

    public static int getCurrentSkyColor() {
        Biome biome = getCurrentBiome();
        if (biome != null) {
            ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(Utilities.getCurrentBiomeLocation());
            if (data != null && data.getSkyColor() != -1) {
                if (data.isOverrideSkyColor()) {
                    return data.getSkyColor();
                }
            }
            return biome.getSpecialEffects().getSkyColor();
        }
        return -1;
    }

    public static int getCurrentFogColor() {
        Biome biome = getCurrentBiome();
        if (biome != null) {
            ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(Utilities.getCurrentBiomeLocation());
            if (data != null && data.getFogColor() != -1) {
                if (data.isOverrideFogColor()) {
                    return data.getFogColor();
                }
            }
            return biome.getSpecialEffects().getFogColor();
        }
        return -1;
    }

    public static int getCurrentWaterColor() {
        ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(Utilities.getCurrentBiomeLocation());
        //TODO: Set water color per block location instead.
        if (data != null && data.getWaterColor() != -1) {
            if (data.isOverrideWaterColor()) {
                return data.getWaterColor();
            }
        }
        return -1;
    }

    public static int getWaterColorAtLocation(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level != null) {
            Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
            if (biomeKey.isPresent()) {
                ResourceLocation location = biomeKey.get().location();
                ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(location);
                if (data != null && data.getWaterColor() != -1) {
                    if (data.isOverrideWaterColor()) {
                        return data.getWaterColor();
                    }
                }
            }
            Biome biome = level.getBiome(pos).value();
            return biome.getWaterColor();
        }
        return -1;
    }

    public static int getCurrentWaterFogColor() {
        Biome biome = getCurrentBiome();
        if (biome != null) {
            ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(Utilities.getCurrentBiomeLocation());
            if (data != null && data.getWaterFogColor() != -1) {
                if (data.isOverrideWaterFogColor()) {
                    return data.getWaterFogColor();
                }
            }
            return biome.getSpecialEffects().getWaterFogColor();
        }
        return -1;
    }

    public static Biome getCurrentBiome() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null) {
            try (Level level = cameraEntity.level()) {
                BlockPos pos = cameraEntity.getOnPos();
                return level.getBiome(pos).value();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static ResourceLocation getCurrentDimensionLocation() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null) {
            try (Level level = cameraEntity.level()) {
                return level.dimension().location();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static ResourceLocation getCurrentBiomeLocation() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null) {
            try (Level level = cameraEntity.level()) {
                BlockPos pos = cameraEntity.getOnPos();
                Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
                if (biomeKey.isPresent()) {
                    return biomeKey.get().location();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static boolean isIntegratedServer() {
        return Minecraft.getInstance().isLocalServer();
    }

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static ClientLevel getClientLevel() {
        return Minecraft.getInstance().level;
    }

    public static ModFogData getDefaultFogData() {
        return new ModFogData(-1, -1, 7907327, 12638463, -8.0f, 96.0f, 4159204, 329011);
    }

    public static void writeData(FileOutputStream fos, String key, String value) throws IOException {
        fos.write((key + "=" + value).getBytes());
        fos.write("\n".getBytes());
    }

    public static String capitalizeFirstInEveryWord(String input) {
        String[] words = input.split("\\s");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(Character.toTitleCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return result.toString().trim();
    }
}
