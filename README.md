# Unlimited Trial Vaults

A server-side Fabric mod for Minecraft **26.1.x** (26.1, 26.1.1, 26.1.2).

Two behavior tweaks for the Tricky Trials content:

1. **Unlimited vault unlocks** - by default ominous vaults (and optionally normal vaults)
   can be unlocked by each player as many times as they like. Vanilla locks every player
   out after one unlock per vault. Trial keys are still consumed on every unlock.
2. **Configurable spawner re-challenge delay** - how long trial chamber spawners stay in
   cooldown after a challenge is configurable: shorter, longer, instant re-trigger, or
   untouched vanilla timing.

## Install

Drop the jar from `build/libs/` into your `mods/` folder. Requires Fabric Loader,
Fabric API, and Java 25 (bundled with MC 26.1+). Works on dedicated servers without
installing anything on clients.

## Configuration

`config/unlimited_trial_vaults.json` (created on first start):

```json
{
  "normal_vault_unlimited": false,
  "ominous_vault_unlimited": true,
  "spawner_cooldown_seconds": -1
}
```

| Key | Default | Meaning |
|---|---|---|
| `normal_vault_unlimited` | `false` | Normal vaults can be unlocked unlimited times per player |
| `ominous_vault_unlimited` | `true` | Ominous vaults can be unlocked unlimited times per player |
| `spawner_cooldown_seconds` | `-1` | Trial spawner re-challenge delay: `-1` vanilla (~30 min), `0` instantly re-triggerable, `N` N seconds |

### In game

Admins (permission level 2+) can edit live:

```
/unlimited_trial_vaults config get
/unlimited_trial_vaults config set ominous_vault_unlimited true
/unlimited_trial_vaults config set spawner_cooldown_seconds 60
```

Changes apply immediately and persist to disk. With ModMenu installed there is also a
config screen in the Mods menu (nicer Cloth Config screen when that is installed too).

## How it works

Two small mixins:

- `VaultBlockEntityMixin` hooks the "already rewarded?" check inside the vault's key
  insert logic. When unlimited is enabled for a vault type, the check always answers
  "not rewarded", so vanilla runs its normal first-unlock flow (consume key, roll loot,
  eject). Nothing else about vault behavior changes.
- `TrialSpawnerMixin` replaces the value returned by `TrialSpawner#getTargetCooldownLength()`.
  All of the spawner state machine's cooldown bookkeeping flows through that method, so
  vanilla keeps handling timers, particles and transitions.

## Building

```
./gradlew build
```

Needs JDK 25; `gradle.properties` pins `org.gradle.java.home` to a local Temurin install
(adjust or remove on other machines). Output lands in `build/libs/`.

## Manual test checklist

Headless CI cannot click vaults, so verify these once in game:

1. Ominous vault: unlock twice with ominous trial keys - both eject loot (default config).
2. Normal vault: second attempt plays the reject sound (default config).
3. `/unlimited_trial_vaults config set spawner_cooldown_seconds 0`, complete a trial:
   the spawner is immediately challengeable again.
4. `/unlimited_trial_vaults config set spawner_cooldown_seconds -1`: back to ~30 min cooldown.
5. Config screen via ModMenu reflects and saves all three values.
