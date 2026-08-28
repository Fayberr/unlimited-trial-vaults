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

// this is where the actual "unlimited" behavior lives: we hook the two
// methods vanilla uses to track per-player unlocks, instead of the
// tryInsertKey call sites (that's what 1.0.3 and earlier did, and it broke
// when other mods hooked the same bytecode expressions - Carpet TIS
// Addition's vaultBlacklistDisabled rule being the one that bit us).
// hooking the methods themselves can't collide with anything since they
// always run whenever something calls them, no matter what the caller looks
// like.
//
// - hasRewardedPlayer forced false -> tryInsertKey's gate always passes
// - addToRewardedPlayers cancelled -> player never gets added to the
//   rewardedPlayers set in the first place, so it never grows for vaults
//   that are set to unlimited (bonus: no need to ever clean it up either)
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
