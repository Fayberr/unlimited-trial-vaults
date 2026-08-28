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

// this handles the OTHER half of the once-per-player rule: activation.
// vanilla's updateConnectedPlayersWithinRange filters players through
// lambda$updateConnectedPlayersWithinRange$0, basically
// "!rewardedPlayers.contains(uuid)". if a player is already rewarded they're
// never counted as "connected", so the vault just sits INACTIVE for them
// forever and silently blocks every key insert - even with the
// VaultServerDataMixin changes above, this lambda would still lock them out.
//
// used to inject inside the lambda body with @ModifyExpressionValue on the
// Set.contains() call, but that stomped on the same expression Carpet TIS
// Addition hooks for its vaultBlacklistDisabled rule. now we just cancel the
// whole lambda at HEAD for unlimited vault types (everyone's eligible),
// which can't collide with anything since it doesn't touch shared bytecode.
// non-unlimited vault types just fall through to vanilla.
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
