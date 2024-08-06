package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModFogData;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class NetworkHandler {
    public static List<Player> modUsers = new ArrayList<>();

    public static final ResourceLocation MOD_HANDSHAKE = new ResourceLocation("fogoverrides", "handshake");
    public static final ResourceLocation DIMENSION_FOG_PACKET = new ResourceLocation("fogoverrides", "dimension_fog");
    public static final ResourceLocation BIOME_FOG_PACKET = new ResourceLocation("fogoverrides", "biome_fog");
    public static final ResourceLocation SPECTATOR_FOG_PACKET = new ResourceLocation("fogoverrides", "spectator_fog");
    public static final ResourceLocation CREATIVE_FOG_PACKET = new ResourceLocation("fogoverrides", "creative_fog");
    public static final ResourceLocation CLOUD_PACKET = new ResourceLocation("fogoverrides", "clouds");
    public static final ResourceLocation OVERLAYS_PACKET = new ResourceLocation("fogoverrides", "overlays");

    public static void registerHandlers() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), MOD_HANDSHAKE, (buf, context) -> {
            if (Utilities.isIntegratedServer()) {
                CurrentDataStorage.INSTANCE.setIntegratedServer(true);
            }
            CurrentDataStorage.INSTANCE.setOnFogOverridesEnabledServer(buf.readBoolean());
        });
        NetworkManager.registerReceiver(NetworkManager.s2c(), SPECTATOR_FOG_PACKET, (buf, context) -> {
            boolean spectatorHasModFog = buf.readBoolean();
            float spectatorNearDistance = buf.readFloat();
            float spectatorFarDistance = buf.readFloat();
            float spectatorWaterNearDistance = buf.readFloat();
            float spectatorWaterFarDistance = buf.readFloat();
            float spectatorLavaNearDistance = buf.readFloat();
            float spectatorLavaFarDistance = buf.readFloat();
            CurrentDataStorage.INSTANCE.updateSpectatorSettings(spectatorHasModFog, spectatorNearDistance, spectatorFarDistance, spectatorWaterNearDistance, spectatorWaterFarDistance, spectatorLavaNearDistance, spectatorLavaFarDistance);
        });
        NetworkManager.registerReceiver(NetworkManager.s2c(), CREATIVE_FOG_PACKET, (buf, context) -> {
            boolean creativeHasModFog = buf.readBoolean();
            float creativeNearDistance = buf.readFloat();
            float creativeFarDistance = buf.readFloat();
            float creativeWaterNearDistance = buf.readFloat();
            float creativeWaterFarDistance = buf.readFloat();
            float creativeLavaNearDistance = buf.readFloat();
            float creativeLavaFarDistance = buf.readFloat();
            CurrentDataStorage.INSTANCE.updateCreativeSettings(creativeHasModFog, creativeNearDistance, creativeFarDistance, creativeWaterNearDistance, creativeWaterFarDistance, creativeLavaNearDistance, creativeLavaFarDistance);
        });
        NetworkManager.registerReceiver(NetworkManager.s2c(), CLOUD_PACKET, (buf, context) -> {
            CurrentDataStorage.INSTANCE.updateCloudHeight(buf.readInt());
        });
        NetworkManager.registerReceiver(NetworkManager.s2c(), OVERLAYS_PACKET, (buf, context) -> {
            boolean waterOverlay = buf.readBoolean();
            boolean fireOverlay = buf.readBoolean();
            int fireOffset = buf.readInt();
            int firePotOffset = buf.readInt();
            CurrentDataStorage.INSTANCE.updateOverlays(waterOverlay, fireOverlay, fireOffset, firePotOffset);
        });
        NetworkManager.registerReceiver(NetworkManager.s2c(), DIMENSION_FOG_PACKET, (buf, context) -> {
            if (buf.readerIndex() > 0) {
                ResourceLocation location = buf.readResourceLocation();
                boolean overrideBiomeFog = buf.readBoolean();
                boolean biomeFogEnabled = buf.readBoolean();
                boolean overrideSkyColor = buf.readBoolean();
                boolean overrideFogColor = buf.readBoolean();
                boolean overrideWaterFog = buf.readBoolean();
                boolean waterPotionEffect = buf.readBoolean();
                boolean overrideWaterColor = buf.readBoolean();
                boolean overrideWaterFogColor = buf.readBoolean();
                boolean overrideLavaFog = buf.readBoolean();
                boolean lavaPotionEffect = buf.readBoolean();
                // nearDistance, farDistance, skyColor, fogColor, waterNear, waterFar, waterPotNear, waterPotFar, waterColor, waterFogColor, lavaNear, lavaFar, lavaPotNear, lavaPotFar
                int[] dataVariables = buf.readVarIntArray();
                ModFogData fogData = new ModFogData(overrideBiomeFog, biomeFogEnabled, dataVariables[0], dataVariables[1], overrideSkyColor, dataVariables[2], overrideFogColor, dataVariables[3], overrideWaterFog, dataVariables[4], dataVariables[5], overrideWaterColor, dataVariables[8], overrideWaterFogColor, dataVariables[9]);
                fogData.setWaterPotionEffect(waterPotionEffect);
                fogData.setWaterPotionNearDistance(dataVariables[6]);
                fogData.setWaterPotionFarDistance(dataVariables[7]);
                fogData.setOverrideLavaFog(overrideLavaFog);
                fogData.setLavaNearDistance(dataVariables[10]);
                fogData.setLavaFarDistance(dataVariables[11]);
                fogData.setLavaPotionEffect(lavaPotionEffect);
                fogData.setLavaPotionNearDistance(dataVariables[12]);
                fogData.setLavaPotionFarDistance(dataVariables[13]);
                if (location.equals(Utilities.getOverworld())) {
                    CurrentDataStorage.INSTANCE.updateOverworldFogData(fogData);
                } else if (location.equals(Utilities.getNether())) {
                    CurrentDataStorage.INSTANCE.updateNetherFogData(fogData);
                } else if (location.equals(Utilities.getTheEnd())) {
                    CurrentDataStorage.INSTANCE.updateTheEndFogData(fogData);
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.s2c(), BIOME_FOG_PACKET, (buf, context) -> {
            if (buf.readerIndex() > 0) {
                ResourceLocation location = buf.readResourceLocation();
                boolean overrideBiomeFog = buf.readBoolean();
                boolean biomeFogEnabled = buf.readBoolean();
                boolean overrideSkyColor = buf.readBoolean();
                boolean overrideFogColor = buf.readBoolean();
                boolean overrideWaterFog = buf.readBoolean();
                boolean waterPotionEffect = buf.readBoolean();
                boolean overrideWaterColor = buf.readBoolean();
                boolean overrideWaterFogColor = buf.readBoolean();
                boolean overrideLavaFog = buf.readBoolean();
                boolean lavaPotionEffect = buf.readBoolean();
                // nearDistance, farDistance, skyColor, fogColor, waterNear, waterFar, waterPotNear, waterPotFar, waterColor, waterFogColor, lavaNear, lavaFar, lavaPotNear, lavaPotFar
                int[] dataVariables = buf.readVarIntArray();
                ModFogData fogData = new ModFogData(overrideBiomeFog, biomeFogEnabled, dataVariables[0], dataVariables[1], overrideSkyColor, dataVariables[2], overrideFogColor, dataVariables[3], overrideWaterFog, dataVariables[4], dataVariables[5], overrideWaterColor, dataVariables[8], overrideWaterFogColor, dataVariables[9]);
                fogData.setWaterPotionEffect(waterPotionEffect);
                fogData.setWaterPotionNearDistance(dataVariables[6]);
                fogData.setWaterPotionFarDistance(dataVariables[7]);
                fogData.setOverrideLavaFog(overrideLavaFog);
                fogData.setLavaNearDistance(dataVariables[10]);
                fogData.setLavaFarDistance(dataVariables[11]);
                fogData.setLavaPotionEffect(lavaPotionEffect);
                fogData.setLavaPotionNearDistance(dataVariables[12]);
                fogData.setLavaPotionFarDistance(dataVariables[13]);
                CurrentDataStorage.INSTANCE.addToBiomeStorage(location, fogData);
                CurrentDataStorage.INSTANCE.refreshWaterColor(location, fogData);
            }
        });
    }

    public static void registerClientHandshake() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(localPlayer -> {
            if (localPlayer.is(Utilities.getClientPlayer())) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeBoolean(true);
                NetworkManager.sendToServer(MOD_HANDSHAKE, buf);
            }
        });
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            if (player == null || player.is(Utilities.getClientPlayer())) {
                CurrentDataStorage.INSTANCE.setOnFogOverridesEnabledServer(false);
                CurrentDataStorage.INSTANCE.setIntegratedServer(false);
            }
        });
    }

    public static void registerServerHandshake() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, MOD_HANDSHAKE, (buf, context) -> {
            if (buf.readBoolean()) {
                if (!modUsers.contains(context.getPlayer()))
                    modUsers.add(context.getPlayer());
                buf.clear();
                buf.writeBoolean(true);
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), MOD_HANDSHAKE, buf);
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), SPECTATOR_FOG_PACKET, getSpectatorSettingsBuffer());
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), CREATIVE_FOG_PACKET, getCreativeSettingsBuffer());
                ResourceLocation[] dimensionLocations = new ResourceLocation[] {Utilities.getOverworld(), Utilities.getNether(), Utilities.getTheEnd()};
                for (ResourceLocation location : dimensionLocations) {
                    NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), DIMENSION_FOG_PACKET, getDimensionBuffer(location));
                }
                for (ResourceLocation location : ModConfig.getBiomeStorage().keySet()) {
                    NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), BIOME_FOG_PACKET, getBiomeBuffer(location));
                }
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), CLOUD_PACKET, getCloudBuffer());
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), OVERLAYS_PACKET, getOverlaysBuffer());
            }
        });
        PlayerEvent.PLAYER_QUIT.register(player -> {
            modUsers.remove(player);
        });
    }

    public static FriendlyByteBuf getSpectatorSettingsBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(ModConfig.spectatorHasModFog);
        buf.writeFloat(ModConfig.spectatorNearDistance);
        buf.writeFloat(ModConfig.spectatorFarDistance);
        buf.writeFloat(ModConfig.spectatorWaterNearDistance);
        buf.writeFloat(ModConfig.spectatorWaterFarDistance);
        buf.writeFloat(ModConfig.spectatorLavaNearDistance);
        buf.writeFloat(ModConfig.spectatorLavaFarDistance);
        return buf;
    }

    public static FriendlyByteBuf getCreativeSettingsBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(ModConfig.creativeHasModFog);
        buf.writeFloat(ModConfig.creativeNearDistance);
        buf.writeFloat(ModConfig.creativeFarDistance);
        buf.writeFloat(ModConfig.creativeWaterNearDistance);
        buf.writeFloat(ModConfig.creativeWaterFarDistance);
        buf.writeFloat(ModConfig.creativeLavaNearDistance);
        buf.writeFloat(ModConfig.creativeLavaFarDistance);
        return buf;
    }

    public static FriendlyByteBuf getCloudBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(ModConfig.cloudHeight);
        return buf;
    }

    public static FriendlyByteBuf getOverlaysBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(ModConfig.renderWaterOverlay);
        buf.writeBoolean(ModConfig.renderFireOverlay);
        buf.writeInt(ModConfig.fireOverlayOffset);
        buf.writeInt(ModConfig.firePotionOverlayOffset);
        return buf;
    }

    public static FriendlyByteBuf getDimensionBuffer(ResourceLocation location) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ModFogData fogData = ModConfig.getFogDataFromDimension(location);
        if (fogData != null) {
            buf.writeResourceLocation(location);
            buf.writeBoolean(fogData.isOverrideGameFog());
            buf.writeBoolean(fogData.isFogEnabled());
            buf.writeBoolean(fogData.isOverrideSkyColor());
            buf.writeBoolean(fogData.isOverrideFogColor());
            buf.writeBoolean(fogData.isOverrideWaterFog());
            buf.writeBoolean(fogData.isWaterPotionEffect());
            buf.writeBoolean(fogData.isOverrideWaterColor());
            buf.writeBoolean(fogData.isOverrideWaterFogColor());
            buf.writeBoolean(fogData.isOverrideLavaFog());
            buf.writeBoolean(fogData.isLavaPotionEffect());
            int[] dataVariables = new int[]{(int) fogData.getNearDistance(), (int) fogData.getFarDistance(), fogData.getSkyColor(), fogData.getFogColor(), (int) fogData.getWaterNearDistance(), (int) fogData.getWaterFarDistance(), (int) fogData.getWaterPotionNearDistance(), (int) fogData.getWaterPotionFarDistance(), fogData.getWaterColor(), fogData.getWaterFogColor(), (int) fogData.getLavaNearDistance(), (int) fogData.getLavaFarDistance(), (int) fogData.getLavaPotionNearDistance(), (int) fogData.getLavaPotionFarDistance()};
            buf.writeVarIntArray(dataVariables);
        }
        return buf;
    }

    public static FriendlyByteBuf getBiomeBuffer(ResourceLocation location) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ModFogData fogData = ModConfig.getFogDataFromBiomeLocation(location);
        if (fogData != null) {
            buf.writeResourceLocation(location);
            buf.writeBoolean(fogData.isOverrideGameFog());
            buf.writeBoolean(fogData.isFogEnabled());
            buf.writeBoolean(fogData.isOverrideSkyColor());
            buf.writeBoolean(fogData.isOverrideFogColor());
            buf.writeBoolean(fogData.isOverrideWaterFog());
            buf.writeBoolean(fogData.isWaterPotionEffect());
            buf.writeBoolean(fogData.isOverrideWaterColor());
            buf.writeBoolean(fogData.isOverrideWaterFogColor());
            buf.writeBoolean(fogData.isOverrideLavaFog());
            buf.writeBoolean(fogData.isLavaPotionEffect());
            int[] dataVariables = new int[]{(int) fogData.getNearDistance(), (int) fogData.getFarDistance(), fogData.getSkyColor(), fogData.getFogColor(), (int) fogData.getWaterNearDistance(), (int) fogData.getWaterFarDistance(), (int) fogData.getWaterPotionNearDistance(), (int) fogData.getWaterPotionFarDistance(), fogData.getWaterColor(), fogData.getWaterFogColor(), (int) fogData.getLavaNearDistance(), (int) fogData.getLavaFarDistance(), (int) fogData.getLavaPotionNearDistance(), (int) fogData.getLavaPotionFarDistance()};
            buf.writeVarIntArray(dataVariables);
        }
        return buf;
    }
}
