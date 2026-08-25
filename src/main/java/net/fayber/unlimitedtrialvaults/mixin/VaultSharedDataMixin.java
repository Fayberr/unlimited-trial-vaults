package net.fayber.unlimitedtrialvaults.mixin;

import java.util.UUID;
import net.fayber.unlimitedtrialvaults.ModConfig;
import net.fayber.unlimitedtrialvaults.UnlimitedTrialVaults;
import net.fayber.unlimitedtrialvaults.VaultContext;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Neutralizes the ACTIVATION half of the once-per-player rule.
 *
 * Vanilla's updateConnectedPlayersWithinRange filters detected players through
 * lambda$updateConnectedPlayersWithinRange$0 = "!rewardedPlayers.contains(uuid)".
 * A rewarded player is never "connected", so the vault stays INACTIVE forever
 * for them and canEjectReward blocks every key insert silently.
 *
 * 1.0.3 and earlier injected INTO that lambda body (@ModifyExpressionValue on
 * its Set.contains call). That collides with other mods hooking the exact same
 * expression (Carpet TIS Addition's vaultBlacklistDisabled rule), so 1.0.4
 * instead cancels the whole lambda at HEAD: for unlimited vault types every
 * player counts as eligible, without touching any shared bytecode slot.
 * Vault types without unlimited fall through to vanilla behavior.
 */
@Mixin(VaultSharedData.class)
public abstract class VaultSharedDataMixin {

	@Inject(
			method = "lambda$updateConnectedPlayersWithinRange$0",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void unlimitedTrialVaults$allowRewardedToActivate(VaultServerData serverData, UUID uuid,
			CallbackInfoReturnable<Boolean> cir) {
		ModConfig cfg = ModConfig.get();
		boolean unlimited = VaultContext.isOminous(serverData)
				? cfg.ominous_vault_unlimited
				: cfg.normal_vault_unlimited;
		if (unlimited) {
			if (cfg.debug()) {
				UnlimitedTrialVaults.LOGGER.info("[UTV] activation filter({}): eligible=true (unlimited)", uuid);
			}
			cir.setReturnValue(true);
		}
	}
}
