package net.fayber.unlimitedtrialvaults;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// fallback config screen for when cloth-config isn't installed. every
// control writes straight through ModConfig.set(), which updates the
// in-memory config and saves it to disk. in singleplayer the integrated
// server reads that same static config, so changes take effect live.
public class VaultModConfigScreen extends Screen {
    private static final int START_Y = 25;
    private static final int SPACING = 26;

    private final Screen parent;

    public VaultModConfigScreen(Screen parent) {
        super(Component.literal("Unlimited Trial Vaults Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.addRenderableWidget(booleanButton("normal_vault_unlimited", "Normal Vaults Unlimited",
                centerX, START_Y));
        this.addRenderableWidget(booleanButton("ominous_vault_unlimited", "Ominous Vaults Unlimited",
                centerX, START_Y + SPACING));

        // Number field (not a slider): exact values matter here, especially 0 = instant.
        EditBox delayField = new EditBox(this.font, centerX - 100, START_Y + SPACING * 2 + 12, 200, 20,
                Component.literal("Spawner Re-Challenge Delay"));
        delayField.setMaxLength(7);
        delayField.setValue(String.valueOf(ModConfig.get().spawner_cooldown_seconds));
        delayField.setResponder(text -> {
            try {
                // set() clamps to the valid range; invalid partial input keeps the old value.
                ModConfig.set("spawner_cooldown_seconds", String.valueOf(Integer.parseInt(text.trim())));
            } catch (NumberFormatException ignored) {
            }
        });
        this.addRenderableWidget(delayField);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button ->
                        this.minecraft.setScreen(this.parent))
                .bounds(centerX - 100, this.height - 30, 200, 20)
                .build());
    }

    // toggle button that flips the named boolean config key and saves it
    private Button booleanButton(String key, String label, int centerX, int y) {
        boolean current = readBool(key);
        return Button.builder(toggleText(label, current), button -> {
                    boolean next = !readBool(key);
                    ModConfig.set(key, String.valueOf(next));
                    button.setMessage(toggleText(label, next));
                })
                .bounds(centerX - 100, y, 200, 20)
                .build();
    }

    private static boolean readBool(String key) {
        ModConfig c = ModConfig.get();
        return switch (key) {
            case "normal_vault_unlimited" -> c.normal_vault_unlimited;
            case "ominous_vault_unlimited" -> c.ominous_vault_unlimited;
            default -> false;
        };
    }

    private static Component toggleText(String prefix, boolean value) {
        return Component.literal(prefix + ": " + (value ? "ON" : "OFF"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        graphics.centeredText(this.font,
                Component.literal("Spawner Re-Challenge Delay (seconds): -1 = vanilla, 0 = instant"),
                this.width / 2, START_Y + SPACING * 2, 0xA0A0A0);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
