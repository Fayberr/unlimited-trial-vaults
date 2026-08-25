package net.fayber.unlimitedtrialvaults.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fayber.unlimitedtrialvaults.ModConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Makes the re-challenge delay of ALL trial spawners configurable.
 *
 * Vanilla derives its cooldown duration from {@code getTargetCooldownLength()};
 * every state transition that arms the cooldown timer flows through this method,
 * so replacing its return value is sufficient - vanilla handles the rest
 * (timer bookkeeping, particles, state machine) unchanged.
 */
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
