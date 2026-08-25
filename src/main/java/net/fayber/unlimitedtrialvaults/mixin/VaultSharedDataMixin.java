package net.fayber.unlimitedtrialvaults.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.UUID;
import net.fayber.unlimitedtrialvaults.ModConfig;
import net.fayber.unlimitedtrialvaults.UnlimitedTrialVaults;
import net.fayber.unlimitedtrialvaults.VaultContext;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Neutralizes the ACTIVATION half of the once-per-player rule.
 *
 * Vanilla's updateConnectedPlayersWithinRange filters detected players through
 * lambda$updateConnectedPlayersWithinRange$0 = "!rewardedPlayers.contains(uuid)".
 * A rewarded player is never "connected", so the vault stays INACTIVE forever
 * for them and canEjectReward blocks every key insert silently. Bypassing only
 * the insert gate (VaultBlockEntityMixin) is not enough.
 *
 * When the vault type is unlimited, contains() is forced false, so previously
 * rewarded players (including unlocks made before installing the mod) count as
 * eligible again. Vault types without unlimited keep exact vanilla behavior.
 */
@Mixin(VaultSharedData.class)
public abstract class VaultSharedDataMixin {

	@ModifyExpressionValue(
			method = "lambda$updateConnectedPlayersWithinRange$0",
			at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z")
	)
	private static boolean unlimitedTrialVaults$allowRewardedToActivate(boolean original,
			VaultServerData serverData, UUID uuid) {
		ModConfig cfg = ModConfig.get();
		boolean ominous = VaultContext.isOminous(serverData);
		boolean unlimited = ominous ? cfg.ominous_vault_unlimited : cfg.normal_vault_unlimited;
		boolean result = original && !unlimited;
		if (cfg.debug()) {
			UnlimitedTrialVaults.LOGGER.info("[UTV] activation filter: contained={} ominous={} unlimited={} -> eligible={}",
					original, ominous, unlimited, !result);
		}
		return result;
	}
}
