package com.example.novaclient.util;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static BlockPos getPlayerBlockPos() {
        if (mc.player == null) return BlockPos.ORIGIN;
        return BlockPos.ofFloored(mc.player.getPos());
    }

    public static boolean isInRange(BlockPos pos, double range) {
        if (mc.player == null) return false;
        return mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= range * range;
    }

    public static boolean isHole(BlockPos pos) {
        if (mc.world == null) return false;
        if (!mc.world.getBlockState(pos).isAir()) return false;
        if (!mc.world.getBlockState(pos.up()).isAir()) return false;

        BlockPos[] sides = {
            pos.down(),
            pos.north(),
            pos.south(),
            pos.east(),
            pos.west()
        };

        for (BlockPos side : sides) {
            if (!mc.world.getBlockState(side).isOf(Blocks.BEDROCK) 
                && !mc.world.getBlockState(side).isOf(Blocks.OBSIDIAN)) {
                return false;
            }
        }

        return true;
    }

    public static Direction getPlaceSide(BlockPos pos) {
        if (mc.world == null) return Direction.UP;

        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            if (!mc.world.getBlockState(neighbor).isAir()) {
                return direction.getOpposite();
            }
        }

        return Direction.UP;
    }
}