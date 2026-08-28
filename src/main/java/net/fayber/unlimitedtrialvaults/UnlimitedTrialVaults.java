package net.fayber.unlimitedtrialvaults;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// mod entrypoint - just loads config and registers the command, the actual
// vault/spawner behavior all lives in the mixins (VaultBlockEntityMixin,
// VaultServerDataMixin, VaultSharedDataMixin, TrialSpawnerMixin)
public class UnlimitedTrialVaults implements ModInitializer {
    public static final String MOD_ID = "unlimited_trial_vaults";
    public static final String VERSION = "1.0.6";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                UnlimitedTrialVaultsCommands.register(dispatcher));

        LOGGER.info("Unlimited Trial Vaults {} initialized. Config: {}", VERSION, ModConfig.get());
    }
}
