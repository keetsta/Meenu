package me.keet.meenu.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.keet.meenu.Meenu;
import me.keet.meenu.client.PlayerStatus;
import me.keet.meenu.networking.RenderStateUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class MenuRender {
    static HashMap<Integer, PlayerStatus> statuses = new HashMap<>();

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(RenderStateUpdatePayload.ID, (payload, context) -> {
            statuses.put(payload.sender(), payload.playerStatus());
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register((context) -> {
            for (var data : statuses.entrySet()) {
                var id = data.getKey();
                var playerStatus = data.getValue();
                var coordinate = context.world().getEntityById(id);

                if (playerStatus == PlayerStatus.NONE || coordinate == null) {
                    continue;
                }

                var matrices = context.matrixStack();

                if (matrices == null) {
                    Meenu.LOGGER.error("Matrices fallback!");
                    return;
                }

                matrices.push();

                Vec3d cameraPos = context.camera().getPos();

                Vec3d lookVector = coordinate.getRotationVec(1.0F);
                double offsetDistance = 0.8;
                Vec3d renderPos = coordinate.getPos().add(lookVector.multiply(offsetDistance));

                float x = (float) (renderPos.getX() - cameraPos.x);
                float y = (float) (renderPos.getY() - cameraPos.y) + 1.5f;
                float z = (float) (renderPos.getZ() - cameraPos.z);

                matrices.translate(x, y, z);

                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-coordinate.getYaw()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(coordinate.getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

                GL11.glDisable(GL11.GL_CULL_FACE);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

                Matrix4f matrix = matrices.peek().getPositionMatrix();

                RenderSystem.setShaderTexture(0, playerStatus.texturePath);
                RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.7f);
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(true);

                float size = 0.25f;
                buffer.vertex(matrix, -size, -size, 0).texture(0, 1);
                buffer.vertex(matrix, -size, size, 0).texture(0, 0);
                buffer.vertex(matrix, size, size, 0).texture(1, 0);
                buffer.vertex(matrix, size, -size, 0).texture(1, 1);

                BufferRenderer.drawWithGlobalProgram(buffer.end());

                GL11.glEnable(GL11.GL_CULL_FACE);

                matrices.pop();
            }
        });
    }
}
