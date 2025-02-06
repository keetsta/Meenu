package me.keet.meenu.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.keet.meenu.Meenu;
import me.keet.meenu.client.PlayerStatus;
import me.keet.meenu.networking.RenderStateUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

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
                float x = (float) (coordinate.getX() - cameraPos.x);
                float y = (float) (coordinate.getY() - cameraPos.y) + 0.5f;
                float z = (float) (coordinate.getZ() - cameraPos.z);

                matrices.translate(x - 0.3f, y + 0.7f, z + 0.5f);

                GL11.glDisable(GL11.GL_CULL_FACE);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

                Matrix4f matrix = Objects.requireNonNull(context.matrixStack()).peek().getPositionMatrix();

                RenderSystem.setShaderTexture(0, playerStatus.texturePath);
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);

                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(60));

                buffer.vertex(matrix, 0, 0, 0).texture(1, 1);
                buffer.vertex(matrix, 0, 0, 0.5f).texture(1, 0);
                buffer.vertex(matrix, 0.5f, 0, 0.5f).texture(0, 0);
                buffer.vertex(matrix, 0.5f, 0, 0).texture(0, 1);

                BufferRenderer.drawWithGlobalProgram(buffer.end());

                GL11.glEnable(GL11.GL_CULL_FACE);

                matrices.pop();
            }
        });
    }
}
