package com.example.novaclient.module.modules.combat;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.RotationUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SurroundBreaker extends Module {
    private final float range = 5.0f;
    private final boolean rotate = true;
    private final int breakDelay = 1;
    private int breakTimer = 0;

    public SurroundBreaker() {
        super("SurroundBreaker", "Breaks obsidian blocks in enemy surrounds", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onDisable() {
        breakTimer = 0;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        breakTimer++;
        if (breakTimer < breakDelay) return;
        breakTimer = 0;

        PlayerEntity target = findTarget();
        if (target == null) return;

        List<BlockPos> surroundBlocks = getSurroundBlocks(BlockPos.ofFloored(target.getPos()));
        if (surroundBlocks.isEmpty()) return;

        surroundBlocks.sort(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5)));

        BlockPos best = surroundBlocks.get(0);

        if (rotate) {
            float[] rot = RotationUtil.getRotations(best, mc.player.getEyePos());
            NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
        }

        mc.interactionManager.updateBlockBreakingProgress(best, Direction.UP);
    }

    private List<BlockPos> getSurroundBlocks(BlockPos center) {
        List<BlockPos> list = new ArrayList<>();
        for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockPos pos = center.offset(dir);
            if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN ||
                mc.world.getBlockState(pos).getBlock() == Blocks.NETHERITE_BLOCK) {
                double dist = mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                if (dist <= range * range) {
                    list.add(pos);
                }
            }
        }
        return list;
    }

    private PlayerEntity findTarget() {
        if (mc.world == null) return null;
        PlayerEntity best = null;
        double bestDist = range;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (NovaClient.getInstance().getFriendManager().isFriend(player.getName().getString())) continue;
            double dist = mc.player.distanceTo(player);
            if (dist < bestDist) {
                bestDist = dist;
                best = player;
            }
        }
        return best;
    }
}