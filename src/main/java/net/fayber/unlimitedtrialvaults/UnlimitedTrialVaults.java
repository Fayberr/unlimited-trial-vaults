package net.fayber.unlimitedtrialvaults;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrypoint for Unlimited Trial Vaults.
 *
 * Loads the config, registers the {@code /unlimited_trial_vaults} command.
 * The actual behavior changes live in the mixins:
 * - VaultBlockEntityMixin: unlimited per-player unlocks per vault type.
 * - TrialSpawnerMixin: configurable trial spawner re-challenge delay.
 */
public class UnlimitedTrialVaults implements ModInitializer {
    public static final String MOD_ID = "unlimited_trial_vaults";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                UnlimitedTrialVaultsCommands.register(dispatcher));

        LOGGER.info("Unlimited Trial Vaults initialized. Config: {}", ModConfig.get());
    }
}
