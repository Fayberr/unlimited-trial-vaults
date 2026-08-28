package net.fayber.unlimitedtrialvaults;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

// /unlimited_trial_vaults config [get|set <key> <value>] - live-edit the mod
// config in game, requires op. saves to disk immediately on every change.
public final class UnlimitedTrialVaultsCommands {
    private UnlimitedTrialVaultsCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("unlimited_trial_vaults")
                .then(Commands.literal("config")
                        .requires(UnlimitedTrialVaultsCommands::isAdmin)
                        .executes(ctx -> showConfig(ctx.getSource()))
                        .then(Commands.literal("get").executes(ctx -> showConfig(ctx.getSource())))
                        .then(Commands.literal("set")
                                .then(Commands.argument("key", StringArgumentType.word())
                                        .then(Commands.argument("value", StringArgumentType.word())
                                                .executes(ctx -> setConfig(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "key"),
                                                        StringArgumentType.getString(ctx, "value"))))))));
    }

    private static int showConfig(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("[Unlimited Trial Vaults] Config: "
                + ModConfig.get()), false);
        return 1;
    }

    private static int setConfig(CommandSourceStack source, String key, String value) {
        try {
            if (ModConfig.set(key, value)) {
                source.sendSuccess(() -> Component.literal("[Unlimited Trial Vaults] Set " + key
                        + " to " + value + ". New config: " + ModConfig.get()), true);
                return 1;
            }
        } catch (NumberFormatException e) {
            source.sendFailure(Component.literal("[Unlimited Trial Vaults] " + key
                    + " expects a number."));
            return 0;
        }
        source.sendFailure(Component.literal("[Unlimited Trial Vaults] Unknown config key '" + key
                + "'. Valid keys: normal_vault_unlimited, ominous_vault_unlimited,"
                + " spawner_cooldown_seconds."));
        return 0;
    }

    private static boolean isAdmin(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
    }
}
