package com.example.novaclient.module.modules.combat;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.BlockUtil;
import com.example.novaclient.util.InventoryUtil;
import com.example.novaclient.util.RotationUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CevBreaker extends Module {
    private final float range = 5.0f;
    private final boolean rotate = true;
    private final boolean placeCrystal = true;
    private final int tickDelay = 2;
    private int tickCounter = 0;

    public CevBreaker() {
        super("CevBreaker", "Breaks ceiling obsidian above targets for crystal damage", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onDisable() {
        tickCounter = 0;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        tickCounter++;
        if (tickCounter < tickDelay) return;
        tickCounter = 0;

        PlayerEntity target = findTarget();
        if (target == null) return;

        List<BlockPos> cevBlocks = findCevTargets(BlockPos.ofFloored(target.getPos()));
        if (cevBlocks.isEmpty()) return;

        cevBlocks.sort(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5)));

        BlockPos best = cevBlocks.get(0);

        if (rotate) {
            float[] rot = RotationUtil.getRotations(best, mc.player.getEyePos());
            NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
        }

        if (placeCrystal) {
            int crystalSlot = InventoryUtil.findItemInHotbar(Items.END_CRYSTAL);
            if (crystalSlot != -1) {
                int prevSlot = mc.player.getInventory().selectedSlot;
                InventoryUtil.switchToSlot(crystalSlot);

                BlockPos placeOn = best.down();
                if (mc.world.getBlockState(placeOn).getBlock() == Blocks.OBSIDIAN ||
                    mc.world.getBlockState(placeOn).getBlock() == Blocks.BEDROCK) {
                    mc.interactionManager.interactBlock(
                        mc.player, Hand.MAIN_HAND,
                        new BlockHitResult(Vec3d.ofCenter(placeOn).add(0, 0.5, 0), Direction.UP, placeOn, false)
                    );
                }

                InventoryUtil.switchToSlot(prevSlot);
            }
        }

        mc.interactionManager.updateBlockBreakingProgress(best, Direction.DOWN);
    }

    private List<BlockPos> findCevTargets(BlockPos targetPos) {
        List<BlockPos> list = new ArrayList<>();
        for (int height = 1; height <= 3; height++) {
            BlockPos above = targetPos.up(height);
            if (mc.world.getBlockState(above).getBlock() == Blocks.OBSIDIAN ||
                mc.world.getBlockState(above).getBlock() == Blocks.NETHERITE_BLOCK) {
                double dist = mc.player.squaredDistanceTo(above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5);
                if (dist <= range * range) {
                    list.add(above);
                    break;
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