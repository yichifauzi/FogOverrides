package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class MixinScreenEffectRenderer {
    @Shadow
    @Nullable
    private static BlockState getViewBlockingState(Player arg) {
        return null;
    }

    @Shadow
    private static void renderTex(TextureAtlasSprite arg, PoseStack arg2) {
    }

    @Shadow
    private static void renderWater(Minecraft arg, PoseStack arg2) {
    }

    @Shadow
    private static void renderFire(Minecraft arg, PoseStack arg2) {
    }

    @Inject(at = @At("HEAD"), method = "renderScreenEffect", cancellable = true)
    private static void injectRenderScreenEffect(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        CurrentDataStorage dataStorage = CurrentDataStorage.INSTANCE;
        Player player = minecraft.player;
        if (player != null) {
            if (!player.noPhysics) {
                BlockState blockState = getViewBlockingState(player);
                if (blockState != null) {
                    renderTex(minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(blockState), poseStack);
                }
            }
            if (!player.isSpectator()) {
                if (player.isEyeInFluid(FluidTags.WATER) && dataStorage.isRenderWaterOverlay()) {
                    renderWater(minecraft, poseStack);
                }
                if (player.isOnFire() && dataStorage.isRenderFireOverlay()) {
                    if (player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                        poseStack.translate(0, dataStorage.getFirePotionOverlayOffset() / 100f, 0);
                    } else {
                        poseStack.translate(0, dataStorage.getFireOverlayOffset() / 100f, 0);
                    }
                    renderFire(minecraft, poseStack);
                }
            }
        }
        ci.cancel();
    }
}
