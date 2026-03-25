package com.example.novaclient.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public class RenderUtil {

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
    }

    public static void endRender() {
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void drawEntityBox(MatrixStack matrices, Entity entity, float red, float green, float blue, float alpha) {
        Box box = entity.getBoundingBox();
        drawBox(matrices, box, red, green, blue, alpha);
    }

    public static void drawBlockBox(MatrixStack matrices, BlockPos pos, float red, float green, float blue, float alpha) {
        Box box = new Box(pos);
        drawBox(matrices, box, red, green, blue, alpha);
    }

    private static void drawBox(MatrixStack matrices, Box box, float red, float green, float blue, float alpha) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha);

        buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha);
        buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha);

        tessellator.draw();
    }
}