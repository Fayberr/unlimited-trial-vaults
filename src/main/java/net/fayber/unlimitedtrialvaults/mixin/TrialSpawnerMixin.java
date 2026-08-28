package net.fayber.unlimitedtrialvaults.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fayber.unlimitedtrialvaults.ModConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// makes the re-challenge delay of trial spawners configurable. vanilla reads
// the cooldown length from getTargetCooldownLength() whenever it arms the
// cooldown timer (state transition into COOLDOWN, not every tick), so just
// swapping the return value here is enough - vanilla still handles the timer
// bookkeeping, particles, state machine, all of that unchanged.
@Mixin(TrialSpawner.class)
public abstract class TrialSpawnerMixin {

	@ModifyReturnValue(method = "getTargetCooldownLength()I", at = @At("RETURN"))
	private int unlimitedTrialVaults$overrideCooldown(int original) {
		int seconds = ModConfig.get().spawner_cooldown_seconds;
		if (seconds < 0) {
			return original; // -1: leave the vanilla (or data pack) cooldown untouched
		}
		return seconds * 20; // 0 = instantly re-triggerable, N = N seconds
	}
}
