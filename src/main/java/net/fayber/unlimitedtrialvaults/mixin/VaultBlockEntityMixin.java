package net.fayber.unlimitedtrialvaults.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fayber.unlimitedtrialvaults.ModConfig;
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

/**
 * Lifts the vanilla "each player can only unlock this vault once" rule for
 * configured vault types. Keys are still consumed on every unlock, and all
 * vanilla feedback (eject animation, sounds) plays unchanged.
 *
 * Hooking the {@code hasRewardedPlayer} call inside {@code tryInsertKey} keeps
 * the whole vanilla flow intact: when the gate answers "not rewarded yet",
 * vanilla proceeds exactly as it would for a first-time unlock.
 */
@Mixin(VaultBlockEntity.Server.class)
public abstract class VaultBlockEntityMixin {

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
		ModConfig cfg = ModConfig.get();
		boolean unlimited = state.getValue(VaultBlock.OMINOUS)
				? cfg.ominous_vault_unlimited
				: cfg.normal_vault_unlimited;
		return original && !unlimited;
	}
}
