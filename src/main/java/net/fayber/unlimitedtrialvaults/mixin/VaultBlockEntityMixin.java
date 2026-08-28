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

// just feeds VaultContext with the ominous flag every tick, since neither the
// activation lambda nor the VaultServerData methods we hook get a BlockState
// of their own to check that against.
//
// note: as of 1.0.4 we don't inject at the tryInsertKey call sites anymore
// (that's why this mixin looks so small now) - those injections collided
// with other mods hooking the same bytecode expressions, e.g. Carpet TIS
// Addition's vaultBlacklistDisabled rule. The actual unlock logic moved to
// method-level hooks in VaultServerDataMixin / VaultSharedDataMixin instead.
@Mixin(VaultBlockEntity.Server.class)
public abstract class VaultBlockEntityMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	private static void unlimitedTrialVaults$recordContext(ServerLevel level, BlockPos pos, BlockState state,
			VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, CallbackInfo ci) {
		VaultContext.record(serverData, state.getValue(VaultBlock.OMINOUS));
	}
}
