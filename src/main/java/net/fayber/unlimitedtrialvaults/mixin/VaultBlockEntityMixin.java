package net.fayber.unlimitedtrialvaults.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.state.BlockState;
import net.fayber.unlimitedtrialvaults.VaultContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Feeds vault type context ({@link VaultContext}) so the method-level hooks in
 * {@link VaultServerDataMixin} and {@link VaultSharedDataMixin} know whether a
 * given vault is ominous (the activation lambda and the VaultServerData methods
 * have no BlockState parameter of their own).
 *
 * Since 1.0.4 the insert gate and rewarded-mark logic live on
 * {@link VaultServerDataMixin} (method level) instead of at tryInsertKey call
 * sites - call-site injections collided with other mods hooking the same
 * expressions (Carpet TIS Addition's vaultBlacklistDisabled rule).
 */
@Mixin(VaultBlockEntity.Server.class)
public abstract class VaultBlockEntityMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	private static void unlimitedTrialVaults$recordContext(ServerLevel level, BlockPos pos, BlockState state,
			VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, CallbackInfo ci) {
		VaultContext.record(serverData, state.getValue(VaultBlock.OMINOUS));
	}
}
