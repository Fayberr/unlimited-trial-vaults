package net.fayber.unlimitedtrialvaults;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// cloth config screen, used instead of the hand-rolled VaultModConfigScreen
// when cloth-config is installed. optional dependency, not required.
public final class VaultClothScreen {

    private VaultClothScreen() {}

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Unlimited Trial Vaults"));

        ConfigEntryBuilder eb = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        ModConfig c = ModConfig.get();
        general.addEntry(bool(eb, "normal_vault_unlimited", "Normal Vaults Unlimited",
                true, c.normal_vault_unlimited,
                "Normal vaults can be unlocked unlimited times per player (keys still consumed)."));
        general.addEntry(bool(eb, "ominous_vault_unlimited", "Ominous Vaults Unlimited",
                true, c.ominous_vault_unlimited,
                "Ominous vaults can be unlocked unlimited times per player (keys still consumed)."));
        general.addEntry(eb.startIntField(Component.literal("Spawner Re-Challenge Delay (seconds)"),
                        c.spawner_cooldown_seconds)
                .setDefaultValue(-1)
                .setTooltip(Component.literal(
                        "How long trial spawners stay in cooldown after a challenge. -1 = vanilla, 0 = instantly re-triggerable."))
                .setSaveConsumer(value -> ModConfig.set("spawner_cooldown_seconds", String.valueOf(value)))
                .build());

        return builder.build();
    }

    private static AbstractConfigListEntry<?> bool(ConfigEntryBuilder eb, String key, String label,
                                                   boolean defaultValue, boolean current, String tooltip) {
        return eb.startBooleanToggle(Component.literal(label), current)
                .setDefaultValue(defaultValue)
                .setTooltip(Component.literal(tooltip))
                .setSaveConsumer(value -> ModConfig.set(key, String.valueOf(value)))
                .build();
    }
}
