package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.Utilities;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class MixinBiome {
    @Inject(at = @At("RETURN"), method = "getSkyColor", cancellable = true)
    public void injectGetSkyColor(CallbackInfoReturnable<Integer> cir) {
        int color = Utilities.getCurrentSkyColor();
        if (color != -1) {
            cir.setReturnValue(color);
        }
    }

    @Inject(at = @At("RETURN"), method = "getFogColor", cancellable = true)
    public void injectGetFogColor(CallbackInfoReturnable<Integer> cir) {
        int color = Utilities.getCurrentFogColor();
        if (color != -1) {
            cir.setReturnValue(color);
        }
    }

    @Inject(at = @At("RETURN"), method = "getWaterFogColor", cancellable = true)
    public void injectGetWaterFogColor(CallbackInfoReturnable<Integer> cir) {
        int color = Utilities.getCurrentWaterFogColor();
        if (color != -1) {
            cir.setReturnValue(color);
        }
    }
}
