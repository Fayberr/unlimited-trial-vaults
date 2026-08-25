package net.fayber.unlimitedtrialvaults;

import java.util.WeakHashMap;
import net.minecraft.world.level.block.entity.vault.VaultServerData;

/**
 * Links VaultServerData instances to their vault type (ominous or not).
 *
 * Needed because the activation filter ({@code updateConnectedPlayersWithinRange})
 * only receives the data object, never the BlockState, so the ominous flag has to
 * be remembered from a context that has it (the server tick / key insert).
 *
 * WeakHashMap: entries vanish with the block entity, no leaks. VaultServerData
 * uses identity semantics (no equals override), which is what we want.
 */
public final class VaultContext {

    private static final WeakHashMap<VaultServerData, Boolean> OMINOUS = new WeakHashMap<>();

    private VaultContext() {}

    public static void record(VaultServerData data, boolean ominous) {
        OMINOUS.put(data, ominous);
    }

    /**
     * true = treat as ominous. On a miss this defaults to "unlimited allowed",
     * which is the feature-preserving direction; misses should not happen in
     * practice because the tick loop records every loaded vault continuously.
     */
    public static boolean isOminous(VaultServerData data) {
        return OMINOUS.getOrDefault(data, Boolean.TRUE);
    }
}
