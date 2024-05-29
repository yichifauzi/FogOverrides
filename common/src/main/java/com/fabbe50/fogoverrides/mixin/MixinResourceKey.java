package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.ModConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourceKey.class)
public class MixinResourceKey {
    @Inject(at = @At("HEAD"), method = "create(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceKey;")
    private static <T> void injectCreate(ResourceKey<? extends Registry<T>> resourceKey, ResourceLocation resourceLocation, CallbackInfoReturnable<ResourceKey<T>> cir) {
        if (resourceKey.equals(Registries.BIOME)) {
            ModConfig.addBiomeToList(resourceLocation);
        }
    }
}
