package de.chestcolor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.AbstractChestBlock;

public class ChestColorMod implements ClientModInitializer {
    public static final int RED = 0xFFFF8080;   // geöffnet
    public static final int GREEN = 0xFF80FF80; // ungeöffnet

    private static BlockPos pending;
    private static String pendingDim;
    private static long pendingSince;

    @Override
    public void onInitializeClient() {
        // Merken, welche Kiste angeklickt wurde
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (level.isClientSide() && level.getBlockState(hit.getBlockPos()).getBlock() instanceof AbstractChestBlock<?>) {
                pending = hit.getBlockPos().immutable();
                pendingDim = OpenedChests.dim(level);
                pendingSince = level.getGameTime();
            }
            return InteractionResult.PASS;
        });

        // Erst als "geöffnet" markieren, wenn das Inventar wirklich aufgeht
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pending == null || client.level == null) return;
            if (client.screen instanceof ContainerScreen) {
                OpenedChests.markOpened(pendingDim, pending, client.level.getBlockState(pending));
                pending = null;
            } else if (client.level.getGameTime() - pendingSince > 40) {
                pending = null; // Klick hat nichts geöffnet (z. B. blockiert)
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> OpenedChests.load(client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> OpenedChests.save());
    }
}
