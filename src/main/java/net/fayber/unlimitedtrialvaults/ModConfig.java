package net.fayber.unlimitedtrialvaults;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// mod config, lives at config/unlimited_trial_vaults.json
// defaults: both vault types unlimited, spawner cooldown untouched (-1 = vanilla)
public final class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("unlimited_trial_vaults.json");

    private static ModConfig INSTANCE = new ModConfig();

    public boolean normal_vault_unlimited = true;
    public boolean ominous_vault_unlimited = true;
    // -1 = vanilla cooldown untouched, 0 = instantly re-triggerable, N = N seconds
    public int spawner_cooldown_seconds = -1;
    // not exposed in config on purpose - flip this and rebuild a one-off jar
    // when actually debugging something. compiler strips the dead log calls
    // when false so it costs nothing in a normal build.
    static final boolean DEBUG = false;

    public boolean debug() {
        return DEBUG;
    }

    public static ModConfig get() {
        return INSTANCE;
    }

    public static void load() {
        ModConfig cfg = new ModConfig();
        if (Files.exists(PATH)) {
            try {
                String json = Files.readString(PATH);
                Raw raw = GSON.fromJson(json, Raw.class);
                if (raw != null) {
                    if (raw.normal_vault_unlimited != null) cfg.normal_vault_unlimited = raw.normal_vault_unlimited;
                    if (raw.ominous_vault_unlimited != null) cfg.ominous_vault_unlimited = raw.ominous_vault_unlimited;
                    if (raw.spawner_cooldown_seconds != null) cfg.spawner_cooldown_seconds = raw.spawner_cooldown_seconds;
                }
            } catch (Exception e) {
                UnlimitedTrialVaults.LOGGER.error("Failed to read config, using defaults", e);
            }
        }
        INSTANCE = cfg;
        save();
    }

    // test hook, swaps the active instance
    static void setForTesting(ModConfig config) {
        INSTANCE = config;
    }

    public static void save() {
        Raw raw = new Raw();
        raw.normal_vault_unlimited = INSTANCE.normal_vault_unlimited;
        raw.ominous_vault_unlimited = INSTANCE.ominous_vault_unlimited;
        raw.spawner_cooldown_seconds = INSTANCE.spawner_cooldown_seconds;
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(raw));
        } catch (IOException e) {
            UnlimitedTrialVaults.LOGGER.error("Failed to save config", e);
        }
    }

    // sets a key by name from the command or the config GUI, false if the key doesn't exist
    public static boolean set(String key, String value) {
        ModConfig c = INSTANCE;
        switch (key.toLowerCase()) {
            case "normal_vault_unlimited" -> c.normal_vault_unlimited = parseBool(value);
            case "ominous_vault_unlimited" -> c.ominous_vault_unlimited = parseBool(value);
            case "spawner_cooldown_seconds" -> c.spawner_cooldown_seconds =
                    Math.max(-1, Math.min(Integer.parseInt(value), 86_400));
            default -> {
                return false;
            }
        }
        save();
        return true;
    }

    private static boolean parseBool(String v) {
        return v.equalsIgnoreCase("true") || v.equals("1") || v.equalsIgnoreCase("yes");
    }

    @Override
    public String toString() {
        return "normal_vault_unlimited=" + normal_vault_unlimited
                + ", ominous_vault_unlimited=" + ominous_vault_unlimited
                + ", spawner_cooldown_seconds=" + spawner_cooldown_seconds;
    }

    // json shape on disk - boxed types so a missing key just stays null and load() skips it
    private static class Raw {
        Boolean normal_vault_unlimited;
        Boolean ominous_vault_unlimited;
        Integer spawner_cooldown_seconds;
    }
}
