package net.fayber.unlimitedtrialvaults;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// mod entrypoint - just loads config and registers the command, the actual
// vault/spawner behavior all lives in the mixins (VaultBlockEntityMixin,
// VaultServerDataMixin, VaultSharedDataMixin, TrialSpawnerMixin)
public class UnlimitedTrialVaults implements ModInitializer {
    public static final String MOD_ID = "unlimited_trial_vaults";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                UnlimitedTrialVaultsCommands.register(dispatcher));

        LOGGER.info("Unlimited Trial Vaults {} initialized. Config: {}", version(), ModConfig.get());
    }

    // read from the mod container so it can't drift out of sync with gradle.properties
    private static String version() {
        return FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("?");
    }
}
