package de.chestcolor;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class OpenedChests {
    private static final Set<String> OPENED = new HashSet<>();
    private static Path file;

    private OpenedChests() {}

    public static String dim(Level level) {
        return level.dimension().toString();
    }

    private static String key(String dim, BlockPos pos) {
        return dim + ";" + pos.asLong();
    }

    public static boolean isOpened(String dim, BlockPos pos) {
        return OPENED.contains(key(dim, pos));
    }

    public static void markOpened(String dim, BlockPos pos, BlockState state) {
        OPENED.add(key(dim, pos));
        // Doppelkiste: andere Hälfte ebenfalls markieren
        if (state.getBlock() instanceof ChestBlock
                && state.hasProperty(ChestBlock.TYPE)
                && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            OPENED.add(key(dim, pos.relative(ChestBlock.getConnectedDirection(state))));
        }
        save();
    }

    /** Lädt die Liste pro Welt bzw. pro Server. */
    public static void load(Minecraft client) {
        OPENED.clear();
        String name;
        if (client.getSingleplayerServer() != null) {
            name = "sp_" + client.getSingleplayerServer().getWorldData().getLevelName();
        } else if (client.getCurrentServer() != null) {
            name = "mp_" + client.getCurrentServer().ip;
        } else {
            name = "unknown";
        }
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        file = FabricLoader.getInstance().getConfigDir().resolve("chestcolor").resolve(name + ".txt");
        try {
            if (Files.exists(file)) OPENED.addAll(Files.readAllLines(file));
        } catch (IOException ignored) {}
    }

    public static void save() {
        if (file == null) return;
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, OPENED);
        } catch (IOException ignored) {}
    }
}
