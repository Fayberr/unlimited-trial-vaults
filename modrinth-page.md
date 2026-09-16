# Unlimited Trial Vaults - Modrinth page copy

Source of truth for the text fields on https://modrinth.com/mod/unlimited-trial-vaults (project id `64tRO4M1`).
Update here first, then run `modrinth copy unlimited-trial-vaults <path-to-this-file>`.

## description

Allows ominous (and normal) trial vaults to be opened repeatedly with keys, plus configurable spawner cooldowns.

## body

# Unlimited Trial Vaults

A lightweight server-side mod for Minecraft 26.1.x and 26.2 that tweaks Tricky Trials vaults and spawners.

## Features

- **Repeatable Vaults:** Ominous vaults no longer lock you out after one payout. Each player can unlock them as many times as they want, consuming a trial key each time. Normal vaults can also be toggled on in the config if you want them repeatable too.
- **Trial Spawner Cooldowns:** Configure how long trial spawners stay in cooldown after a challenge is completed. Keep vanilla timing (~30 minutes), set an instant re-trigger, or choose any custom delay in seconds.

## Commands & Config

Admins (permission level 2+) can adjust settings live in-game:
- `/unlimited_trial_vaults config` shows the current configuration
- `/unlimited_trial_vaults config set ominous_vault_unlimited true`
- `/unlimited_trial_vaults config set normal_vault_unlimited false`
- `/unlimited_trial_vaults config set spawner_cooldown_seconds 0` (0 = instant re-challenge)

All options are saved in `config/unlimited_trial_vaults.json`. In singleplayer, they can also be adjusted directly from the Mods screen if Mod Menu is present (with Cloth Config supported as well).

## Compatibility & Notes

- **Server-side only:** Clients do not need to install the mod to join a server running it.
- Works in singleplayer worlds as well.
