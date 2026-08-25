package net.fayber.unlimitedtrialvaults.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.fayber.unlimitedtrialvaults.ModConfig;
import net.fayber.unlimitedtrialvaults.UnlimitedTrialVaults;
import net.fayber.unlimitedtrialvaults.VaultContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Neutralizes the once-per-player rule at METHOD level instead of at the call
 * sites inside tryInsertKey.
 *
 * Why method level: other mods (e.g. Carpet TIS Addition's vaultBlacklistDisabled
 * rule) hook the same call sites / expressions we did before 1.0.4, and stacked
 * injections on one bytecode slot silently misbehave. Hooking the callee methods
 * themselves cannot collide with anything: whatever happens at the caller side,
 * these bodies always run when invoked.
 *
 * For unlimited vault types:
 * - hasRewardedPlayer is forced false, so tryInsertKey's gate always passes and
 *   any "already unlocked" UI treats the player as eligible.
 * - addToRewardedPlayers is cancelled, so the player never enters rewardedPlayers
 *   in the first place (also stops the 128-entry list from growing).
 */
@Mixin(VaultServerData.class)
public abstract class VaultServerDataMixin {

	@Inject(method = "hasRewardedPlayer", at = @At("HEAD"), cancellable = true)
	private void unlimitedTrialVaults$notRewarded(Player player, CallbackInfoReturnable<Boolean> cir) {
		ModConfig cfg = ModConfig.get();
		boolean unlimited = VaultContext.isOminous((VaultServerData) (Object) this)
				? cfg.ominous_vault_unlimited
				: cfg.normal_vault_unlimited;
		if (unlimited) {
			if (cfg.debug()) {
				UnlimitedTrialVaults.LOGGER.info("[UTV] hasRewardedPlayer({}) forced false (unlimited)",
						player.getName().getString());
			}
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "addToRewardedPlayers", at = @At("HEAD"), cancellable = true)
	private void unlimitedTrialVaults$skipRewardedMark(Player player, CallbackInfo ci) {
		ModConfig cfg = ModConfig.get();
		boolean unlimited = VaultContext.isOminous((VaultServerData) (Object) this)
				? cfg.ominous_vault_unlimited
				: cfg.normal_vault_unlimited;
		if (unlimited) {
			if (cfg.debug()) {
				UnlimitedTrialVaults.LOGGER.info("[UTV] addToRewardedPlayers({}) skipped (unlimited)",
						player.getName().getString());
			}
			ci.cancel();
		}
	}
}
