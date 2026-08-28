package net.fayber.unlimitedtrialvaults;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

// registers our config screen with ModMenu so it shows up on the Mods
// screen. client-only, dedicated servers never load this class. uses the
// cloth-config screen if that mod's installed, otherwise the hand-rolled one.
public class VaultModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return VaultClothScreen::create;
        }
        return VaultModConfigScreen::new;
    }
}
