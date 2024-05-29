package com.fabbe50.fogoverrides.fabric;

import com.fabbe50.fogoverrides.ClothScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClothScreen::getConfigScreen;
    }
}
