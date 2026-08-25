package net.fayber.unlimitedtrialvaults.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fayber.unlimitedtrialvaults.ModConfig;
import net.fayber.unlimitedtrialvaults.UnlimitedTrialVaults;
import net.fayber.unlimitedtrialvaults.VaultContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Lifts the vanilla "each player can only unlock this vault once" rule for
 * configured vault types. Keys are still consumed on every unlock, and all
 * vanilla feedback (eject animation, sounds) plays unchanged.
 *
 * Vanilla enforces the rule in TWO places, both handled here:
 *
 * 1. tryInsertKey's hasRewardedPlayer gate (plays the reject sound) - bypassed
 *    when the vault type is unlimited.
 * 2. VaultSharedData's activation filter, which removes rewarded players from
 *    connectedPlayers so the vault never leaves INACTIVE state (handled in
 *    {@link VaultSharedDataMixin}, which needs this class to record the vault
 *    type via {@link VaultContext}).
 *
 * Additionally, when a vault type is unlimited, the player's UUID is no longer
 * added to rewardedPlayers at all, so the activation filter passes naturally
 * for future unlocks.
 */
@Mixin(VaultBlockEntity.Server.class)
public abstract class VaultBlockEntityMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	private static void unlimitedTrialVaults$recordContext(ServerLevel level, BlockPos pos, BlockState state,
			VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, CallbackInfo ci) {
		VaultContext.record(serverData, state.getValue(VaultBlock.OMINOUS));
	}

	@ModifyExpressionValue(
			method = "tryInsertKey",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/entity/vault/VaultServerData;hasRewardedPlayer(Lnet/minecraft/world/entity/player/Player;)Z"
			)
	)
	private static boolean unlimitedTrialVaults$allowRepeatedUnlocks(boolean original,
			ServerLevel level, BlockPos pos, BlockState state, VaultConfig config,
			VaultServerData serverData, VaultSharedData sharedData, Player player, ItemStack keyStack) {
		VaultContext.record(serverData, state.getValue(VaultBlock.OMINOUS));
		ModConfig cfg = ModConfig.get();
		boolean unlimited = state.getValue(VaultBlock.OMINOUS)
				? cfg.ominous_vault_unlimited
				: cfg.normal_vault_unlimited;
		if (cfg.debug()) {
			UnlimitedTrialVaults.LOGGER.info("[UTV] insert gate at {}: alreadyRewarded={}, unlimited={} -> gate={}",
					pos.toShortString(), original, unlimited, original && !unlimited);
		}
		return original && !unlimited;
	}

	@Inject(
			method = "tryInsertKey",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/entity/vault/VaultServerData;addToRewardedPlayers(Lnet/minecraft/world/entity/player/Player;)V"
			),
			cancellable = true
	)
	private static void unlimitedTrialVaults$skipRewardedMark(ServerLevel level, BlockPos pos, BlockState state,
			VaultConfig config, VaultServerData serverData, VaultSharedData sharedData,
			Player player, ItemStack keyStack, CallbackInfo ci) {
		ModConfig cfg = ModConfig.get();
		boolean unlimited = state.getValue(VaultBlock.OMINOUS)
				? cfg.ominous_vault_unlimited
				: cfg.normal_vault_unlimited;
		if (unlimited) {
			// Never record the player as rewarded: keeps the activation filter
			// (VaultSharedData) passing and stops the 128-entry list from growing.
			if (ModConfig.get().debug()) {
				UnlimitedTrialVaults.LOGGER.info("[UTV] skipping rewarded-mark at {} for {}",
						pos.toShortString(), player.getName().getString());
			}
			ci.cancel();
		}
	}
}
