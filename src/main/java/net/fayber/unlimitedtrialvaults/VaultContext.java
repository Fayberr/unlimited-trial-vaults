package net.fayber.unlimitedtrialvaults;

import java.util.WeakHashMap;
import net.minecraft.world.level.block.entity.vault.VaultServerData;

// maps a VaultServerData instance to its vault type (ominous or not).
// the activation filter (updateConnectedPlayersWithinRange) only gets the
// data object, never the BlockState, so we stash the ominous flag here from
// the tick where we do have it, and read it back later.
//
// weak map on purpose - entries just disappear once the block entity is
// gone, nothing to clean up manually. VaultServerData has no equals()
// override so this is identity-keyed, which is what we want.
public final class VaultContext {

    private static final WeakHashMap<VaultServerData, Boolean> OMINOUS = new WeakHashMap<>();

    private VaultContext() {}

    public static void record(VaultServerData data, boolean ominous) {
        OMINOUS.put(data, ominous);
    }

    // defaults to true (unlimited-allowed) on a cache miss - shouldn't really
    // happen since the tick loop records every loaded vault every tick, but
    // if it ever does miss we want to fail open toward the mod's own feature
    public static boolean isOminous(VaultServerData data) {
        return OMINOUS.getOrDefault(data, Boolean.TRUE);
    }
}
