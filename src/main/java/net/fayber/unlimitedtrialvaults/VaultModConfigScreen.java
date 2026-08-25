package net.fayber.unlimitedtrialvaults;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Hand-rolled ModMenu config screen. Every control writes through
 * {@link ModConfig#set(String, String)}, which updates the in-memory config
 * and saves it to {@code config/unlimited_trial_vaults.json}. In singleplayer
 * the integrated server shares the same static config, so changes apply live.
 */
public class VaultModConfigScreen extends Screen {
    /** Slider top end, in seconds. */
    private static final int MAX_SPAWNER_COOLDOWN_SECONDS = 3600;

    private final Screen parent;

    public VaultModConfigScreen(Screen parent) {
        super(Component.literal("Unlimited Trial Vaults Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int spacing = 22;
        int startY = 25;

        this.addRenderableWidget(booleanButton("normal_vault_unlimited", "Normal Vaults Unlimited",
                centerX, startY));
        this.addRenderableWidget(booleanButton("ominous_vault_unlimited", "Ominous Vaults Unlimited",
                centerX, startY + spacing));

        int seconds = ModConfig.get().spawner_cooldown_seconds;
        this.addRenderableWidget(new AbstractSliderButton(centerX - 100, startY + spacing * 2, 200, 20,
                cooldownLabel(seconds), ((double) seconds + 1) / (MAX_SPAWNER_COOLDOWN_SECONDS + 1)) {
            @Override
            protected void updateMessage() {
                this.setMessage(cooldownLabel(ModConfig.get().spawner_cooldown_seconds));
            }

            @Override
            protected void applyValue() {
                int secs = (int) Math.round(this.value * (MAX_SPAWNER_COOLDOWN_SECONDS + 1)) - 1;
                ModConfig.set("spawner_cooldown_seconds", String.valueOf(secs));
                this.setMessage(cooldownLabel(secs));
            }
        });

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button ->
                        this.minecraft.setScreen(this.parent))
                .bounds(centerX - 100, this.height - 30, 200, 20)
                .build());
    }

    /** A toggle that flips the named boolean config key and saves it. */
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

    private static Component cooldownLabel(int secs) {
        if (secs < 0) return Component.literal("Spawner Re-Challenge Delay: vanilla");
        if (secs == 0) return Component.literal("Spawner Re-Challenge Delay: instant");
        return Component.literal("Spawner Re-Challenge Delay: " + secs + "s");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
